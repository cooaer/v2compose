package io.github.v2compose.ui.topic.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.v2compose.R
import io.github.v2compose.ui.common.BackIcon
import io.github.v2compose.ui.topic.bean.TopicInfoWrapper


enum class TopicMenuItem(val icon: ImageVector, @StringRes val textResId: Int) {
    Append(Icons.Rounded.NoteAdd, R.string.topic_menu_item_append),
    Favorite(Icons.Rounded.BookmarkAdd, R.string.topic_menu_item_favorite),
    Favorited(Icons.Rounded.BookmarkAdded, R.string.topic_menu_item_unfavorite),
    More(Icons.Rounded.MoreVert, R.string.topic_menu_item_more),
    Thanks(Icons.Rounded.FavoriteBorder, R.string.menu_item_thank),
    Thanked(Icons.Rounded.Favorite, R.string.menu_item_unthank),
    Ignore(Icons.Rounded.VisibilityOff, R.string.topic_menu_item_ignore),
    Ignored(Icons.Rounded.Visibility, R.string.topic_menu_item_unignore),
    Report(Icons.Rounded.Report, R.string.topic_menu_item_report),
    Reported(Icons.Outlined.Report, R.string.topic_menu_item_reported),
    Share(Icons.Rounded.Share, R.string.topic_menu_item_share),
    OpenInBrowser(Icons.Rounded.OpenInBrowser, R.string.topic_menu_item_open_in_browser),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicTopBar(
    isLoggedIn: Boolean,
    topicInfo: TopicInfoWrapper,
    showTopicTitle: Boolean,
    onBackClick: () -> Unit,
    onMenuClick: (TopicMenuItem) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = with(topicInfo.topic?.headerInfo?.title) {
                    if (showTopicTitle && this != null) this else stringResource(R.string.topic)
                },
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = { BackIcon(onBackClick) },
        actions = {
            TopicTopBarActions(isLoggedIn, topicInfo, onMenuClick)
        },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun TopicTopBarActions(
    isLoggedIn: Boolean,
    topicInfo: TopicInfoWrapper,
    onMenuClick: (TopicMenuItem) -> Unit
) {
    val contentColor = LocalContentColor.current
    if (isLoggedIn) {
        val topicMenuItem = remember(topicInfo) {
            if (topicInfo.isFavorited) TopicMenuItem.Favorited else TopicMenuItem.Favorite
        }
        TextButton(
            onClick = { onMenuClick(topicMenuItem) },
            modifier = Modifier
                .height(48.dp)
                .widthIn(min = 48.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    topicMenuItem.icon,
                    contentDescription = topicMenuItem.name,
                    tint = contentColor
                )
                val favoriteCount = topicInfo.favoriteCount
                if (favoriteCount > 0) {
                    Text(
                        favoriteCount.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor
                    )
                }
            }
        }

    }

    var moreExpanded by remember { mutableStateOf(false) }
    IconButton(onClick = { moreExpanded = true }) {
        Icon(TopicMenuItem.More.icon, contentDescription = "more")
    }

    val topicMenuItems: List<TopicMenuItem> = remember(isLoggedIn, topicInfo) {
        mutableListOf<TopicMenuItem>().apply {
            if (isLoggedIn) {
                if (topicInfo.topic != null) {
                    if (topicInfo.topic.headerInfo.canAppend()) {
                        add(TopicMenuItem.Append)
                    }
                    add(if (topicInfo.isThanked) TopicMenuItem.Thanked else TopicMenuItem.Thanks)
                }
                add(if (topicInfo.isIgnored) TopicMenuItem.Ignored else TopicMenuItem.Ignore)
                if (topicInfo.topic?.hasReportPermission() == true) {
                    add(if (topicInfo.isReported) TopicMenuItem.Reported else TopicMenuItem.Report)
                }
            }
            addAll(listOf(TopicMenuItem.Share, TopicMenuItem.OpenInBrowser))
        }
    }
    DropdownMenu(expanded = moreExpanded, onDismissRequest = { moreExpanded = false }) {
        topicMenuItems.forEach { menuItem ->
            DropdownMenuItem(text = { Text(stringResource(id = menuItem.textResId)) },
                leadingIcon = {
                    Icon(menuItem.icon, menuItem.name)
                },
                onClick = {
                    onMenuClick(menuItem)
                    moreExpanded = false
                })
        }
    }
}