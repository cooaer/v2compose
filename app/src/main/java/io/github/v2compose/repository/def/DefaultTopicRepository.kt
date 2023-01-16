package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.datasource.AppSettings
import io.github.v2compose.datasource.AppSettingsDataSource
import io.github.v2compose.datasource.TopicPagingSource
import io.github.v2compose.network.V2exApi
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultTopicRepository @Inject constructor(
    private val api: V2exApi,
    private val appSettingsDataSource: AppSettingsDataSource,
) : TopicRepository {

    override suspend fun getTopicInfo(topicId: String): TopicInfo {
        return api.topicDetails(topicId, 1)
    }

    override fun getTopic(topicId: String, reversed: Boolean): Flow<PagingData<Any>> {
        return Pager(PagingConfig(pageSize = 10)) { TopicPagingSource(api, topicId, reversed) }.flow
    }

    override val appSettings: Flow<AppSettings>
        get() = appSettingsDataSource.appSettings

    override suspend fun toggleRepliesReversed() {
        appSettingsDataSource.toggleTopicRepliesOrder()
    }
}