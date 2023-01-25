package io.github.v2compose.ui.main.user

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.core.extension.castOrNull
import io.github.v2compose.core.share
import io.github.v2compose.network.bean.UserPageInfo
import io.github.v2compose.network.bean.UserReplies
import io.github.v2compose.network.bean.UserTopics
import io.github.v2compose.ui.common.*
import kotlinx.coroutines.launch

@Composable
fun UserScreenRoute(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val userArgs = viewModel.userArgs
    val topicTitleOverview by viewModel.topicTitleOverview.collectAsStateWithLifecycle()
    val userUiState by viewModel.userPageInfo.collectAsStateWithLifecycle()
    val userTopics = viewModel.userTopics.collectAsLazyPagingItems()
    val userReplies = viewModel.userReplies.collectAsLazyPagingItems()

    UserScreen(
        userUiState = userUiState,
        userTopics = userTopics,
        userReplies = userReplies,
        topicTitleOverview = topicTitleOverview,
        onBackClick = onBackClick,
        onShareClick = {
            context.share(userArgs.userName, Constants.userUrl(userArgs.userName))
        },
        onRetryClick = { viewModel.retry() },
        onTopicClick = onTopicClick,
        onNodeClick = onNodeClick,
        openUri = openUri,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserScreen(
    userUiState: UserUiState,
    userTopics: LazyPagingItems<UserTopics.Item>,
    userReplies: LazyPagingItems<UserReplies.Item>,
    topicTitleOverview: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onRetryClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
) {

    Scaffold(topBar = {
        UserTopBar(
            title = "",
            userPageInfo = userUiState.castOrNull<UserUiState.Success>()?.userPageInfo,
            onBackClick = onBackClick,
            onShareClick = onShareClick
        )
    }) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            UserContent(
                userUiState = userUiState,
                userTopics = userTopics,
                userReplies = userReplies,
                topicTitleOverview = topicTitleOverview,
                onRetryClick = onRetryClick,
                onTopicClick = onTopicClick,
                onNodeClick = onNodeClick,
                openUri = openUri
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTopBar(
    title: String,
    userPageInfo: UserPageInfo?,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    TopAppBar(
        title = {
            if (userPageInfo != null) {
                UserTopAppBarTitle(userPageInfo = userPageInfo)
            } else {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        navigationIcon = { BackIcon(onBackClick = onBackClick) },
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(Icons.Rounded.Share, "share user")
            }
        }
    )
}

@Composable
fun UserTopAppBarTitle(userPageInfo: UserPageInfo) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TopicUserAvatar(userName = userPageInfo.userName, userAvatar = userPageInfo.avatar)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                userPageInfo.userName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (userPageInfo.isOnline) {
                Text(
                    text = stringResource(id = R.string.user_online),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.user_offline),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun UserContent(
    userUiState: UserUiState,
    userTopics: LazyPagingItems<UserTopics.Item>,
    userReplies: LazyPagingItems<UserReplies.Item>,
    topicTitleOverview: Boolean,
    onRetryClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (userUiState) {
        is UserUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {
//                UserHeader(userPageInfo = userUiState.userPageInfo)
                UserDescription(userPageInfo = userUiState.userPageInfo)
                UserPager(
                    userTopics = userTopics,
                    userReplies = userReplies,
                    topicTitleOverview = topicTitleOverview,
                    onTopicClick = onTopicClick,
                    onNodeClick = onNodeClick,
                    openUri = openUri,
                )
            }
        }
        is UserUiState.Loading -> {
            Loading()
        }
        is UserUiState.Error -> {
            LoadError(error = userUiState.error, onRetryClick = onRetryClick)
        }
    }
}

@Composable
private fun UserHeader(userPageInfo: UserPageInfo) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        AsyncImage(
            model = userPageInfo.avatar,
            contentDescription = "${userPageInfo.userName}'s avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(userPageInfo.userName, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(4.dp))
            if (userPageInfo.isOnline) {
                Text(
                    text = stringResource(id = R.string.user_online),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.user_offline),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 6.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            userPageInfo.desc,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium)
        )
    }
}

@Composable
private fun UserDescription(userPageInfo: UserPageInfo) {
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            userPageInfo.desc,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserPager(
    userTopics: LazyPagingItems<UserTopics.Item>,
    userReplies: LazyPagingItems<UserReplies.Item>,
    topicTitleOverview: Boolean,
    onTopicClick: (String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val tabNames = listOf(stringResource(R.string.user_topic), stringResource(R.string.user_reply))

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions: List<TabPosition> ->
            UserTabIndicator(tabPosition = tabPositions[pagerState.currentPage])
        }) {
        tabNames.forEachIndexed { index, name ->
            val selected = pagerState.currentPage == index
            Tab(
                selected = selected,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page = index)
                    }
                }, modifier = Modifier.height(32.dp)
            ) {
                Text(
                    name,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                )
            }
        }
    }

    HorizontalPager(pageCount = 2, state = pagerState) {
        when (it) {
            0 -> UserTopicsList(
                items = userTopics,
                topicTitleOverview = topicTitleOverview,
                onTopicClick = onTopicClick,
                onNodeClick = onNodeClick
            )
            1 -> UserRepliesList(
                items = userReplies,
                onTopicClick = onTopicClick,
                openUri = openUri
            )
        }
    }
}

