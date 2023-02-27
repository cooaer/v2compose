package io.github.v2compose.ui.topic

import android.util.Size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.cooaer.htmltext.Img
import io.github.cooaer.htmltext.LocalHtmlImageLoadedCallback
import io.github.v2compose.R
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.network.bean.TopicInfo.ContentInfo.Supplement
import io.github.v2compose.network.bean.TopicInfo.Reply
import io.github.v2compose.ui.HandleSnackbarMessage
import io.github.v2compose.ui.common.*
import io.github.v2compose.ui.topic.composables.*
import kotlinx.coroutines.launch

private const val TAG = "TopicScreen"

@Composable
fun TopicScreenRoute(
    onBackClick: () -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onAddSupplementClick:(String) -> Unit,
    openUri: (String) -> Unit,
    viewModel: TopicViewModel = hiltViewModel(),
    screenState: TopicScreenState = rememberTopicScreenState(),
) {
    val args = viewModel.topicArgs
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val repliesReversed by viewModel.repliesReversed.collectAsStateWithLifecycle(initialValue = true)
    val topicItems = viewModel.topicItems.collectAsLazyPagingItems()

    val topicInfo = if (topicItems.itemCount > 0) {
        topicItems.peek(0)?.let {
            if (it is TopicInfo) it.also { viewModel.updateTopicInfoWrapper(topic = it) } else null
        }
    } else null

    val topicInfoWrapper by viewModel.topicInfoWrapper
    val replyWrappers = viewModel.replyWrappers
    val replyTopicState by viewModel.replyTopicState.collectAsStateWithLifecycle()

    HandleReplyTopicState(replyTopicState, topicItems, openUri)

    HandleSnackbarMessage(viewModel, screenState)

    TopicScreen(
        isLoggedIn = isLoggedIn,
        topicInfo = topicInfoWrapper,
        repliesOrder = if (repliesReversed) RepliesOrder.Negative else RepliesOrder.Positive,
        topicItems = topicItems,
        htmlImageSizes = viewModel.htmlImageSizes,
        snackbarHostState = screenState.snackbarHostState,
        replyWrappers = replyWrappers,
        replyTopicState = replyTopicState,
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
        onHtmlImageLoaded = { img -> img.size()?.let { viewModel.saveHtmlImageSize(img.src, it) } },
        onSendComment = viewModel::replyTopic,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicScreen(
    isLoggedIn: Boolean,
    topicInfo: TopicInfoWrapper,
    repliesOrder: RepliesOrder,
    topicItems: LazyPagingItems<Any>,
    htmlImageSizes: Map<String, Size>,
    replyWrappers: Map<String, ReplyWrapper>,
    snackbarHostState: SnackbarHostState,
    replyTopicState: ReplyTopicState,
    onBackClick: () -> Unit,
    onTopicMenuClick: (TopicMenuItem) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onRepliedOrderClick: (RepliesOrder) -> Unit,
    openUri: (String) -> Unit,
    onReplyMenuItemClick: (ReplyMenuItem, Reply) -> Unit,
    onHtmlImageLoaded: (Img) -> Unit,
    onSendComment: (String) -> Unit,
) {
    val density = LocalDensity.current

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets.ime.union(WindowInsets.systemBars),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CompositionLocalProvider(LocalHtmlImageLoadedCallback provides onHtmlImageLoaded) {
                TopicList(
                    topicInfo = topicInfo,
                    repliesOrder = repliesOrder,
                    topicItems = topicItems,
                    lazyListState = scrollState,
                    htmlImageSizes = htmlImageSizes,
                    replyWrappers = replyWrappers,
                    isLoggedIn = isLoggedIn,
                    onUserAvatarClick = onUserAvatarClick,
                    onNodeClick = onNodeClick,
                    onRepliedOrderClick = onRepliedOrderClick,
                    onTopicReplyClick = {
                        replyInputInitialText = initialReplyText(it)
                        replyInputState = ReplyInputState.Expanded
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
                    modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                )
            }

            ReplyInput(
                initialValue = replyInputInitialText,
                onValueChanged = { replyInputCurrentText = it },
                state = replyInputState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
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
    htmlImageSizes: Map<String, Size>,
    replyWrappers: Map<String, ReplyWrapper>,
    isLoggedIn: Boolean,
    onUserAvatarClick: (String, String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onRepliedOrderClick: (RepliesOrder) -> Unit,
    onTopicReplyClick: (Reply) -> Unit,
    openUri: (String) -> Unit,
    onTopicMenuItemClick: (ReplyMenuItem, Reply) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickedUserReplies = rememberMutableStateListOf<List<Reply>>()
    var repliesBarIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val clickUriHandler: Function2<String, Reply, Unit> = { uri, reply ->
        val userName = uri.removePrefix("/member/")
        val userReplies =
            topicItems.itemSnapshotList.filter { it is Reply && it.floor < reply.floor && it.userName == userName } as List<Reply>
        if (userReplies.isEmpty()) {
            openUri(uri)
        } else {
            clickedUserReplies.add(userReplies)
        }
    }

    clickedUserReplies.forEachIndexed { index, item ->
        UserRepliesDialog(
            userReplies = item,
            onDismissRequest = { clickedUserReplies.removeAt(index) },
            onUserAvatarClick = onUserAvatarClick,
            onUriClick = clickUriHandler,
        )
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = fabSizeWithMargin),
    ) {
        pagingRefreshItem(topicItems)
        if (topicInfo.topic != null) {
            if (!topicInfo.topic.isValid) {
                //TODO 非登录状态，触发某些关键字（如 fg ），重定向到首页，导致解析失败
                return@LazyColumn
            }
            var listItemIndex = 0

            item(key = "title", contentType = "title") {
                TopicTitle(
                    topicInfo = topicInfo.topic,
                    onUserAvatarClick = onUserAvatarClick,
                    onNodeClick = onNodeClick
                )
            }
            listItemIndex++

            if (topicInfo.topic.contentInfo.content.isNotEmpty()) {
                item(key = "content", contentType = "content") {
                    TopicContent(
                        content = topicInfo.topic.contentInfo.content,
                        htmlImageSizes = htmlImageSizes,
                        openUri = openUri,
                    )
                }
                listItemIndex++
            }

            if (topicInfo.topic.contentInfo.supplements.isNotEmpty()) {
                itemsIndexed(items = topicInfo.topic.contentInfo.supplements,
                    key = { supplementIndex, item -> "supplement:$supplementIndex" },
                    contentType = { _, _ -> "supplement" }) { supplementIndex, item ->
                    TopicSupplement(
                        index = supplementIndex,
                        supplement = item,
                        htmlImageSizes = htmlImageSizes,
                        openUri = openUri,
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
        }
        itemsIndexed(items = topicItems,
            key = { index, item -> if (item is Reply) item.replyId else "item#$index#${topicItems.itemCount}" }) { index, item ->
            if (item is Reply) {
                val replyWrapper = replyWrappers[item.replyId]
                if (replyWrapper?.ignored == true) {
                    return@itemsIndexed
                }
                TopicReply(index = index,
                    reply = item,
                    replyWrapper = replyWrapper,
                    opName = topicInfo.topic?.headerInfo?.userName ?: "",
                    htmlImageSizes = htmlImageSizes,
                    isLoggedIn = isLoggedIn,
                    onUserAvatarClick = onUserAvatarClick,
                    onUriClick = clickUriHandler,
                    onClick = onTopicReplyClick,
                    onMenuItemClick = { onTopicMenuItemClick(it, item) })
            }
        }
        pagingAppendMoreItem(lazyPagingItems = topicItems)
    }
}

@Composable
private fun UserRepliesDialog(
    userReplies: List<Reply>,
    onDismissRequest: () -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onUriClick: (String, Reply) -> Unit,
) {
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
                            start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    with(userReplies.first()) {
                        TopicUserAvatar(userName = userName,
                            userAvatar = avatar,
                            onUserAvatarClick = { onUserAvatarClick(userName, avatar) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(id = R.string.user_previous_replies, userName),
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(
                        items = userReplies,
                        key = { _, item -> item.replyId }) { index, item ->
                        UserTopicReply(index, reply = item, onUriClick = onUriClick)
                    }
                }
            }
        }
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
        nodeId = topicInfo.headerInfo.tagId,
        nodeName = topicInfo.headerInfo.tag,
        title = topicInfo.headerInfo.title,
        onUserAvatarClick = {
            onUserAvatarClick(
                topicInfo.headerInfo.userName, topicInfo.headerInfo.avatar
            )
        },
        onNodeClick = {
            onNodeClick(topicInfo.headerInfo.tagId, topicInfo.headerInfo.tag)
        })
}

@Composable
private fun TopicContent(
    content: String,
    htmlImageSizes: Map<String, Size>,
    openUri: (String) -> Unit,
) {
    HtmlContent(
        content = content,
        htmlImageSizes = htmlImageSizes,
        selectable = false,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        onUriClick = openUri,
    )
}

@Composable
private fun TopicSupplement(
    index: Int,
    supplement: Supplement,
    htmlImageSizes: Map<String, Size>,
    openUri: (String) -> Unit,
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
                content = supplement.content,
                htmlImageSizes = htmlImageSizes,
                selectable = false,
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

