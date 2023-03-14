package io.github.v2compose.ui.main.nodes

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.*
import io.github.v2compose.network.bean.Node
import io.github.v2compose.ui.common.LoadError
import io.github.v2compose.ui.common.PullToRefresh
import io.github.v2compose.ui.common.SimpleNode
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NodesContent(
    onNodeClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NodesViewModel = hiltViewModel(),
) {
    val nodesUiState by viewModel.nodesUiState.collectAsStateWithLifecycle()
    val nodeCategories by viewModel.nodeCategories.collectAsStateWithLifecycle()

    NodesContainer(nodesUiState, nodeCategories, onNodeClick, viewModel::refresh, modifier)
}

@Composable
private fun NodesContainer(
    nodesUiState: NodesUiState,
    nodeCategories: List<Pair<String, List<Node>>>,
    onNodeClick: (String, String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        when (nodesUiState) {
            is NodesUiState.Error -> {
                LoadError(error = nodesUiState.error, onRetryClick = onRefresh)
            }
            else -> {
                val isRefreshing = nodesUiState is NodesUiState.Loading
                PullToRefresh(refreshing = isRefreshing, onRefresh = onRefresh) {
                    NodesList(nodeCategories = nodeCategories, onNodeClick = onNodeClick)
                }
            }
        }
    }
}

private val CategoryTitleBarWidth = 92.dp
private val NodeMinWidth = 88.dp
private val NodeHeight = 92.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NodesList(
    nodeCategories: List<Pair<String, List<Node>>>,
    onNodeClick: (String, String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    BoxWithConstraints {
        val maxPageWidth = maxWidth - CategoryTitleBarWidth
        val nodeColumnCount = floor(maxPageWidth / NodeMinWidth).toInt()
        val nodeWidth = floor((maxPageWidth / nodeColumnCount).value).dp

        val nodeRowCount = floor(maxHeight / NodeHeight).toInt()
        val pageNodeCount = nodeRowCount * nodeColumnCount
        val nodeHeight = floor((maxHeight / nodeRowCount).value).dp

        val categoryIndies: List<Int> = remember(nodeCategories) {
            nodeCategories.map { category -> ceil(1f * category.second.size / pageNodeCount).toInt() }
                .runningFold(0) { sum, pages -> sum + pages }
        }
        val nodePages: List<List<Node>> = remember(nodeCategories) {
            nodeCategories.map { category ->
                val nodeCount = category.second.size
                val pageCount = ceil(1f * nodeCount / pageNodeCount).toInt()
                (0 until pageCount).map { index ->
                    val toIndex = minOf((index + 1) * pageNodeCount, nodeCount)
                    category.second.subList(index * pageNodeCount, toIndex)
                }
            }.flatten()
        }
        val selectedCategoryIndex = remember(pagerState.currentPage) {
            val nextPage = categoryIndies.indexOfFirst { it > pagerState.currentPage }
            if (nextPage >= 0) nextPage - 1 else categoryIndies.size - 1
        }

        Row() {
            LazyColumn(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .fillMaxHeight()
                    .width(CategoryTitleBarWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(
                    items = nodeCategories,
                    key = { _, item -> item.first }) { index, item ->
                    CategoryTitle(
                        selected = selectedCategoryIndex == index,
                        title = item.first,
                        onTitleClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(categoryIndies[index])
                            }
                        },
                    )
                }
            }
            VerticalPager(
                pageCount = nodePages.size,
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { index ->
                NodesGroup(nodePages[index], nodeWidth, nodeHeight, onNodeClick = onNodeClick)
            }
        }
    }
}

@Composable
private fun NodesGroup(
    nodes: List<Node>,
    nodeWidth: Dp,
    nodeHeight: Dp,
    onNodeClick: (String, String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxSize(),
        mainAxisAlignment = FlowMainAxisAlignment.Center,
    ) {
        val nodesSize = nodes.size
        val fullSize = ceil(nodesSize / 3f).toInt() * 3
        (0 until fullSize).forEach { index ->
            if (index < nodesSize) {
                val item = nodes[index]
                SimpleNode(
                    title = item.title,
                    avatar = item.avatar,
                    onItemClick = { onNodeClick(item.name, item.title) },
                    modifier = Modifier.size(nodeWidth, nodeHeight),
                )
            } else {
                Spacer(Modifier.size(nodeWidth, nodeHeight))
            }
        }
    }
}

@Composable
private fun CategoryTitle(selected: Boolean, title: String, onTitleClick: () -> Unit) {
    val textColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        LocalContentColor.current
    }

    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.1f)
    } else {
        Color.Transparent
    }

    val indicatorColor = MaterialTheme.colorScheme.primary
    val indicatorWidth = with(LocalDensity.current) { 4.dp.toPx() }

    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier
            .clickable { onTitleClick() }
            .drawBehind {
                drawRect(color = backgroundColor)
                if (selected) {
                    drawRect(color = indicatorColor, size = size.copy(width = indicatorWidth))
                }
            }
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        color = textColor,
        textAlign = TextAlign.Center,
    )
}

