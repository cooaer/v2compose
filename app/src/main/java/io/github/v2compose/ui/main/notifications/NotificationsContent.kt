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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import io.github.v2compose.network.bean.NotificationInfo
import io.github.v2compose.ui.common.*

private const val TAG = "NotificationsContent"

@Composable
fun NotificationsContent(
    onLoginClick: () -> Unit,
    onUriClick: (String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoggedIn) {
            val unreadNotifications by viewModel.unreadNotifications.collectAsStateWithLifecycle()
            val notifications = viewModel.notifications.collectAsLazyPagingItems()

            LaunchedEffect(unreadNotifications) {
                if (unreadNotifications > 0) {
                    notifications.refresh()
                }
            }
            NotificationList(
                notifications = notifications,
                onUriClick = onUriClick,
                onUserAvatarClick = onUserAvatarClick
            )
        } else {
            ElevatedButton(onClick = onLoginClick, modifier = Modifier.align(Alignment.Center)) {
                Text(stringResource(id = R.string.login))
            }
        }
    }
}

@Composable
private fun NotificationList(
    notifications: LazyPagingItems<NotificationInfo.Reply>,
    onUriClick: (String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
) {
    val refreshing = remember(notifications.loadState.refresh) {
        with(notifications.loadState.refresh) {
            !endOfPaginationReached && this is LoadState.Loading
        }
    }

    PullToRefresh(refreshing = refreshing, onRefresh = { notifications.refresh() }) {
        LazyColumn() {
//            pagingRefreshItem(lazyPagingItems = notifications)
            itemsIndexed(items = notifications, key = { _, item -> item.id }) { _, item ->
                item?.let {
                    NotificationItem(
                        item = item,
                        onUriClick = onUriClick,
                        onUserAvatarClick = onUserAvatarClick
                    )
                }
            }
            pagingAppendMoreItem(lazyPagingItems = notifications)
        }
    }
}

@Composable
private fun NotificationItem(
    item: NotificationInfo.Reply,
    onUriClick: (String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
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
                        content = item.content,
                        onUriClick = onUriClick,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
        ListDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

