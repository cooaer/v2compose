package io.github.v2compose.ui.main.nodes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.FlowRow
import io.github.v2compose.network.bean.NodesNavInfo
import io.github.v2compose.ui.common.NodeTag

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NodesContent(
    onNodeClick: (String, String) -> Unit,
    viewModel: NodesViewModel = hiltViewModel(),
) {
    var nodesUiState = viewModel.nodesNavInfo.value
    if (nodesUiState !is NodesUiState.Success) {
        nodesUiState = viewModel.nodesNavInfo.collectAsStateWithLifecycle().value
    }
    when (nodesUiState) {
        is NodesUiState.Success -> {
            NodesList(nodesNavInfo = nodesUiState.nodesNavInfo, onNodeClick = onNodeClick)
        }
        is NodesUiState.Loading -> {
            NodesLoading()
        }
        is NodesUiState.Error -> {
            NodesError()
        }
    }
}


@Composable
fun NodesList(
    nodesNavInfo: NodesNavInfo, onNodeClick: (String, String) -> Unit,
) {
    LazyColumn() {
        items(count = nodesNavInfo.size, key = { nodesNavInfo[it].category }) { index ->
            NodesGroup(nodesNavInfo[index], onNodeClick = onNodeClick)
        }
    }
}

@Composable
fun NodesGroup(
    category: NodesNavInfo.Item, onNodeClick: (String, String) -> Unit,
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        NodesGroupTitle(title = category.category)
        Spacer(modifier = Modifier.height(8.dp))
        NodesFlow(nodes = category.nodes, onNodeClick = onNodeClick)
    }
}

@Composable
fun NodesGroupTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
}

@Composable
fun NodesFlow(
    nodes: List<NodesNavInfo.Item.NodeItem>,
    onNodeClick: (String, String) -> Unit,
) {
    FlowRow(mainAxisSpacing = 12.dp, crossAxisSpacing = 12.dp) {
        nodes.forEach { node ->
            NodeTag(nodeName = node.name, nodeId = node.id, onItemClick = onNodeClick)
        }
    }
}

@Composable
fun NodesLoading() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
fun NodesError() {

}