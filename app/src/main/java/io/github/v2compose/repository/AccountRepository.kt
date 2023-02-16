package io.github.v2compose.repository

import io.github.v2compose.datasource.Account
import io.github.v2compose.network.bean.HomePageInfo
import io.github.v2compose.network.bean.LoginParam
import io.github.v2compose.network.bean.TwoStepLoginInfo
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    val account: Flow<Account>

    val isLoggedIn: Flow<Boolean>

    suspend fun getLoginParam(): LoginParam

    suspend fun login(loginParams: Map<String, String>): LoginParam

    suspend fun getTwoStepLoginInfo(): TwoStepLoginInfo

    suspend fun loginNextStep(once: String, code: String): TwoStepLoginInfo

    suspend fun logout(): Boolean

    suspend fun updateLocalUserInfo(
        userName: String? = null,
        userAvatar: String? = null,
        description: String? = null,
        nodes: Int? = null,
        topics: Int? = null,
        following: Int? = null,
    )

    suspend fun getHomePageInfo(): HomePageInfo

    suspend fun fetchUserInfo()

    suspend fun refreshAccount()

    //签到
    suspend fun signIn()

}