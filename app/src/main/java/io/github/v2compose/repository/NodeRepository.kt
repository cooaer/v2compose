package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.network.bean.NodeInfo
import io.github.v2compose.network.bean.NodeTopicInfo
import io.github.v2compose.network.bean.NodesInfo
import io.github.v2compose.network.bean.NodesNavInfo
import kotlinx.coroutines.flow.Flow

interface NodeRepository {

    suspend fun getNodes(): NodesInfo

    val nodesNavInfo: Flow<NodesNavInfo?>
    suspend fun getNodesNavInfo(): NodesNavInfo

    suspend fun getNodeInfo(nodeId: String): NodeInfo
    fun getNodeTopicInfo(nodeId: String): Flow<PagingData<Any>>

    suspend fun doNodeAction(nodeId: String, actionUrl: String): NodeTopicInfo

}