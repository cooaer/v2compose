package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.network.bean.UserPageInfo
import io.github.v2compose.network.bean.UserReplies
import io.github.v2compose.network.bean.UserTopics
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserPageInfo(userName: String): UserPageInfo

    fun getUserTopics(userName: String): Flow<PagingData<UserTopics.Item>>

    fun getUserReplies(userName: String): Flow<PagingData<UserReplies.Item>>


}