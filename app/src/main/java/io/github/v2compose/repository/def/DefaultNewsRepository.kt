package io.github.v2compose.repository.def

import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.repository.NewsRepository
import javax.inject.Inject

class DefaultNewsRepository @Inject constructor(
    private val v2exService: V2exService,
    private val appPreferences: AppPreferences,
) : NewsRepository {

    override suspend fun getHomeNews(tab: String): NewsInfo {
        return v2exService.homeNews(tab).also {
            appPreferences.updateAccount(unreadNotifications = it.unreadCount)
        }
    }

}