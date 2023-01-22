package io.github.v2compose.ui.topic

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.v2compose.R
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.network.bean.TopicInfo.ContentInfo.Supplement
import io.github.v2compose.network.bean.TopicInfo.Reply
import io.github.v2compose.ui.common.*
import io.github.v2compose.util.isUserPath
import kotlinx.coroutines.launch

private const val TAG = "TopicScreen"

@Composable
fun TopicRoute(
    onBackClick: () -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
    viewModel: TopicViewModel = hiltViewModel(),
    screenState: TopicScreenState = rememberTopicScreenState(),
) {
    val repliesReversed by viewModel.repliesReversed.collectAsStateWithLifecycle(initialValue = true)

    val lazyPagingItems = viewModel.topicItemFlow.collectAsLazyPagingItems()

    val topicInfo = if (lazyPagingItems.itemCount > 0) {
        lazyPagingItems.peek(0)?.let {
            if (it is TopicInfo) it else null
        }
    } else null



    TopicScreen(
        topicInfo = topicInfo,
        repliesOrder = if (repliesReversed) RepliesOrder.Negative else RepliesOrder.Positive,
        topicItems = lazyPagingItems,
        onBackClick = onBackClick,
        onMenuClick = { screenState.onMenuClick(it, viewModel.topicArgs, topicInfo) },
        onUserAvatarClick = onUserAvatarClick,
        onNodeClick = onNodeClick,
        onRepliedOrderClick = { viewModel.toggleRepliesReversed() },
        openUri = openUri,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicScreen(
    topicInfo: TopicInfo?,
    repliesOrder: RepliesOrder,
    topicItems: LazyPagingItems<Any>,
    onBackClick: () -> Unit,
    onMenuClick: (MenuItem) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onRepliedOrderClick: (RepliesOrder) -> Unit,
    openUri: (String) -> Unit,
) {
    val density = LocalDensity.current

    val scrollState = topicItems.rememberLazyListState()
    val topBarShowTopicTitle = remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 ||
                    scrollState.firstVisibleItemScrollOffset < with(density) { -64.dp.toPx() }
        }
    }
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopicTopBar(
                topicInfo = topicInfo,
                showTopicTitle = topBarShowTopicTitle.value,
                onBackClick = onBackClick,
                onMenuClick = onMenuClick,
                scrollBehavior = topAppBarScrollBehavior
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
                topicInfo = topicInfo,
                repliesOrder = repliesOrder,
                topicItems = topicItems,
                lazyListState = scrollState,
                onUserAvatarClick = onUserAvatarClick,
                onNodeClick = onNodeClick,
                onRepliedOrderClick = onRepliedOrderClick,
                openUri = openUri,
                modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopicList(
    topicInfo: TopicInfo?,
    repliesOrder: RepliesOrder,
    topicItems: LazyPagingItems<Any>,
    lazyListState: LazyListState,
    onUserAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onRepliedOrderClick: (RepliesOrder) -> Unit,
    openUri: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var clickedReplyUser by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var repliesBarIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    clickedReplyUser?.let {
        UserRepliesDialog(
            topicItems = topicItems,
            clickedReplyUser = it,
            onDismissRequest = { clickedReplyUser = null },
            onUserAvatarClick = onUserAvatarClick,
            openUri = openUri,
        )
    }

    LazyColumn(state = lazyListState, modifier = modifier) {
        pagingRefreshItem(topicItems)
        if (topicInfo != null) {
            if (!topicInfo.isValid) {
                //TODO 非登录状态，触发某些关键字（如 fg ），重定向到首页，导致解析失败
                return@LazyColumn
            }
            var listItemIndex = 0

            item(key = "title", contentType = "title") {
                TopicTitle(
                    topicInfo = topicInfo,
                    onUserAvatarClick = onUserAvatarClick,
                    onNodeClick = onNodeClick
                )
            }
            listItemIndex++

            if (topicInfo.contentInfo.content.isNotEmpty()) {
                item(key = "content", contentType = "content") {
                    TopicContent(
                        content = topicInfo.contentInfo.content,
                        openUri = openUri
                    )
                }
                listItemIndex++
            }

            if (topicInfo.contentInfo.supplements.isNotEmpty()) {
                itemsIndexed(
                    items = topicInfo.contentInfo.supplements,
                    key = { supplementIndex, item -> "supplement:$supplementIndex" },
                    contentType = { _, _ -> "supplement" }
                ) { supplementIndex, item ->
                    TopicSupplement(
                        index = supplementIndex,
                        supplement = item,
                        openUri = openUri
                    )
                }
                listItemIndex += topicInfo.contentInfo.supplements.size
            }

            if (topicInfo.contentInfo.content.isNotEmpty() && topicInfo.contentInfo.supplements.isEmpty()) {
                item(key = "divider#onRepliesBar", contentType = "divider") {
                    ListDivider(
                        modifier = Modifier.padding(end = 16.dp),
                    )
                }
                listItemIndex++
            }

            repliesBarIndex = listItemIndex
            stickyHeader(key = "repliesBar", contentType = "repliesBar") {
                TopicRepliesBar(
                    replyNum = topicInfo.headerInfo.commentNum,
                    repliesOrder = repliesOrder,
                    onRepliedOrderClick = {
                        onRepliedOrderClick(it)
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(repliesBarIndex + 1)
                        }
                    },
                )
            }
        }
        itemsIndexed(items = topicItems, key = { index, item -> item }) { index, item ->
            if (item is Reply) {
                TopicReply(
                    index = index,
                    reply = item,
                    onUserAvatarClick = onUserAvatarClick,
                    openUri = { uri ->
                        if (uri.isUserPath()) {
                            clickedReplyUser = Pair(item.floor, uri)
                        } else {
                            openUri(uri)
                        }
                    }
                )
            }
        }
        pagingAppendMoreItem(lazyPagingItems = topicItems)
    }
}

@Composable
private fun UserRepliesDialog(
    topicItems: LazyPagingItems<Any>,
    clickedReplyUser: Pair<Int, String>,
    onDismissRequest: () -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
) {
    val floor = clickedReplyUser.first
    val userName = clickedReplyUser.second.removePrefix("/member/")
    val userReplies =
        topicItems.itemSnapshotList.filter { it is Reply && it.floor < floor && it.userName == userName } as List<Reply>

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
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    with(userReplies.first()) {
                        TopicUserAvatar(
                            userName = userName,
                            userAvatar = avatar,
                            onUserAvatarClick = { onUserAvatarClick(userName, avatar) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(id = R.string.user_previous_replies, userName),
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                var innerClickedReplyUser by remember { mutableStateOf<Pair<Int, String>?>(null) }

                innerClickedReplyUser?.let {
                    UserRepliesDialog(
                        topicItems = topicItems,
                        clickedReplyUser = it,
                        onDismissRequest = { innerClickedReplyUser = null },
                        onUserAvatarClick = onUserAvatarClick,
                        openUri = openUri
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(items = userReplies, key = { _, item -> item }) { index, item ->
                        UserReply(index, reply = item, openUri = {
                            if (it.isUserPath()) {
                                innerClickedReplyUser = Pair(item.floor, it)
                            } else {
                                openUri(it)
                            }
                        })
                    }
                }
            }
        }
    }
}


@Composable
private fun UserReply(index: Int, reply: Reply, openUri: (String) -> Unit) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        if (index != 0) {
            Divider()
        }
        Spacer(Modifier.height(8.dp))
        HtmlContent(html = reply.replyContent, onUriClick = openUri)
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
    onUserAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit
) {
    if (topicInfo == null) return
    SimpleTopic(
        userName = topicInfo.headerInfo.userName,
        userAvatar = topicInfo.headerInfo.avatar,
        time = topicInfo.headerInfo.time,
        replyNum = topicInfo.headerInfo.commentNum,
        nodeId = topicInfo.headerInfo.tagId,
        nodeName = topicInfo.headerInfo.tag,
        title = topicInfo.headerInfo.title,
        onUserAvatarClick = {
            onUserAvatarClick(
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
private fun TopicContent(content: String, openUri: (String) -> Unit) {
    HtmlContent(
        html = content,
        selectable = false,
        textStyle = TextStyle.Default.copy(fontSize = 15.sp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        onUriClick = openUri,
    )
}

@Composable
private fun TopicSupplement(
    index: Int,
    supplement: Supplement,
    openUri: (String) -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val leftBorderColor = MaterialTheme.colorScheme.tertiary
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        ListDivider(modifier = Modifier.align(alignment = Alignment.BottomCenter))
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
                onUriClick = openUri,
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
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
        TopicUserAvatar(
            userName = reply.userName,
            userAvatar = reply.avatar,
            modifier = Modifier.padding(top = 12.dp),
            onUserAvatarClick = { onUserAvatarClick(reply.userName, reply.avatar) })
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(top = 12.dp, end = 16.dp)) {
                UserName(userName = reply.userName)

                HtmlContent(
                    html = reply.replyContent,
                    selectable = false,
                    textStyle = TextStyle.Default.copy(fontSize = 15.sp),
                    onUriClick = openUri,
                    modifier = Modifier.fillMaxWidth()
                )
                ReplyFloor(
                    floor = reply.floor,
                    modifier = Modifier.padding(bottom = 12.dp, top = 4.dp)
                )
            }
            ListDivider(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.BottomCenter),
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
private fun ReplyFloor(floor: Int, modifier: Modifier = Modifier) {
    Text(
        stringResource(id = R.string.n_floor, floor),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    )
}


enum class MenuItem(val icon: ImageVector, @StringRes val textResId: Int) {
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
    onMenuClick: (MenuItem) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
//        modifier = Modifier.shadow(elevation = 4.dp),
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
        navigationIcon = { BackIcon(onBackClick) },
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
        },
        scrollBehavior = scrollBehavior,
    )
}

