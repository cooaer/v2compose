package io.github.v2compose.repository.def

import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.repository.NewsRepository
import javax.inject.Inject

class DefaultNewsRepository @Inject constructor(
    private val v2ExService: V2exService,
) : NewsRepository {
    override suspend fun getHomeNews(tab: String): NewsInfo {
        return v2ExService.homeNews(tab)
    }


}