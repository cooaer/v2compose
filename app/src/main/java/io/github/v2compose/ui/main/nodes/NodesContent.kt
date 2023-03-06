package io.github.v2compose.ui.main.nodes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.FlowRow
import io.github.v2compose.network.bean.NodesNavInfo
import io.github.v2compose.ui.common.LoadError
import io.github.v2compose.ui.common.NodeTag
import io.github.v2compose.ui.common.PullToRefresh

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NodesContent(
    onNodeClick: (String, String) -> Unit,
    viewModel: NodesViewModel = hiltViewModel(),
) {
    val nodesUiState by viewModel.nodesUiState.collectAsStateWithLifecycle()
    val nodesNavInfo by viewModel.nodesNavInfo.collectAsStateWithLifecycle()

    NodesContainer(nodesUiState, nodesNavInfo, onNodeClick, viewModel::refresh)
}

@Composable
private fun NodesContainer(
    nodesUiState: NodesUiState,
    nodesNavInfo: NodesNavInfo?,
    onNodeClick: (String, String) -> Unit,
    onRefresh: () -> Unit,
) {
    when (nodesUiState) {
        is NodesUiState.Error -> {
            LoadError(error = nodesUiState.error, onRetryClick = onRefresh)
        }
        else -> {
            val isRefreshing = nodesUiState is NodesUiState.Loading
            PullToRefresh(refreshing = isRefreshing, onRefresh = onRefresh) {
                nodesNavInfo?.let {
                    NodesList(nodesNavInfo = it, onNodeClick = onNodeClick)
                }
            }
        }
    }
}


@Composable
fun NodesList(
    nodesNavInfo: NodesNavInfo, onNodeClick: (String, String) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = nodesNavInfo.size, key = { nodesNavInfo[it].category }) { index ->
            NodesGroup(nodesNavInfo[index], onNodeClick = onNodeClick)
        }
    }
}

@Composable
fun NodesGroup(
    category: NodesNavInfo.Item, onNodeClick: (String, String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        NodesGroupTitle(title = category.category)
        Spacer(modifier = Modifier.height(8.dp))
        NodesFlow(nodes = category.nodes, onNodeClick = onNodeClick)
    }
}

@Composable
fun NodesGroupTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxSize())
}

@Composable
fun NodesFlow(
    nodes: List<NodesNavInfo.Item.NodeItem>,
    onNodeClick: (String, String) -> Unit,
) {
    FlowRow(mainAxisSpacing = 12.dp, crossAxisSpacing = 12.dp, modifier = Modifier.fillMaxWidth()) {
        nodes.forEach { node ->
            NodeTag(nodeName = node.name, nodeId = node.id, onItemClick = onNodeClick)
        }
    }
}