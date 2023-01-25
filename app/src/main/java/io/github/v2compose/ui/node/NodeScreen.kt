package io.github.v2compose.ui.node

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.core.extension.castOrNull
import io.github.v2compose.network.bean.NodeInfo
import io.github.v2compose.network.bean.NodeTopicInfo
import io.github.v2compose.ui.common.*

private const val TAG = "NodeScreen"

@Composable
fun NodeRoute(
    onBackClick: () -> Unit,
    onTopicClick: (NodeTopicInfo.Item) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
    viewModel: NodeViewModel = hiltViewModel(),
    nodeScreenState: NodeScreenState = rememberNodeScreenState(),
) {
    val nodeArgs = viewModel.nodeArgs
    val nodeUiState by viewModel.nodeInfoFlow.collectAsStateWithLifecycle()
    val lazyPagingItems = viewModel.nodeTopicInfoFlow.collectAsLazyPagingItems()
    val topicTitleOverview by viewModel.topicTitleOverview.collectAsStateWithLifecycle()

    NodeScreen(
        nodeArgs = nodeArgs,
        nodeUiState = nodeUiState,
        lazyPagingItems = lazyPagingItems,
        topicTitleOverview = topicTitleOverview,
        onBackClick = onBackClick,
        onRetryNodeClick = { viewModel.retryNode() },
        onTopicClick = onTopicClick,
        onUserAvatarClick = onUserAvatarClick,
        onShareClick = { nodeScreenState.share(nodeArgs, nodeUiState) },
        openUri = openUri,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NodeScreen(
    nodeArgs: NodeArgs,
    nodeUiState: NodeUiState,
    lazyPagingItems: LazyPagingItems<Any>,
    topicTitleOverview: Boolean,
    onBackClick: () -> Unit,
    onRetryNodeClick: () -> Unit,
    onTopicClick: (NodeTopicInfo.Item) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onShareClick: () -> Unit,
    openUri: (String) -> Unit,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val nodeTopicInfo = if (lazyPagingItems.itemCount > 0) {
        lazyPagingItems.peek(0).castOrNull<NodeTopicInfo>()
    } else null

    Scaffold(
        topBar = {
            NodeTopBar(
                nodeArgs = nodeArgs,
                nodeUiState = nodeUiState,
                nodeTopicInfo = nodeTopicInfo,
                onBackClick = onBackClick,
                onShareClick = onShareClick,
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) {
        NodeContent(
            nodeUiState = nodeUiState,
            lazyPagingItems = lazyPagingItems,
            topicTitleOverview = topicTitleOverview,
            modifier = Modifier
                .padding(it)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            onTopicClick = onTopicClick,
            onUserAvatarClick = onUserAvatarClick,
            onRetryNodeClick = onRetryNodeClick,
            openUri = openUri,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NodeTopBar(
    nodeArgs: NodeArgs,
    nodeUiState: NodeUiState,
    nodeTopicInfo: NodeTopicInfo?,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            NodeTitle(
                nodeArgs = nodeArgs,
                nodeUiState = nodeUiState,
                nodeTopicInfo = nodeTopicInfo
            )
        },
        navigationIcon = { BackIcon(onBackClick = onBackClick) },
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(Icons.Rounded.Share, "share node")
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun NodeTitle(nodeArgs: NodeArgs, nodeUiState: NodeUiState, nodeTopicInfo: NodeTopicInfo?) {
    val nodeInfo =
        remember(nodeUiState) { if (nodeUiState is NodeUiState.Success) nodeUiState.nodeInfo else null }
    val nodeName = nodeInfo?.name ?: nodeArgs.nodeName
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = nodeInfo?.avatar,
            contentDescription = nodeName,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.disabled))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                nodeName ?: stringResource(id = R.string.node),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onBackground,
            )
            nodeInfo?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(id = R.string.node_topics_and_favorites, it.topics, it.stars),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.disabled),
                )
            }
        }
    }
}

@Composable
private fun NodeContent(
    nodeUiState: NodeUiState,
    lazyPagingItems: LazyPagingItems<Any>,
    topicTitleOverview: Boolean,
    onRetryNodeClick: () -> Unit,
    onTopicClick: (NodeTopicInfo.Item) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    when (nodeUiState) {
        is NodeUiState.Success -> {
            TopicList(
                nodeInfo = nodeUiState.nodeInfo,
                lazyPagingItems = lazyPagingItems,
                topicTitleOverview = topicTitleOverview,
                onTopicClick = onTopicClick,
                onUserAvatarClick = onUserAvatarClick,
                openUri = openUri,
                modifier = modifier,
            )
        }
        is NodeUiState.Error -> {
            LoadError(
                error = nodeUiState.error,
                onRetryClick = onRetryNodeClick,
                modifier = modifier
            )
        }
        is NodeUiState.Loading -> {
            Loading(modifier = modifier)
        }
    }
}

@Composable
private fun TopicList(
    nodeInfo: NodeInfo,
    lazyPagingItems: LazyPagingItems<Any>,
    topicTitleOverview: Boolean,
    onTopicClick: (NodeTopicInfo.Item) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nodeTopicInfo: NodeTopicInfo? = if (lazyPagingItems.itemCount > 0) {
        lazyPagingItems.peek(0).castOrNull<NodeTopicInfo>()
    } else null

    //TODO: 非登录情况下，某些节点无法访问
    if (nodeTopicInfo != null && !nodeTopicInfo.isValid) {
        Log.e(TAG, "node topic info is invalid, nodeInfo = $nodeInfo")
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize(), state = lazyPagingItems.rememberLazyListState()) {
        pagingRefreshItem(lazyPagingItems = lazyPagingItems)
        itemsIndexed(items = lazyPagingItems, key = { index, item -> item }) { index, item ->
            if (item is NodeTopicInfo) {
                if (nodeInfo.header.isNotEmpty()) {
                    NodeDescription(desc = nodeInfo.header, openUri = openUri)
                }
            } else if (item is NodeTopicInfo.Item) {
                NodeTopic(
                    item = item,
                    titleOverview = topicTitleOverview,
                    onTopicClick = onTopicClick,
                    onUserAvatarClick = onUserAvatarClick
                )
            }
        }
        pagingAppendMoreItem(lazyPagingItems = lazyPagingItems)
    }
}

@Composable
private fun NodeDescription(desc: String, openUri: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        HtmlContent(
            html = desc,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onUriClick = openUri
        )
        ListDivider(modifier = Modifier.align(alignment = Alignment.BottomCenter))
    }
}

@Composable
private fun NodeTopic(
    item: NodeTopicInfo.Item,
    titleOverview: Boolean,
    onTopicClick: (NodeTopicInfo.Item) -> Unit,
    onUserAvatarClick: (String, String) -> Unit
) {
    Box(modifier = Modifier.clickable { onTopicClick(item) }) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TopicUserAvatar(
                    userName = item.userName,
                    userAvatar = item.avatar,
                    onUserAvatarClick = { onUserAvatarClick(item.userName, item.avatar) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = item.userName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.FirstLineTop,
                            )
                        )
                    )

                    Row {
                        Text(
                            stringResource(id = R.string.node_click_times, item.clickNum),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.n_comment, item.commentNum),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                item.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (titleOverview) Constants.topicTitleOverviewMaxLines else Integer.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
            )
        }
        ListDivider(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}