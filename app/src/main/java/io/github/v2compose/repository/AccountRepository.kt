package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.bean.Account
import io.github.v2compose.network.bean.*
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    val account: Flow<Account>

    val isLoggedIn: Flow<Boolean>

    val unreadNotifications: Flow<Int>

    fun getNotifications(): Flow<PagingData<NotificationInfo.Reply>>

    suspend fun resetNotificationCount()

    suspend fun getLoginParam(): LoginParam

    suspend fun login(loginParams: Map<String, String>): LoginParam

    suspend fun getTwoStepLoginInfo(): TwoStepLoginInfo

    suspend fun loginNextStep(once: String, code: String): TwoStepLoginInfo

    suspend fun logout(): Boolean

    suspend fun getHomePageInfo(): HomePageInfo

    suspend fun fetchUserInfo()

    suspend fun refreshAccount()

    //签到

    val hasCheckingInTips: Flow<Boolean>

    val autoCheckIn: Flow<Boolean>

    val lastCheckInTime: Flow<Long>

    suspend fun dailyInfo(): DailyInfo

    suspend fun checkIn(once: String): DailyInfo

    val myTopics: Flow<PagingData<MyTopicsInfo.Item>>
    val myFollowing: Flow<PagingData<MyFollowingInfo.Item>>
    suspend fun getMyNodes(): MyNodesInfo

}