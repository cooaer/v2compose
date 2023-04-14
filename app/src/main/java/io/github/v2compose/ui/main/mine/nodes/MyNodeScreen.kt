package io.github.v2compose.ui.main.mine.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.cooaer.htmltext.fullUrl
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.network.bean.MyNodesInfo
import io.github.v2compose.ui.common.BackIcon
import io.github.v2compose.ui.common.LoadError
import io.github.v2compose.ui.common.Loading

@Composable
fun MyNodesScreenRoute(
    onBackClick: () -> Unit,
    onNodeClick: (MyNodesInfo.Item) -> Unit,
    viewModel: MyNodesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyNodesScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNodeClick = onNodeClick,
        onRetryClick = viewModel::refresh,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyNodesScreen(
    uiState: MyNodesUiState,
    onBackClick: () -> Unit,
    onNodeClick: (MyNodesInfo.Item) -> Unit,
    onRetryClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.my_nodes)) },
                navigationIcon = { BackIcon(onBackClick = onBackClick) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { insets ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (uiState) {
                is MyNodesUiState.Loading -> Loading()
                is MyNodesUiState.Success -> NodesGrid(
                    myNodesInfo = uiState.data,
                    onNodeClick = onNodeClick,
                )
                is MyNodesUiState.Error -> LoadError(
                    error = uiState.error,
                    onRetryClick = onRetryClick,
                )
            }
        }
    }

}

@Composable
private fun NodesGrid(myNodesInfo: MyNodesInfo, onNodeClick: (MyNodesInfo.Item) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 108.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        if (!myNodesInfo.isValid) return@LazyVerticalGrid
        itemsIndexed(myNodesInfo.items, key = { _, item -> item.name }) { _, item ->
            NodeItem(
                title = item.title,
                avatar = item.avatar,
                item.topicNum,
                onItemClick = { onNodeClick(item) },
            )
        }
    }
}

@Composable
private fun NodeItem(
    title: String,
    avatar: String,
    topics: Int,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onItemClick() }
            .padding(horizontal = 2.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = avatar.fullUrl(baseUrl = Constants.baseUrl),
            contentDescription = title,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = modifier.height(4.dp))
        Row {
            val contentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
            Icon(
                Icons.Rounded.Topic,
                "topics",
                modifier = Modifier.size(16.dp),
                tint = contentColor
            )
            Spacer(Modifier.width(2.dp))
            Text(
                topics.toString(),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = contentColor,
            )
        }
    }
}