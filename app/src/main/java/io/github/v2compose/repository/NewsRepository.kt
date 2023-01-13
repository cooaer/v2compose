package io.github.v2compose.repository

import io.github.v2compose.network.bean.NewsInfo

interface NewsRepository {
    suspend fun getHomeNews(tab: String): NewsInfo
}