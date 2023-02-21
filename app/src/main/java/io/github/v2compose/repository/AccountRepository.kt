package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.datasource.Account
import io.github.v2compose.network.bean.HomePageInfo
import io.github.v2compose.network.bean.LoginParam
import io.github.v2compose.network.bean.NotificationInfo
import io.github.v2compose.network.bean.TwoStepLoginInfo
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
    suspend fun signIn()

}