@Composable
private fun UserTopicsList(
    items: LazyPagingItems<UserTopics.Item>,
    topicTitleOverview: Boolean,
    onTopicClick: (String) -> Unit,
    onNodeClick: (String, String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        pagingRefreshItem(lazyPagingItems = items)

        itemsIndexed(items = items, key = { index, item -> item.link }) { index, item ->
            if (item == null) return@itemsIndexed
            UserTopicItem(
                topic = item,
                topicTitleOverview = topicTitleOverview,
                onTopicClick = onTopicClick,
                onNodeClick = onNodeClick
            )
        }

        pagingAppendMoreItem(lazyPagingItems = items)
    }
}

@Composable
fun UserTopicItem(
    topic: UserTopics.Item,
    topicTitleOverview: Boolean,
    onTopicClick: (String) -> Unit,
    onNodeClick: (String, String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTopicClick(topic.link) }) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Text(
                    topic.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    maxLines = if (topicTitleOverview) Constants.topicTitleOverviewMaxLines else Integer.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(8.dp))
                NodeTag(
                    nodeName = topic.nodeName,
                    nodeId = topic.nodeLink,
                    onItemClick = onNodeClick
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                topic.lastReply,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium),
            )
        }
        ListDivider(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun UserRepliesList(
    items: LazyPagingItems<UserReplies.Item>,
    onTopicClick: (String) -> Unit,
    openUri: (String) -> Unit,
) {
    LazyColumn {
        item(key = "refresh", contentType = "loadState") {
            PagingLoadState(state = items.loadState.refresh, onRetryClick = { items.retry() })
        }

        itemsIndexed(
            items = items,
            key = { index, item -> item.dock.link + item.content.content }) { index, item ->
            if (item == null) return@itemsIndexed
            UserReplyItem(reply = item, onTopicClick = onTopicClick, openUri = openUri)
        }

        item(key = "append", contentType = "loadState") {
            PagingLoadState(state = items.loadState.append, onRetryClick = { items.retry() })
        }
    }
}

@Composable
fun UserReplyItem(
    reply: UserReplies.Item,
    onTopicClick: (String) -> Unit,
    openUri: (String) -> Unit
) {
    val contentColor = LocalContentColor.current
    Box(modifier = Modifier.clickable { onTopicClick(reply.dock.link) }) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row {
                Text(
                    reply.dock.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = ContentAlpha.medium),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    reply.dock.time,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = ContentAlpha.disabled),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val backgroundColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            val leftBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
            HtmlContent(
                html = reply.content.content,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawRect(color = backgroundColor)
                        drawRect(
                            color = leftBorderColor,
                            size = size.copy(width = 4.dp.toPx())
                        )
                    }
                    .padding(start = 8.dp),
                onUriClick = openUri
            )
        }
        ListDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun UserTabIndicator(tabPosition: TabPosition, modifier: Modifier = Modifier) {
    val tabWidth = 32.dp
    val leftSpace = (tabPosition.width - tabWidth) / 2
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = tabPosition.left + leftSpace,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.BottomStart)
            .offset(x = indicatorOffset)
            .width(currentTabWidth)
            .height(2.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    )
}