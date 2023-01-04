package io.github.v2compose.data

import io.github.v2compose.network.bean.NodesInfo
import io.github.v2compose.network.bean.NodesNavInfo

interface NodeRepository {

    suspend fun getNodes(): NodesInfo
    suspend fun getNodesNavInfo(): NodesNavInfo

}