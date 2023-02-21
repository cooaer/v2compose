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
    private val v2exService: V2exService,
    private val appPreferences: AppPreferences,
) : TopicRepository {

    override suspend fun getTopicInfo(topicId: String): TopicInfo {
        return v2exService.topicDetails(topicId, 1)
    }

    override fun getTopic(topicId: String, reversed: Boolean): Flow<PagingData<Any>> {
        return Pager(PagingConfig(pageSize = 10)) { TopicPagingSource(v2exService, topicId, reversed) }.flow
    }

    override val repliesOrderReversed: Flow<Boolean>
        get() = appPreferences.appSettings.map { it.topicRepliesReversed }

    override suspend fun toggleRepliesReversed() {
        appPreferences.toggleTopicRepliesOrder()
    }

    override fun search(keyword: String): Flow<PagingData<SoV2EXSearchResultInfo.Hit>> {
        return Pager(PagingConfig(pageSize = 10)) { SearchPagingSource(keyword, v2exService) }.flow
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
            ActionMethod.Get -> v2exService.getTopicAction(topicUrl, action, topicId, once)
            ActionMethod.Post -> v2exService.postTopicAction(topicUrl, action, topicId, once)
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
            ActionMethod.Get -> v2exService.getReplyAction(topicUrl, action, replyId, once)
            ActionMethod.Post -> v2exService.postReplyAction(topicUrl, action, replyId, once)
        }
    }

    override suspend fun ignoreReply(
        topicId: String,
        replyId: String,
        once: String
    ): Boolean {
        val topicUrl = V2exUri.topicUrl(topicId)
        return v2exService.ignoreReply(topicUrl, replyId, once).isSuccessful
    }

    override suspend fun replyTopic(topicId: String, content: String, once: String): ReplyTopicResultInfo {
        val params = mapOf("content" to content, "once" to once)
        return v2exService.replyTopic(topicId, params)
    }
}