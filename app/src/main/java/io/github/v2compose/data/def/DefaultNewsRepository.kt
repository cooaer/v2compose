package io.github.v2compose.data.def

import io.github.v2compose.data.NewsRepository
import io.github.v2compose.network.V2exApi
import io.github.v2compose.network.bean.NewsInfo
import javax.inject.Inject

class DefaultNewsRepository @Inject constructor(
    private val v2exApi: V2exApi,
) : NewsRepository {
    override suspend fun getHomeNews(tab: String): NewsInfo {
        return v2exApi.homeNews(tab)
    }


}