package io.github.v2compose.repository.def

import io.github.v2compose.repository.NodeRepository
import io.github.v2compose.network.V2exApi
import io.github.v2compose.network.bean.NodesInfo
import io.github.v2compose.network.bean.NodesNavInfo
import javax.inject.Inject

class DefaultNodeRepository @Inject constructor(private val v2exApi: V2exApi) : NodeRepository {
    override suspend fun getNodes(): NodesInfo {
        return v2exApi.nodes()
    }

    override suspend fun getNodesNavInfo(): NodesNavInfo {
        return v2exApi.nodesNavInfo()
    }
}