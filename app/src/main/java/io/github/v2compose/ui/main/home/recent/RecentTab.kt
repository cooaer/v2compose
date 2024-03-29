package io.github.v2compose.ui.main.home.recent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.v2compose.network.bean.RecentTopics
import io.github.v2compose.ui.common.*
import io.github.v2compose.ui.main.composables.ClickHandler
import kotlinx.coroutines.launch

@Composable
fun RecentTab(
    onRecentItemClick: (RecentTopics.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    viewModel: RecentViewModel = hiltViewModel(),
) {

    val recentTopics = viewModel.recentTopics.collectAsLazyPagingItems()
    val topicTitleOverview by viewModel.topicTitleOverview.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (recentTopics.itemSnapshotList.isEmpty()) {
            PagingLoadState(
                state = recentTopics.loadState.refresh,
                onRetryClick = { recentTopics.retry() },
            )
        } else {
            RecentTopicsList(
                recentTopics = recentTopics,
                topicTitleOverview = topicTitleOverview,
                onRecentItemClick = onRecentItemClick,
                onNodeClick = onNodeClick,
                onUserAvatarClick = onUserAvatarClick,
            )
        }
    }
}

@Composable
private fun RecentTopicsList(
    recentTopics: LazyPagingItems<RecentTopics.Item>,
    topicTitleOverview: Boolean,
    onRecentItemClick: (RecentTopics.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val refreshing = remember(recentTopics.loadState) {
        recentTopics.loadState.refresh is LoadState.Loading
    }

    PullToRefresh(refreshing = refreshing, onRefresh = { recentTopics.refresh() }) {
        val lazyListState = recentTopics.rememberLazyListState()
        ClickHandler(enabled = !refreshing) {
            coroutineScope.launch {
                if (lazyListState.isScrollInProgress) {
                    lazyListState.animateScrollToItem(0)
                    recentTopics.refresh()
                } else if (lazyListState.canScrollBackward) {
                    lazyListState.animateScrollToItem(0)
                } else {
                    recentTopics.refresh()
                }
            }
        }

        LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
            pagingPrependMoreItem(recentTopics)
            itemsIndexed(recentTopics, key = { _, item -> item.id }) { index, item ->
                if (item == null) return@itemsIndexed
                SimpleTopic(title = item.title,
                    userName = item.userName,
                    userAvatar = item.avatar,
                    time = item.time,
                    replyCount = item.replies.toString(),
                    nodeName = item.nodeName,
                    nodeTitle = item.nodeTitle,
                    titleOverview = topicTitleOverview,
                    onItemClick = { onRecentItemClick(item) },
                    onNodeClick = { onNodeClick(item.nodeName, item.nodeTitle) },
                    onUserAvatarClick = { onUserAvatarClick(item.userName, item.avatar) })
            }
            pagingAppendMoreItem(recentTopics)
        }
    }
}