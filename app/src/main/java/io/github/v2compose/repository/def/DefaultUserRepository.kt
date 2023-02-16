package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.datasource.UserRepliesDataSource
import io.github.v2compose.datasource.UserTopicsDataSource
import io.github.v2compose.network.V2exApi
import io.github.v2compose.network.bean.UserPageInfo
import io.github.v2compose.network.bean.UserReplies
import io.github.v2compose.network.bean.UserTopics
import io.github.v2compose.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(private val v2exApi: V2exApi) : UserRepository {
    override suspend fun getUserPageInfo(userName: String): UserPageInfo {
        return v2exApi.userPageInfo(userName)
    }

    override fun getUserTopics(userName: String): Flow<PagingData<UserTopics.Item>> {
        return Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            )
        ) { UserTopicsDataSource(userName, v2exApi) }.flow
    }

    override fun getUserReplies(userName: String): Flow<PagingData<UserReplies.Item>> {
        return Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            )
        ) { UserRepliesDataSource(userName, v2exApi) }.flow
    }

}