package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.bean.Account
import io.github.v2compose.datasource.AccountPreferences
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.datasource.AppStateStore
import io.github.v2compose.datasource.NotificationPagingSource
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.WebkitCookieManager
import io.github.v2compose.network.bean.*
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultAccountRepository @Inject constructor(
    private val v2exService: V2exService,
    private val appPreferences: AppPreferences,
    private val accountPreferences: AccountPreferences,
    private val cookieManager: WebkitCookieManager,
    private val appStateStore: AppStateStore,
) : AccountRepository {

    companion object {
        private const val TAG = "DefaultAccountRepository"
    }

    override val account: Flow<Account>
        get() = accountPreferences.account

    override val isLoggedIn: Flow<Boolean>
        get() = accountPreferences.account.map { it.isValid() }

    override val unreadNotifications: Flow<Int>
        get() = accountPreferences.unreadNotifications

    override fun getNotifications(): Flow<PagingData<NotificationInfo.Reply>> {
        return Pager(
            PagingConfig(pageSize = 20, enablePlaceholders = false)
        ) { NotificationPagingSource(v2exService, accountPreferences) }.flow
    }

    override suspend fun resetNotificationCount() {
        return accountPreferences.unreadNotifications(0)
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
        accountPreferences.clear()
        cookieManager.clearCookies()
        return true
    }

    override suspend fun getHomePageInfo(): HomePageInfo {
        return v2exService.homePageInfo(account.first().userName)
    }

    override suspend fun fetchUserInfo() {
        val dailyInfo = v2exService.dailyInfo()
        if (dailyInfo.isValid) {
            accountPreferences.updateAccount(
                userName = dailyInfo.userName,
                userAvatar = dailyInfo.avatar
            )
        }
    }

    override suspend fun refreshAccount() = coroutineScope {
        val userName = account.first().userName
        val homePageInfoDeferred = async { v2exService.homePageInfo(userName) }
        val userInfoDeferred = async { v2exService.userInfo(userName) }
        val homePageInfo = homePageInfoDeferred.await()
        val userInfo = userInfoDeferred.await()

        accountPreferences.updateAccount(
            userName = homePageInfo.userName,
            userAvatar = userInfo.largestAvatar,
            description = homePageInfo.desc,
            nodes = homePageInfo.nodes,
            topics = homePageInfo.topics,
            following = homePageInfo.following,
        )
    }

    override val hasCheckingInTips: Flow<Boolean> = appStateStore.hasCheckingInTips

    override val autoCheckIn: Flow<Boolean> = appPreferences.appSettings.map { it.autoCheckIn }

    override val lastCheckInTime: Flow<Long> = accountPreferences.lastCheckInTime

    override suspend fun dailyInfo(): DailyInfo {
        return v2exService.dailyInfo().also {
            appStateStore.updateHasCheckingInTips(!it.hadCheckedIn())
            if (it.hadCheckedIn()) {
                accountPreferences.lastCheckInTime(System.currentTimeMillis())
            }
        }
    }

    override suspend fun checkIn(once: String): DailyInfo {
        return v2exService.checkIn(once).also {
            appStateStore.updateHasCheckingInTips(!it.hadCheckedIn())
            accountPreferences.lastCheckInTime(System.currentTimeMillis())
        }
    }

}