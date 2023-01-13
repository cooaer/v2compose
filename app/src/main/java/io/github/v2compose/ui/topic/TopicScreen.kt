package io.github.v2compose.ui.topic

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.cooaer.htmltext.HtmlText
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.network.bean.TopicInfo.ContentInfo.Supplement
import io.github.v2compose.network.bean.TopicInfo.Reply
import io.github.v2compose.ui.common.PagingAppendMore
import io.github.v2compose.ui.common.SegmentedControl
import io.github.v2compose.ui.common.SimpleTopic
import io.github.v2compose.ui.common.TopicUserAvatar
import io.github.v2compose.util.isUserPath

@Composable
fun TopicRoute(
    onBackClick: () -> Unit,
    viewModel: TopicViewModel = hiltViewModel(),
    screenState: TopicScreenState = rememberTopicScreenState(),
) {
    var repliesOrder by remember { mutableStateOf(RepliesOrder.Negative) }

    val lazyPagingItems = remember(repliesOrder) {
        viewModel.topicItemFlow(repliesOrder == RepliesOrder.Negative)
    }.collectAsLazyPagingItems()

    val topicInfo = if (lazyPagingItems.itemCount > 0) {
        lazyPagingItems.peek(0)?.let {
            if (it is TopicInfo) it else null
        }
    } else null

    val onMenuClick = fun(item: MenuItem) {
        if (topicInfo == null) return
        when (item) {
            MenuItem.Share -> {
                screenState.share(
                    topicInfo.headerInfo.title,
                    Constants.topicUrl(viewModel.topicArgs.topicId)
                )
            }
            MenuItem.OpenInBrowser -> {
                screenState.openInBrowser(Constants.topicUrl(viewModel.topicArgs.topicId))
            }
            else -> {}
        }
    }

    TopicScreen(
        repliesOrder = repliesOrder,
        topicItems = lazyPagingItems,
        onBackClick = onBackClick,
        onMenuClick = onMenuClick,
        onAvatarClick = { userName, avatar -> },
        onNodeClick = { nodeId, nodeName -> },
        onRepliedOrderClick = { repliesOrder = it },
        onContentUriClick = { uri -> screenState.openUri(uri) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicScreen(
    repliesOrder: RepliesOrder,
    topicItems: LazyPagingItems<Any>,
    onBackClick: () -> Unit,
    onMenuClick: (MenuItem) -> Unit,
    onAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onRepliedOrderClick: (RepliesOrder) -> Unit,
    onContentUriClick: (String) -> Unit,
) {
    val density = LocalDensity.current

    val topicInfo = if (topicItems.itemCount > 0) {
        topicItems.peek(0)?.let {
            if (it is TopicInfo) it else null
        }
    } else null

    val scrollState = rememberLazyListState()
    val topBarShowTopicTitle = remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 ||
                    scrollState.firstVisibleItemScrollOffset < with(density) { -64.dp.toPx() }
        }
    }

    Scaffold(
        topBar = {
            TopicTopBar(
                topicInfo = topicInfo,
                showTopicTitle = topBarShowTopicTitle.value,
                onBackClick = onBackClick,
                onMenuClick = onMenuClick
            )
        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = { onMenuClick(MenuItem.Comment) }) {
//                Icon(
//                    MenuItem.Comment.icon,
//                    "comment",
//                )
//            }
//        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopicList(
                repliesOrder = repliesOrder,
                topicItems = topicItems,
                lazyListState = scrollState,
                onAvatarClick = onAvatarClick,
                onNodeClick = onNodeClick,
                onRepliedOrderClick = onRepliedOrderClick,
                onContentUriClick = onContentUriClick
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopicList(
    repliesOrder: RepliesOrder,
    topicItems: LazyPagingItems<Any>,
    lazyListState: LazyListState,
    onAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onRepliedOrderClick: (RepliesOrder) -> Unit,
    onContentUriClick: (String) -> Unit,
) {
    var clickedReplyUser by remember { mutableStateOf<Pair<Int, String>?>(null) }

    clickedReplyUser?.let {
        UserRepliesDialog(
            topicItems = topicItems,
            reverseOrder = repliesOrder == RepliesOrder.Negative,
            clickedReplyUser = it,
            onDismissRequest = { clickedReplyUser = null },
            onAvatarClick = onAvatarClick,
            onContentUriClick = onContentUriClick,
        )
    }

    LazyColumn(state = lazyListState) {
        for (index in 0 until topicItems.itemCount) {
            when (val topicItem = topicItems.peek(index)) {
                is TopicInfo -> {
                    item(key = "title", contentType = "title") {
                        TopicTitle(
                            topicInfo = topicItem,
                            onAvatarClick = onAvatarClick,
                            onNodeClick = onNodeClick
                        )
                    }

                    if (topicItem.contentInfo.content.isNotEmpty()) {
                        item(key = "content", contentType = "content") {
                            TopicContent(
                                content = topicItem.contentInfo.content,
                                onContentUriClick = onContentUriClick
                            )
                        }
                    }

                    if (topicItem.contentInfo.supplements.isNotEmpty()) {
                        itemsIndexed(
                            items = topicItem.contentInfo.supplements,
                            key = { supplementIndex, item -> "supplement:$supplementIndex" },
                            contentType = { _, _ -> "supplement" }
                        ) { supplementIndex, item ->
                            TopicSupplement(
                                index = supplementIndex,
                                supplement = item,
                                onContentUriClick = onContentUriClick
                            )
                        }
                    }

                    if (topicItem.contentInfo.content.isNotEmpty() && topicItem.contentInfo.supplements.isEmpty()) {
                        item(key = "divider#onRepliesBar", contentType = "divider") {
                            Divider(
                                modifier = Modifier.padding(end = 16.dp),
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                            )
                        }
                    }

                    stickyHeader(key = "repliesBar", contentType = "repliesBar") {
                        TopicRepliesBar(
                            replyNum = topicItem.headerInfo.commentNum,
                            repliesOrder = repliesOrder,
                            onRepliedOrderClick = onRepliedOrderClick,
                        )
                    }
                }
                is Reply -> {
                    item(key = "reply#$index", contentType = "reply") {
                        TopicReply(
                            index = index,
                            reply = topicItem,
                            onAvatarClick = onAvatarClick,
                            onContentUriClick = { uri ->
                                if (uri.isUserPath()) {
                                    clickedReplyUser = Pair(index, uri)
                                } else {
                                    onContentUriClick(uri)
                                }
                            }
                        )
                    }
                }
            }
        }
        if (!topicItems.loadState.append.endOfPaginationReached) {
            item(key = "appendMore#${topicItems.itemCount}", contentType = "appendMore") {
                PagingAppendMore(lazyPagingItems = topicItems) {
                    topicItems.retry()
                }
            }
        }
    }
}

@Composable
private fun UserRepliesDialog(
    topicItems: LazyPagingItems<Any>,
    reverseOrder: Boolean,
    clickedReplyUser: Pair<Int, String>,
    onDismissRequest: () -> Unit,
    onAvatarClick: (String, String) -> Unit,
    onContentUriClick: (String) -> Unit,
) {
    val currentIndex = clickedReplyUser.first
    val userName = clickedReplyUser.second.removePrefix("/member/")
    val userReplies = topicItems.itemSnapshotList.let {
        if (reverseOrder) it.subList(currentIndex, it.size).reversed() else it.subList(
            0,
            currentIndex
        )
    }.filter { it is Reply && it.userName == userName } as List<Reply>

    if (userReplies.isEmpty()) {
        onDismissRequest()
        return
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(maxHeight = 560.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(
                        start = 24.dp,
                        top = 24.dp,
                        end = 24.dp,
                        bottom = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    with(userReplies.first()) {
                        TopicUserAvatar(
                            userName = userName,
                            avatar = avatar,
                            onAvatarClick = { onAvatarClick(userName, avatar) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(id = R.string.user_previous_replies, userName),
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    val items = if (reverseOrder) userReplies.reversed() else userReplies
                    itemsIndexed(items = items, key = { _, item -> item }) { index, item ->
                        UserReply(index, reply = item, onContentUriClick = onContentUriClick)
                    }
                }
            }
        }
    }
}


@Composable
private fun UserReply(index: Int, reply: Reply, onContentUriClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        if (index != 0) {
            Divider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
        }
        Spacer(Modifier.height(8.dp))
        HtmlContent(html = reply.replyContent, onUriClick = onContentUriClick)
        Row {
            ReplyFloor(floor = reply.floor)
            Spacer(modifier = Modifier.width(8.dp))
            PublishedTime(time = reply.time)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
private fun TopicTitle(
    topicInfo: TopicInfo?,
    onAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit
) {
    if (topicInfo == null) return
    SimpleTopic(
        userName = topicInfo.headerInfo.userName,
        avatar = topicInfo.headerInfo.avatar,
        time = topicInfo.headerInfo.time,
        replyNum = topicInfo.headerInfo.commentNum,
        nodeId = topicInfo.headerInfo.tagId,
        nodeName = topicInfo.headerInfo.tag,
        title = topicInfo.headerInfo.title,
        onAvatarClick = {
            onAvatarClick(
                topicInfo.headerInfo.userName,
                topicInfo.headerInfo.avatar
            )
        },
        onNodeClick = {
            onNodeClick(topicInfo.headerInfo.tagId, topicInfo.headerInfo.tag)
        }
    )
}

@Composable
private fun TopicContent(content: String, onContentUriClick: (String) -> Unit) {
    HtmlContent(
        html = content,
        selectable = false,
        textStyle = TextStyle.Default.copy(fontSize = 15.sp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        onUriClick = onContentUriClick,
    )
}

@Composable
private fun TopicSupplement(
    index: Int,
    supplement: Supplement,
    onContentUriClick: (String) -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val leftBorderColor = MaterialTheme.colorScheme.tertiary
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Divider(modifier = Modifier.align(alignment = Alignment.BottomCenter))
        Column(
            modifier = Modifier
                .drawBehind {
                    drawRect(color = backgroundColor)
                    drawRect(color = leftBorderColor, size = size.copy(width = 4.dp.toPx()))
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                supplement.title,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                style = MaterialTheme.typography.labelMedium
            )
            HtmlContent(
                html = supplement.content,
                selectable = false,
                textStyle = TextStyle.Default.copy(fontSize = 15.sp),
                modifier = Modifier.fillMaxWidth(),
                onUriClick = onContentUriClick,
            )
        }
    }
}

enum class RepliesOrder(@StringRes val textResId: Int) {
    //最新的回复排在最前面
    Negative(R.string.replies_order_negative),

    //最早的回复排在最前面
    Positive(R.string.replies_order_positive),
}

@Composable
private fun TopicRepliesBar(
    replyNum: String,
    repliesOrder: RepliesOrder,
    onRepliedOrderClick: (RepliesOrder) -> Unit
) {
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.n_comment, replyNum),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        val orders = RepliesOrder.values().toList()
        SegmentedControl(
            segments = orders,
            selectedSegment = repliesOrder,
            onSegmentSelected = onRepliedOrderClick,
            modifier = Modifier.sizeIn(maxWidth = 108.dp)
        ) { order ->
            val textColorAlpha = if (repliesOrder == order) 1f else 0.6f
            Text(
                stringResource(order.textResId),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = textColorAlpha)
            )
        }
    }
}

@Composable
private fun TopicReply(
    index: Int,
    reply: Reply,
    onAvatarClick: (String, String) -> Unit,
    onContentUriClick: (String) -> Unit
) {
    Row(modifier = Modifier.padding(start = 16.dp)) {
        TopicUserAvatar(
            userName = reply.userName,
            avatar = reply.avatar,
            modifier = Modifier.padding(top = 12.dp),
            onAvatarClick = { onAvatarClick(reply.userName, reply.avatar) })
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(top = 12.dp, end = 16.dp)) {
                UserName(userName = reply.userName)

                HtmlContent(
                    html = reply.replyContent,
                    selectable = false,
                    textStyle = TextStyle.Default.copy(fontSize = 15.sp),
                    onUriClick = onContentUriClick,
                )
                ReplyFloor(
                    floor = reply.floor,
                    modifier = Modifier.padding(bottom = 12.dp, top = 4.dp)
                )
            }
            Divider(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
//            IconButton(
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(end = 4.dp),
//                onClick = { /*TODO*/ }) {
//                Icon(
//                    imageVector = Icons.Rounded.MoreVert,
//                    contentDescription = "more",
//                    tint = MaterialTheme.colorScheme.secondary
//                )
//            }
//            IconButton(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(end = 4.dp),
//                onClick = { /*TODO*/ }) {
//                Icon(
//                    imageVector = Icons.Rounded.FavoriteBorder,
//                    contentDescription = "favorite",
//                    tint = MaterialTheme.colorScheme.secondary
//                )
//            }
        }
    }
}

@Composable
private fun UserName(userName: String, modifier: Modifier = Modifier) {
    Text(
        userName,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(
            lineHeightStyle = LineHeightStyle(
                trim = LineHeightStyle.Trim.FirstLineTop,
                alignment = LineHeightStyle.Alignment.Center,
            )
        ),
    )
}

@Composable
private fun PublishedTime(time: String, modifier: Modifier = Modifier) {
    Text(
        time,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    )
}

@Composable
private fun ReplyFloor(floor: String, modifier: Modifier = Modifier) {
    Text(
        floor,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    )
}


private enum class MenuItem(val icon: ImageVector, @StringRes val textResId: Int) {
    Sort(Icons.Rounded.Sort, R.string.topic_menu_item_sort),
    BookmarkAdd(Icons.Rounded.BookmarkAdd, R.string.topic_menu_item_add_bookmark),
    Bookmark(Icons.Rounded.Bookmark, R.string.topic_menu_item_bookmark),
    More(Icons.Rounded.MoreVert, R.string.topic_menu_item_more),
    Comment(Icons.Rounded.Comment, R.string.topic_menu_item_comment),
    Thanks(Icons.Rounded.FavoriteBorder, R.string.topic_menu_item_thanks),
    Thanked(Icons.Rounded.Favorite, R.string.topic_menu_item_thanked),
    Report(Icons.Rounded.Report, R.string.topic_menu_item_report),
    Reported(Icons.Outlined.Report, R.string.topic_menu_item_reported),
    Share(Icons.Rounded.Share, R.string.topic_menu_item_share),
    OpenInBrowser(Icons.Rounded.OpenInBrowser, R.string.topic_menu_item_open_in_browser),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicTopBar(
    topicInfo: TopicInfo?,
    showTopicTitle: Boolean,
    onBackClick: () -> Unit,
    onMenuClick: (MenuItem) -> Unit
) {
    TopAppBar(
        modifier = Modifier.shadow(elevation = 4.dp),
        title = {
            Text(
                text = with(topicInfo?.headerInfo?.title) {
                    if (showTopicTitle && this != null) this else stringResource(R.string.topic)
                },
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "back"
                )
            }
        },
        actions = {
//            listOf(MenuItem.Sort).forEach { menuItem ->
//                IconButton(onClick = { onMenuClick(menuItem) }) {
//                    Icon(menuItem.icon, contentDescription = menuItem.name)
//                }
//            }
            var moreExpanded by remember { mutableStateOf(false) }
            IconButton(onClick = { moreExpanded = true }) {
                Icon(MenuItem.More.icon, contentDescription = "")
            }
            DropdownMenu(expanded = moreExpanded, onDismissRequest = { moreExpanded = false }) {
                listOf(
//                    if (topicInfo?.headerInfo?.hadThanked() == true) MenuItem.Thanked else MenuItem.Thanks,
//                    if (topicInfo?.hasReported() == true) MenuItem.Reported else MenuItem.Report,
                    MenuItem.Share,
                    MenuItem.OpenInBrowser
                ).forEach { menuItem ->
                    DropdownMenuItem(
                        text = { Text(stringResource(id = menuItem.textResId)) },
                        leadingIcon = { Icon(menuItem.icon, menuItem.name) },
                        onClick = {
                            onMenuClick(menuItem)
                            moreExpanded = false
                        })
                }
            }
        })
}


@Composable
private fun HtmlContent(
    html: String,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    onUriClick: (uri: String) -> Unit
) {
    val uriHandler = remember(onUriClick) {
        object : UriHandler {
            override fun openUri(uri: String) {
                onUriClick(uri)
            }
        }
    }
    CompositionLocalProvider(LocalUriHandler provides uriHandler) {
        HtmlText(html = html, modifier = modifier, selectable = selectable, textStyle = textStyle)
    }
}