package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.network.bean.TopicInfo
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    suspend fun getTopicInfo(topicId: String): TopicInfo
    fun getTopic(topicId: String, reversed:Boolean): Flow<PagingData<Any>>
}