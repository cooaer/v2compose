package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.network.bean.SoV2EXSearchResultInfo
import io.github.v2compose.network.bean.TopicInfo
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    suspend fun getTopicInfo(topicId: String): TopicInfo
    fun getTopic(topicId: String, reversed: Boolean): Flow<PagingData<Any>>

    val repliesOrderReversed: Flow<Boolean>
    suspend fun toggleRepliesReversed()

    fun search(keyword: String): Flow<PagingData<SoV2EXSearchResultInfo.Hit>>

    val topicTitleOverview: Flow<Boolean>
}