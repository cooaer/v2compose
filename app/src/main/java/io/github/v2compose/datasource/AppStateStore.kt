package io.github.v2compose.datasource

import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.network.bean.NodesNavInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import me.ghui.fruit.Fruit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStateStore @Inject constructor(
    private val fruit: Fruit,
) {

    private val _hasCheckingInTips = MutableStateFlow(false)
    val hasCheckingInTips: Flow<Boolean> = flow {
        emitAll(_hasCheckingInTips)
    }

    suspend fun updateHasCheckingInTips(value: Boolean) {
        _hasCheckingInTips.emit(value)
    }

    private val _nodesNavInfo = MutableStateFlow<NodesNavInfo?>(null)
    val nodesNavInfo: Flow<NodesNavInfo?> = flow {
        emitAll(_nodesNavInfo)
    }

    suspend fun updateNodesNavInfoWithNewsInfo(newsInfo: NewsInfo) {
        if (newsInfo.isValid && _nodesNavInfo.value != null) {
            return
        }
        val newNodesNavInfo = fruit.fromHtml(newsInfo.rawResponse, NodesNavInfo::class.java)
        if (newNodesNavInfo.isValid) {
            _nodesNavInfo.emit(newNodesNavInfo)
        }
    }

    suspend fun updateNodesNavInfo(value: NodesNavInfo?) {
        _nodesNavInfo.emit(value)
    }

}