package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.datasource.Account
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.datasource.NotificationPagingSource
import io.github.v2compose.network.OkHttpFactory
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.HomePageInfo
import io.github.v2compose.network.bean.LoginParam
import io.github.v2compose.network.bean.NotificationInfo
import io.github.v2compose.network.bean.TwoStepLoginInfo
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultAccountRepository @Inject constructor(
    private val v2exService: V2exService,
    private val appPreferences: AppPreferences,
) : AccountRepository {

    companion object {
        private const val TAG = "DefaultAccountRepository"
    }

    override val account: Flow<Account>
        get() = appPreferences.account

    override val isLoggedIn: Flow<Boolean>
        get() = appPreferences.account.map { it.isValid() }

    override val unreadNotifications: Flow<Int>
        get() = appPreferences.account.map { it.unreadNotifications }

    override fun getNotifications(): Flow<PagingData<NotificationInfo.Reply>> {
        return Pager(
            PagingConfig(pageSize = 20, enablePlaceholders = false)
        ) { NotificationPagingSource(v2exService, appPreferences) }.flow
    }

    override suspend fun resetNotificationCount() {
        return appPreferences.updateAccount(unreadNotifications = 0)
    }

    override suspend fun getLoginParam(): LoginParam {
        return v2exService.loginParam()
    }

    override suspend fun login(loginParams: Map<String, String>): LoginParam {
        return v2exService.login(loginParams)
    }

    override suspend fun getTwoStepLoginInfo(): TwoStepLoginInfo {
        return v2exService.twoStepLogin()
    }

    override suspend fun loginNextStep(once: String, code: String): TwoStepLoginInfo {
        return v2exService.signInTwoStep(mapOf("once" to once, "code" to code))
    }

    override suspend fun logout(): Boolean {
        appPreferences.account(Account.Empty)
        OkHttpFactory.cookieJar.clearCookies()
        return true
    }

    override suspend fun getHomePageInfo(): HomePageInfo {
        return v2exService.homePageInfo(account.first().userName)
    }

    override suspend fun fetchUserInfo() {
        val dailyInfo = v2exService.dailyInfo()
        if (dailyInfo.isValid) {
            appPreferences.updateAccount(
                userName = dailyInfo.userName,
                userAvatar = dailyInfo.avatar
            )
        }
    }

    override suspend fun refreshAccount() {
        val userName = account.first().userName
        val homePageInfo = v2exService.homePageInfo(userName)
        val userInfo = v2exService.userInfo(userName)
        appPreferences.updateAccount(
            userName = homePageInfo.userName,
            userAvatar = userInfo.largestAvatar,
            description = homePageInfo.desc,
            nodes = homePageInfo.nodes,
            topics = homePageInfo.topics,
            following = homePageInfo.following,
        )
    }

    override suspend fun signIn() {

    }

}