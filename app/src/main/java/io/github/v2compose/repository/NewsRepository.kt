package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.network.bean.RecentTopics
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getHomeNews(tab: String): NewsInfo
    val recentTopics: Flow<PagingData<RecentTopics.Item>>
}