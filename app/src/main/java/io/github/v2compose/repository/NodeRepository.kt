package io.github.v2compose.repository

import androidx.paging.PagingData
import io.github.v2compose.network.bean.*
import kotlinx.coroutines.flow.Flow

interface NodeRepository {

    suspend fun getNodes(): NodesInfo

    suspend fun getAllNodes(): List<Node>

    val nodesNavInfo: Flow<NodesNavInfo?>
    suspend fun getNodesNavInfo(): NodesNavInfo

    suspend fun getNodeInfo(nodeName: String): NodeInfo
    fun getNodeTopicInfo(nodeName: String): Flow<PagingData<Any>>

    suspend fun doNodeAction(nodeName: String, actionUrl: String): NodeTopicInfo

}