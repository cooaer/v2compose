package io.github.v2compose.repository.def

import io.github.v2compose.bean.AccountBalance
import io.github.v2compose.datasource.AccountPreferences
import io.github.v2compose.datasource.AppStateStore
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.repository.NewsRepository
import javax.inject.Inject

class DefaultNewsRepository @Inject constructor(
    private val v2exService: V2exService,
    private val accountPreferences: AccountPreferences,
    private val appStateStore: AppStateStore,
) : NewsRepository {

    override suspend fun getHomeNews(tab: String): NewsInfo {
        return v2exService.homeNews(tab).also {
            accountPreferences.unreadNotifications(it.unreadCount)
            accountPreferences.updateAccount(
                balance = AccountBalance(
                    it.balanceGold,
                    it.balanceSilver,
                    it.balanceBronze,
                )
            )
            appStateStore.updateHasCheckingInTips(it.hasCheckingInTips())
            appStateStore.updateNodesNavInfoWithNewsInfo(it)
        }
    }

}