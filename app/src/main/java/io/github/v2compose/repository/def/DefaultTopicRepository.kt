package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.datasource.SearchPagingSource
import io.github.v2compose.datasource.TopicPagingSource
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.ReplyTopicResultInfo
import io.github.v2compose.network.bean.SoV2EXSearchResultInfo
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.network.bean.V2exResult
import io.github.v2compose.repository.ActionMethod
import io.github.v2compose.repository.TopicRepository
import io.github.v2compose.util.V2exUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultTopicRepository @Inject constructor(
    private val api: V2exService,
    private val appPreferences: AppPreferences,
) : TopicRepository {

    override suspend fun getTopicInfo(topicId: String): TopicInfo {
        return api.topicDetails(topicId, 1)
    }

    override fun getTopic(topicId: String, reversed: Boolean): Flow<PagingData<Any>> {
        return Pager(PagingConfig(pageSize = 10)) { TopicPagingSource(api, topicId, reversed) }.flow
    }

    override val repliesOrderReversed: Flow<Boolean>
        get() = appPreferences.appSettings.map { it.topicRepliesReversed }

    override suspend fun toggleRepliesReversed() {
        appPreferences.toggleTopicRepliesOrder()
    }

    override fun search(keyword: String): Flow<PagingData<SoV2EXSearchResultInfo.Hit>> {
        return Pager(PagingConfig(pageSize = 10)) { SearchPagingSource(keyword, api) }.flow
    }

    override val topicTitleOverview: Flow<Boolean>
        get() = appPreferences.appSettings.map { it.topicTitleOverview }

    override suspend fun topicAction(
        action: String,
        method: ActionMethod,
        topicId: String,
        once: String,
    ): V2exResult {
        val topicUrl = V2exUri.topicUrl(topicId)
        return when (method) {
            ActionMethod.Get -> api.getTopicAction(topicUrl, action, topicId, once)
            ActionMethod.Post -> api.postTopicAction(topicUrl, action, topicId, once)
        }
    }

    override suspend fun replyAction(
        action: String,
        method: ActionMethod,
        topicId: String,
        replyId: String,
        once: String
    ): V2exResult {
        val topicUrl = V2exUri.topicUrl(topicId)
        return when (method) {
            ActionMethod.Get -> api.getReplyAction(topicUrl, action, replyId, once)
            ActionMethod.Post -> api.postReplyAction(topicUrl, action, replyId, once)
        }
    }

    override suspend fun ignoreReply(
        topicId: String,
        replyId: String,
        once: String
    ): Boolean {
        val topicUrl = V2exUri.topicUrl(topicId)
        return api.ignoreReply(topicUrl, replyId, once).isSuccessful
    }

    override suspend fun replyTopic(topicId: String, content: String, once: String): ReplyTopicResultInfo {
        val params = mapOf("content" to content, "once" to once)
        return api.replyTopic(topicId, params)
    }
}