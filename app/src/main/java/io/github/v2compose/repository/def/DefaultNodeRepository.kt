package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.core.extension.isRedirect
import io.github.v2compose.datasource.NodePagingSource
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.NodeInfo
import io.github.v2compose.network.bean.NodeTopicInfo
import io.github.v2compose.network.bean.NodesInfo
import io.github.v2compose.network.bean.NodesNavInfo
import io.github.v2compose.repository.NodeRepository
import io.github.v2compose.V2exUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultNodeRepository @Inject constructor(private val v2exService: V2exService) :
    NodeRepository {
    override suspend fun getNodes(): NodesInfo {
        return v2exService.nodes()
    }

    override suspend fun getNodesNavInfo(): NodesNavInfo {
        return v2exService.nodesNavInfo()
    }

    override suspend fun getNodeInfo(nodeId: String): NodeInfo {
        return v2exService.nodeInfo(nodeId)
    }

    override fun getNodeTopicInfoFlow(nodeId: String): Flow<PagingData<Any>> {
        return Pager(PagingConfig(pageSize = 10)) { NodePagingSource(nodeId, v2exService) }.flow
    }

    override suspend fun doNodeAction(nodeId: String, actionUrl: String): NodeTopicInfo {
        val nodeUrl = V2exUri.nodeUrl(nodeId)
        return try {
            v2exService.nodeAction(nodeUrl, actionUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e.isRedirect(nodeUrl)) {
                v2exService.nodesInfo(nodeId, 1)
            } else {
                throw e
            }
        }
    }
}
