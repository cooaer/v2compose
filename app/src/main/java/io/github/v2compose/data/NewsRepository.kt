package io.github.v2compose.data

import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.network.bean.NodesInfo

interface NewsRepository {
    suspend fun getHomeNews(tab: String): NewsInfo
}