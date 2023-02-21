package io.github.v2compose.repository.def

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.v2compose.datasource.NodePagingSource
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.NodeInfo
import io.github.v2compose.network.bean.NodesInfo
import io.github.v2compose.network.bean.NodesNavInfo
import io.github.v2compose.repository.NodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultNodeRepository @Inject constructor(private val v2ExService: V2exService) : NodeRepository {
    override suspend fun getNodes(): NodesInfo {
        return v2ExService.nodes()
    }

    override suspend fun getNodesNavInfo(): NodesNavInfo {
        return v2ExService.nodesNavInfo()
    }

    override suspend fun getNodeInfo(nodeId: String): NodeInfo {
        return v2ExService.nodeInfo(nodeId)
    }

    override fun getNodeTopicInfoFlow(nodeId: String): Flow<PagingData<Any>> {
        return Pager(PagingConfig(pageSize = 10)) { NodePagingSource(nodeId, v2ExService) }.flow
    }
}
