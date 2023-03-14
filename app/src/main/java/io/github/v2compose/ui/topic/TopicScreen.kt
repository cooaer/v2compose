package io.github.v2compose.ui.topic

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.v2compose.R
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.network.bean.TopicInfo.ContentInfo.Supplement
import io.github.v2compose.network.bean.TopicInfo.Reply
import io.github.v2compose.ui.HandleSnackbarMessage
import io.github.v2compose.ui.common.*
import io.github.v2compose.ui.gallery.composables.PopupImage
import io.github.v2compose.ui.topic.composables.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "TopicScreen"

@Composable
fun TopicScreenRoute(
    onBackClick: () -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onAddSupplementClick: (String) -> Unit,
    openUri: (String) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
    viewModel: TopicViewModel = hiltViewModel(),
    screenState: TopicScreenState = rememberTopicScreenState(),
) {
    val args = viewModel.topicArgs
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val repliesReversed by viewModel.repliesReversed.collectAsStateWithLifecycle(initialValue = true)
    val highlightOpReply by viewModel.highlightOpReply.collectAsStateWithLifecycle()
    val topicItems = viewModel.topicItems.collectAsLazyPagingItems()

    val topicInfo = if (topicItems.itemCount > 0) {
        topicItems.peek(0)?.let {
            if (it is TopicInfo) it.also { viewModel.updateTopicInfoWrapper(topic = it) } else null
        }
    } else null

    val topicInfoWrapper by viewModel.topicInfoWrapper
    val replyWrappers = viewModel.replyWrappers
    val replyTopicState by viewModel.replyTopicState.collectAsStateWithLifecycle()
    var htmlImageUrl by rememberSaveable { mutableStateOf("") }

    if (htmlImageUrl.isNotEmpty()) {
        PopupImage(imageUrl = htmlImageUrl) {
            htmlImageUrl = ""
        }
    }

    HandleReplyTopicState(replyTopicState, topicItems, openUri)

    HandleSnackbarMessage(viewModel, screenState)

    TopicScreen(
        isLoggedIn = isLoggedIn,
        topicInfo = topicInfoWrapper,
        repliesOrder = if (repliesReversed) RepliesOrder.Negative else RepliesOrder.Positive,
        topicItems = topicItems,
        sizedHtmls = viewModel.sizedHtmls,
        replyWrappers = replyWrappers,
        replyTopicState = replyTopicState,
        highlightOpReply = highlightOpReply,
        onBackClick = onBackClick,
        onTopicMenuClick = {
            when (it) {
                TopicMenuItem.Favorite -> viewModel.favoriteTopic()
                TopicMenuItem.Favorited -> viewModel.unFavoriteTopic()
                TopicMenuItem.Append -> onAddSupplementClick(args.topicId)
                TopicMenuItem.Thanks -> viewModel.thanksTopic()
                TopicMenuItem.Thanked -> viewModel.unThanksTopic()
                TopicMenuItem.Ignore -> viewModel.ignoreTopic()
                TopicMenuItem.Ignored -> viewModel.unIgnoreTopic()
                TopicMenuItem.Report -> viewModel.reportTopic()
                TopicMenuItem.Reported -> viewModel.unReportTopic()
                else -> screenState.onMenuClick(it, viewModel.topicArgs, topicInfo)
            }
        },
        onUserAvatarClick = onUserAvatarClick,
        onNodeClick = onNodeClick,
        onRepliedOrderClick = { viewModel.toggleRepliesReversed() },
        openUri = openUri,
        onReplyMenuItemClick = { menuItem, reply ->
            when (menuItem) {
                ReplyMenuItem.Thank -> viewModel.thankReply(reply)
                ReplyMenuItem.Thanked -> viewModel.unFavoriteReply(reply)
                ReplyMenuItem.Ignore -> viewModel.ignoreReply(reply)
                ReplyMenuItem.Copy -> screenState.copy(reply)
                ReplyMenuItem.HomePage -> onUserAvatarClick(reply.userName, reply.avatar)
                else -> {}
            }
        },
        loadHtmlImage = viewModel::loadHtmlImage,
        onSendComment = viewModel::replyTopic,
        onHtmlImageClick = { current, _ -> htmlImageUrl = current },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicScreen(
    isLoggedIn: Boolean,
    topicInfo: TopicInfoWrapper,
    repliesOrder: RepliesOrder,
    topicItems: LazyPagingItems<Any>,
    sizedHtmls: SnapshotStateMap<String, String>,
    replyWrappers: Map<String, ReplyWrapper>,
    replyTopicState: ReplyTopicState,
    highlightOpReply: Boolean,
    onBackClick: () -> Unit,
    onTopicMenuClick: (TopicMenuItem) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onRepliedOrderClick: (RepliesOrder) -> Unit,
    openUri: (String) -> Unit,
    onReplyMenuItemClick: (ReplyMenuItem, Reply) -> Unit,
    loadHtmlImage: (String, String, String?) -> Unit,
    onSendComment: (String) -> Unit,
    onHtmlImageClick: (String, List<String>) -> Unit,
) {
    val density = LocalDensity.current

    var clickReplyTimes by remember { mutableStateOf(0) }
    var replyInputInitialText by remember { mutableStateOf("") }
    var replyInputCurrentText by remember { mutableStateOf("") }
    var replyInputState by remember { mutableStateOf(ReplyInputState.Collapsed) }

    val scrollState = topicItems.rememberLazyListState()
    val topBarShowTopicTitle by remember(density, scrollState) {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset < with(
                density
            ) { -64.dp.toPx() }
        }
    }
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(enabled = replyInputState == ReplyInputState.Expanded) {
        replyInputState = ReplyInputState.Collapsed
    }

    Scaffold(
        topBar = {
            TopicTopBar(
                isLoggedIn = isLoggedIn,
                topicInfo = topicInfo,
                showTopicTitle = topBarShowTopicTitle,
                onBackClick = onBackClick,
                onMenuClick = onTopicMenuClick,
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        floatingActionButton = {
            val fabType = if (replyTopicState == ReplyTopicState.Loading) FabType.Loading else {
                when (replyInputState) {
                    ReplyInputState.Collapsed -> FabType.Reply
                    ReplyInputState.Expanded -> FabType.Send
                }
            }
            FabButton(visible = isLoggedIn, type = fabType, onClick = { tabType ->
                if (fabType == FabType.Send) {
                    onSendComment(replyInputCurrentText)
                    replyInputState = ReplyInputState.Collapsed
                } else if (tabType == FabType.Reply) {
                    replyInputState = ReplyInputState.Expanded
                }
            })
        },
        contentWindowInsets = WindowInsets.ime.union(WindowInsets.systemBars),
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            TopicList(
                topicInfo = topicInfo,
                repliesOrder = repliesOrder,
                topicItems = topicItems,
                lazyListState = scrollState,
                sizedHtmls = sizedHtmls,
                replyWrappers = replyWrappers,
                isLoggedIn = isLoggedIn,
                highlightOpReply = highlightOpReply,
                onUserAvatarClick = onUserAvatarClick,
                onNodeClick = onNodeClick,
                onRepliedOrderClick = onRepliedOrderClick,
                onTopicReplyClick = {
                    replyInputInitialText = initialReplyText(it)
                    replyInputState = ReplyInputState.Expanded
                    clickReplyTimes++
                },
                openUri = openUri,
                onTopicMenuItemClick = { menuItem, reply ->
                    if (menuItem == ReplyMenuItem.Reply) {
                        replyInputInitialText = initialReplyText(reply)
                        replyInputState = ReplyInputState.Expanded
                    } else {
                        onReplyMenuItemClick(menuItem, reply)
                    }
                },
                loadHtmlImage = loadHtmlImage,
                onHtmlImageClick = onHtmlImageClick,
                modifier = Modifier
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            )

            ReplyInput(
                initialValue = replyInputInitialText,
                clickReplyTimes = clickReplyTimes,
                onValueChanged = { replyInputCurrentText = it },
                state = replyInputState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            if (replyTopicState is ReplyTopicState.Success) {
                LaunchedEffect(true) {
                    replyInputInitialText = ""
                }
            }
        }
    }
}

fun initialReplyText(mention: Reply?): String {
    if (mention == null) return ""
    return "@${mention.userName} "
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopicList(
    topicInfo: TopicInfoWrapper,
    repliesOrder: RepliesOrder,
    topicItems: LazyPagingItems<Any>,
    lazyListState: LazyListState,
    sizedHtmls: SnapshotStateMap<String, String>,
    replyWrappers: Map<String, ReplyWrapper>,
    isLoggedIn: Boolean,
    highlightOpReply: Boolean,
    onUserAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onRepliedOrderClick: (RepliesOrder) -> Unit,
    onTopicReplyClick: (Reply) -> Unit,
    openUri: (String) -> Unit,
    onTopicMenuItemClick: (ReplyMenuItem, Reply) -> Unit,
    loadHtmlImage: (tag: String, html: String, img: String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
    modifier: Modifier = Modifier,
) {
    val clickedUserReplies = rememberMutableStateListOf<List<Reply>>()
    var repliesBarIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val repliesBarHeight = with(LocalDensity.current) { 40.dp.roundToPx() }
    val floorRegex = "#\\d+".toRegex()

    fun clickUriHandler(uri: String, reply: Reply) {
        while (true) {
            if (!uri.startsWith("/member/")) break
            val userName = uri.removePrefix("/member/")
            val userReplies =
                topicItems.itemSnapshotList.filter { it is Reply && it.floor < reply.floor && it.userName == userName } as List<Reply>
            if (userReplies.isEmpty()) break
            clickedUserReplies.add(userReplies)
            return
        }
        if (floorRegex.matches(uri)) {
            Log.d(TAG, "clickUriHandler, uri = $uri")
            val floor = uri.substring(1).toIntOrNull() ?: return
            val floorReply =
                topicItems.itemSnapshotList.firstOrNull { it is Reply && it.floor == floor } as Reply?
                    ?: return
            clickedUserReplies.add(listOf(floorReply))
            return
        }
        openUri(uri)
    }

    clickedUserReplies.forEachIndexed { index, item ->
        UserRepliesDialog(
            userReplies = item,
            sizedHtmls = sizedHtmls,
            onDismissRequest = { clickedUserReplies.removeAt(index) },
            onUserAvatarClick = onUserAvatarClick,
            onUriClick = { uri, reply -> clickUriHandler(uri, reply) },
            loadHtmlImage = loadHtmlImage,
            onHtmlImageClick = onHtmlImageClick,
        )
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = if (isLoggedIn) fabSizeWithMargin else 0.dp),
    ) {
        pagingRefreshItem(topicItems)
        var listItemIndex = 0
        if (topicInfo.topic != null) {
            if (!topicInfo.topic.isValid) {
                //TODO 非登录状态，触发某些关键字（如 fg ），重定向到首页，导致解析失败
                return@LazyColumn
            }

            item(key = "title", contentType = "title") {
                TopicTitle(
                    topicInfo = topicInfo.topic,
                    onUserAvatarClick = onUserAvatarClick,
                    onNodeClick = onNodeClick
                )
            }
            listItemIndex++

            if (topicInfo.topic.contentInfo.content.isNotEmpty()) {
                val tag = "content"
                item(key = tag, contentType = "content") {
                    val content = topicInfo.topic.contentInfo.content
                    TopicContent(
                        content = sizedHtmls[tag] ?: content,
                        openUri = openUri,
                        loadHtmlImage = { html, src -> loadHtmlImage(tag, html, src) },
                        onHtmlImageClick = onHtmlImageClick,
                    )
                }
                listItemIndex++
            }

            if (topicInfo.topic.contentInfo.supplements.isNotEmpty()) {
                itemsIndexed(items = topicInfo.topic.contentInfo.supplements,
                    key = { supplementIndex, item -> "supplement:$supplementIndex" },
                    contentType = { _, _ -> "supplement" }) { supplementIndex, item ->
                    val tag = "supplement:$supplementIndex"
                    TopicSupplement(
                        index = supplementIndex,
                        supplement = item,
                        content = sizedHtmls[tag] ?: item.content,
                        openUri = openUri,
                        loadHtmlImage = { html, src -> loadHtmlImage(tag, html, src) },
                        onHtmlImageClick = onHtmlImageClick,
                    )
                }
                listItemIndex += topicInfo.topic.contentInfo.supplements.size
            }

            if (topicInfo.topic.contentInfo.content.isNotEmpty() && topicInfo.topic.contentInfo.supplements.isEmpty()) {
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
                    replyNum = topicInfo.topic.headerInfo.commentNum,
                    repliesOrder = repliesOrder,
                    onRepliedOrderClick = {
                        onRepliedOrderClick(it)
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(repliesBarIndex)
                        }
                    },
                )
            }
            listItemIndex++
        }
        itemsIndexed(items = topicItems,
            key = { index, item -> if (item is Reply) item.replyId else "item#$index" }) { index, item ->
            if (item is Reply) {
                val replyWrapper = replyWrappers[item.replyId]
                if (replyWrapper?.ignored == true) {
                    return@itemsIndexed
                }
                val tag = "reply#${item.replyId}"
                TopicReply(
                    index = index,
                    reply = item,
                    replyWrapper = replyWrapper,
                    opName = topicInfo.topic?.headerInfo?.userName ?: "",
                    isLoggedIn = isLoggedIn,
                    content = sizedHtmls[tag] ?: item.replyContent,
                    highlightOpReply = highlightOpReply,
                    onUserAvatarClick = onUserAvatarClick,
                    onUriClick = { uri, reply -> clickUriHandler(uri, reply) },
                    onClick = {
                        onTopicReplyClick(it)
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(
                                listItemIndex + index,
                                -repliesBarHeight
                            )
                            delay(400)
                            lazyListState.animateScrollToItem(
                                listItemIndex + index,
                                -repliesBarHeight
                            )
                        }
                    },
                    onMenuItemClick = { onTopicMenuItemClick(it, item) },
                    loadHtmlImage = { html, src -> loadHtmlImage(tag, html, src) },
                    onHtmlImageClick = onHtmlImageClick,
                )
            }
        }
        pagingAppendMoreItem(lazyPagingItems = topicItems)
    }
}

@Composable
private fun TopicTitle(
    topicInfo: TopicInfo?,
    onUserAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit
) {
    if (topicInfo == null) return
    SimpleTopic(userName = topicInfo.headerInfo.userName,
        userAvatar = topicInfo.headerInfo.avatar,
        time = topicInfo.headerInfo.time,
        replyCount = topicInfo.headerInfo.commentNum,
        viewCount = topicInfo.headerInfo.viewCount,
        nodeName = topicInfo.headerInfo.tagName,
        nodeTitle = topicInfo.headerInfo.tag,
        title = topicInfo.headerInfo.title,
        onUserAvatarClick = {
            onUserAvatarClick(
                topicInfo.headerInfo.userName, topicInfo.headerInfo.avatar
            )
        },
        onNodeClick = {
            onNodeClick(topicInfo.headerInfo.tagName, topicInfo.headerInfo.tag)
        })
}

@Composable
private fun TopicContent(
    content: String,
    openUri: (String) -> Unit,
    loadHtmlImage: (String, String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
) {
    HtmlContent(
        content = content,
        selectable = false,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        onUriClick = openUri,
        loadImage = loadHtmlImage,
        onHtmlImageClick = onHtmlImageClick,
    )
}

@Composable
private fun TopicSupplement(
    index: Int,
    supplement: Supplement,
    content: String,
    openUri: (String) -> Unit,
    loadHtmlImage: (String, String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val leftBorderColor = MaterialTheme.colorScheme.tertiary

    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        ListDivider(modifier = Modifier.align(alignment = Alignment.BottomCenter))
        Column(modifier = Modifier
            .drawBehind {
                drawRect(color = backgroundColor)
                drawRect(color = leftBorderColor, size = size.copy(width = 4.dp.toPx()))
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(
                supplement.title,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                style = MaterialTheme.typography.labelMedium
            )
            HtmlContent(
                content = content,
                selectable = false,
                modifier = Modifier.fillMaxWidth(),
                onUriClick = openUri,
                loadImage = loadHtmlImage,
                onHtmlImageClick = onHtmlImageClick,
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
    replyNum: String, repliesOrder: RepliesOrder, onRepliedOrderClick: (RepliesOrder) -> Unit
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


enum class FabType {
    Reply, Send, Loading
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FabButton(
    visible: Boolean,
    type: FabType,
    onClick: (FabType) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    if (visible) {
        FloatingActionButton(
            shape = CircleShape,
            onClick = { onClick(type) },
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            val contentColor = LocalContentColor.current
            AnimatedContent(targetState = type) { state ->
                when (state) {
                    FabType.Send -> Icon(Icons.Rounded.Send, type.name, tint = contentColor)
                    FabType.Reply -> Icon(Icons.Rounded.Comment, type.name, tint = contentColor)
                    FabType.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), color = contentColor, strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun HandleReplyTopicState(
    replyTopicState: ReplyTopicState,
    topicItems: LazyPagingItems<Any>,
    onUriClick: (String) -> Unit,
) {
    if (replyTopicState is ReplyTopicState.Success) {
        LaunchedEffect(replyTopicState) {
            topicItems.refresh()
        }
    } else if (replyTopicState is ReplyTopicState.Failure) {
        val problem = rememberSaveable(replyTopicState) { replyTopicState.result.problem }
        HtmlAlertDialog(content = problem, onUriClick = onUriClick)
    }
}

