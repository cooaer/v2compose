package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.V2exUri
import io.github.v2compose.core.extension.isRedirect
import io.github.v2compose.datasource.AppStateStore
import io.github.v2compose.datasource.NodePagingSource
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.NodeInfo
import io.github.v2compose.network.bean.NodeTopicInfo
import io.github.v2compose.network.bean.NodesNavInfo
import io.github.v2compose.repository.NodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultNodeRepository @Inject constructor(
    private val v2exService: V2exService,
    private val appStateStore: AppStateStore,
) : NodeRepository {

    override suspend fun getNodes() = v2exService.nodes()

    override suspend fun getAllNodes() = v2exService.allNodes()

    override val nodesNavInfo: Flow<NodesNavInfo?>
        get() = appStateStore.nodesNavInfo

    override suspend fun getNodesNavInfo(): NodesNavInfo {
        return v2exService.nodesNavInfo().also {
            appStateStore.updateNodesNavInfo(it)
        }
    }

    override suspend fun getNodeInfo(nodeName: String): NodeInfo {
        return v2exService.nodeInfo(nodeName)
    }

    override fun getNodeTopicInfo(nodeName: String): Flow<PagingData<Any>> {
        return Pager(PagingConfig(pageSize = 10)) { NodePagingSource(nodeName, v2exService) }.flow
    }

    override suspend fun doNodeAction(nodeName: String, actionUrl: String): NodeTopicInfo {
        val nodeUrl = V2exUri.nodeUrl(nodeName)
        return try {
            v2exService.nodeAction(nodeUrl, actionUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e.isRedirect(nodeUrl)) {
                v2exService.nodesInfo(nodeName, 1)
            } else {
                throw e
            }
        }
    }
}
