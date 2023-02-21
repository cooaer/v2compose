package io.github.v2compose.repository.def

import io.github.v2compose.datasource.Account
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.network.OkHttpFactory
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.HomePageInfo
import io.github.v2compose.network.bean.LoginParam
import io.github.v2compose.network.bean.TwoStepLoginInfo
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultAccountRepository @Inject constructor(
    private val v2ExService: V2exService,
    private val appPreferences: AppPreferences,
) : AccountRepository {

    companion object {
        private const val TAG = "DefaultAccountRepository"
    }

    override val account: Flow<Account>
        get() = appPreferences.account

    override val isLoggedIn: Flow<Boolean>
        get() = appPreferences.account.map { it.isValid() }

    override suspend fun getLoginParam(): LoginParam {
        return v2ExService.loginParam()
    }

    override suspend fun login(loginParams: Map<String, String>): LoginParam {
        return v2ExService.login(loginParams)
    }

    override suspend fun getTwoStepLoginInfo(): TwoStepLoginInfo {
        return v2ExService.twoStepLogin()
    }

    override suspend fun loginNextStep(once: String, code: String): TwoStepLoginInfo {
        return v2ExService.signInTwoStep(mapOf("once" to once, "code" to code))
    }

    override suspend fun logout(): Boolean {
        appPreferences.account(Account.Empty)
        OkHttpFactory.cookieJar.clearCookies()
        return true
    }

    override suspend fun updateLocalUserInfo(
        userName: String?,
        userAvatar: String?,
        description: String?,
        nodes: Int?,
        topics: Int?,
        following: Int?
    ) {
        val current = appPreferences.account.first()
        appPreferences.account(
            current.copy(
                userName = userName ?: current.userName,
                userAvatar = userAvatar ?: current.userAvatar,
                description = description ?: current.description,
                nodes = nodes ?: current.nodes,
                topics = topics ?: current.topics,
                following = following ?: current.following,
            )
        )
    }

    override suspend fun getHomePageInfo(): HomePageInfo {
        return v2ExService.homePageInfo(account.first().userName)
    }

    override suspend fun fetchUserInfo() {
        val dailyInfo = v2ExService.dailyInfo()
        if (dailyInfo.isValid) {
            updateLocalUserInfo(userName = dailyInfo.userName, userAvatar = dailyInfo.avatar)
        }
    }

    override suspend fun refreshAccount() {
        val userName = account.first().userName
        val homePageInfo = v2ExService.homePageInfo(userName)
        val userInfo = v2ExService.userInfo(userName)
        updateLocalUserInfo(
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