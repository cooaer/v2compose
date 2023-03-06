package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.core.extension.isRedirect
import io.github.v2compose.datasource.UserRepliesDataSource
import io.github.v2compose.datasource.UserTopicsDataSource
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.UserPageInfo
import io.github.v2compose.network.bean.UserReplies
import io.github.v2compose.network.bean.UserTopics
import io.github.v2compose.repository.UserRepository
import io.github.v2compose.V2exUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(private val v2exService: V2exService) :
    UserRepository {
    override suspend fun getUserPageInfo(userName: String): UserPageInfo {
        return v2exService.userPageInfo(userName)
    }

    override fun getUserTopics(userName: String): Flow<PagingData<UserTopics.Item>> {
        return Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            )
        ) { UserTopicsDataSource(userName, v2exService) }.flow
    }

    override fun getUserReplies(userName: String): Flow<PagingData<UserReplies.Item>> {
        return Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            )
        ) { UserRepliesDataSource(userName, v2exService) }.flow
    }

    override suspend fun doUserAction(userName: String, url: String): UserPageInfo {
        return try {
            val referer = V2exUri.userUrl(userName)
            v2exService.userAction(referer, url)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e.isRedirect("/member/$userName")) {
                v2exService.userPageInfo(userName)
            } else {
                throw e
            }
        }
    }
}