package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.bean.DraftTopic
import io.github.v2compose.network.bean.*
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    suspend fun getTopicInfo(topicId: String): TopicInfo
    fun getTopic(topicId: String, reversed: Boolean): Flow<PagingData<Any>>

    val repliesOrderReversed: Flow<Boolean>

    suspend fun toggleRepliesReversed()

    fun search(keyword: String): Flow<PagingData<SoV2EXSearchResultInfo.Hit>>

    val topicTitleOverview: Flow<Boolean>

    suspend fun doTopicAction(
        action: String,
        method: ActionMethod,
        topicId: String,
        once: String
    ): V2exResult

    suspend fun doReplyAction(
        action: String,
        method: ActionMethod,
        topicId: String,
        replyId: String,
        once: String
    ): V2exResult

    suspend fun ignoreReply(topicId: String, replyId: String, once: String): Boolean

    suspend fun replyTopic(topicId: String, content: String, once: String): ReplyTopicResultInfo

    val draftTopic: Flow<DraftTopic>

    suspend fun saveDraftTopic(title: String, content: String, node: TopicNode?)

    suspend fun getCreateTopicPageInfo(): CreateTopicPageInfo

    suspend fun getTopicNodes(): List<TopicNode>

    suspend fun createTopic(
        title: String,
        content: String,
        nodeId: String,
        once: String
    ): CreateTopicPageInfo
}

enum class ActionMethod {
    Get, Post
}