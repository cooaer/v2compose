package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.network.bean.ReplyTopicResultInfo
import io.github.v2compose.network.bean.SoV2EXSearchResultInfo
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.network.bean.V2exResult
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    suspend fun getTopicInfo(topicId: String): TopicInfo
    fun getTopic(topicId: String, reversed: Boolean): Flow<PagingData<Any>>

    val repliesOrderReversed: Flow<Boolean>

    suspend fun toggleRepliesReversed()

    fun search(keyword: String): Flow<PagingData<SoV2EXSearchResultInfo.Hit>>

    val topicTitleOverview: Flow<Boolean>

    suspend fun topicAction(
        action: String,
        method: ActionMethod,
        topicId: String,
        once: String
    ): V2exResult

    suspend fun replyAction(
        action: String,
        method: ActionMethod,
        topicId: String,
        replyId: String,
        once: String
    ): V2exResult

    suspend fun ignoreReply(topicId: String, replyId: String, once: String): Boolean

    suspend fun replyTopic(topicId: String, content: String, once: String): ReplyTopicResultInfo
}

enum class ActionMethod {
    Get, Post
}