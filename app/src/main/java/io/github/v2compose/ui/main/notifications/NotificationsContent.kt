package io.github.v2compose.ui.main.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.v2compose.R
import io.github.v2compose.V2exUri
import io.github.v2compose.network.bean.NotificationInfo
import io.github.v2compose.ui.common.*
import io.github.v2compose.ui.gallery.composables.PopupImage
import io.github.v2compose.ui.main.composables.ClickHandler
import kotlinx.coroutines.launch

private const val TAG = "NotificationsContent"

@Composable
fun NotificationsContent(
    onLoginClick: () -> Unit,
    onUriClick: (String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val unreadNotifications by viewModel.unreadNotifications.collectAsStateWithLifecycle()

    if (isLoggedIn) {
        val notifications = viewModel.notifications.collectAsLazyPagingItems()
        NotificationsContainer(
            unreadNotifications = unreadNotifications,
            notifications = notifications,
            sizedHtmls = viewModel.sizedHtmls,
            onUriClick = onUriClick,
            onUserAvatarClick = onUserAvatarClick,
            loadHtmlImage = viewModel::loadHtmlImage,
            modifier = modifier,
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            ElevatedButton(onClick = onLoginClick, modifier = Modifier.align(Alignment.Center)) {
                Text(stringResource(id = R.string.login))
            }
        }
    }
}

@Composable
private fun NotificationsContainer(
    unreadNotifications: Int,
    notifications: LazyPagingItems<NotificationInfo.Reply>,
    sizedHtmls: SnapshotStateMap<String, String>,
    onUriClick: (String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    loadHtmlImage: (String, String, String?) -> Unit,
    modifier: Modifier,
) {
    var htmlImageUrl by rememberSaveable { mutableStateOf("") }
    if (htmlImageUrl.isNotEmpty()) {
        PopupImage(imageUrl = htmlImageUrl) {
            htmlImageUrl = ""
        }
    }

    LaunchedEffect(unreadNotifications) {
        if (unreadNotifications > 0) {
            notifications.refresh()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (notifications.itemCount > 0) {
            NotificationList(
                notifications = notifications,
                sizedHtmls = sizedHtmls,
                onUriClick = onUriClick,
                onUserAvatarClick = onUserAvatarClick,
                loadHtmlImage = loadHtmlImage,
                onHtmlImageClick = { current, _ -> htmlImageUrl = current }
            )
        } else {
            PagingLoadState(
                state = notifications.loadState.refresh,
                onRetryClick = notifications::refresh,
            )
        }
    }
}

@Composable
private fun NotificationList(
    notifications: LazyPagingItems<NotificationInfo.Reply>,
    sizedHtmls: SnapshotStateMap<String, String>,
    onUriClick: (String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    loadHtmlImage: (String, String, String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = notifications.rememberLazyListState()
    val refreshing = remember(notifications.loadState.refresh) {
        with(notifications.loadState.refresh) {
            !endOfPaginationReached && this is LoadState.Loading
        }
    }

    ClickHandler(enabled = !refreshing) {
        coroutineScope.launch {
            if (lazyListState.isScrollInProgress) {
                lazyListState.animateScrollToItem(0)
                notifications.refresh()
            } else if (lazyListState.canScrollBackward) {
                lazyListState.animateScrollToItem(0)
            } else {
                notifications.refresh()
            }
        }
    }

    PullToRefresh(refreshing = refreshing, onRefresh = { notifications.refresh() }) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            //pagingRefreshItem(lazyPagingItems = notifications)
            itemsIndexed(items = notifications, key = { _, item -> item.id }) { _, item ->
                item?.let {
                    val tag = "notification#${item.id}"
                    NotificationItem(
                        item = item,
                        content = sizedHtmls[tag] ?: item.content,
                        onUriClick = onUriClick,
                        onUserAvatarClick = onUserAvatarClick,
                        loadHtmlImage = { html, src -> loadHtmlImage(tag, html, src) },
                        onHtmlImageClick = onHtmlImageClick,
                    )
                }
            }
            pagingAppendMoreItem(lazyPagingItems = notifications)
        }
    }

    val isRefreshing = notifications.loadState.refresh is LoadState.Loading
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            lazyListState.scrollToItem(0)
        }
    }
}

@Composable
private fun NotificationItem(
    item: NotificationInfo.Reply,
    content: String,
    onUriClick: (String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    loadHtmlImage: (String, String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
) {
    val contentColor = LocalContentColor.current
    val titleText = remember(item) {
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(item.name)
            }
            append(" ")
            append(item.title)
        }
    }
    Box(modifier = Modifier.clickable { onUriClick(item.link) }) {
        Row(modifier = Modifier.padding(16.dp)) {
            TopicUserAvatar(
                userName = item.name,
                userAvatar = item.avatar,
                onUserAvatarClick = { onUserAvatarClick(item.name, item.avatar) })

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titleText,
                    color = contentColor.copy(alpha = ContentAlpha.high),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    item.time,
                    color = contentColor.copy(alpha = ContentAlpha.medium),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End),
                )
                if (item.content.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    HtmlContent(
                        content = content,
                        onUriClick = { onUriClick(V2exUri.fixUriWithTopicPath(it, item.link)) },
                        loadImage = loadHtmlImage,
                        onHtmlImageClick = onHtmlImageClick,
                        onClick = { onUriClick(item.link) },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                    )
                }
            }
        }
        ListDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

