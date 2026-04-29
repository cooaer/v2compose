package io.github.v2compose.ui.topic.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Reply
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.network.bean.TopicInfo.Reply
import io.github.v2compose.ui.common.HtmlContent
import io.github.v2compose.ui.common.ListDivider
import io.github.v2compose.ui.common.OnHtmlImageClick
import io.github.v2compose.ui.common.TopicUserAvatar
import io.github.v2compose.ui.topic.bean.ReplyWrapper
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import v2compose.shared.generated.resources.Res
import v2compose.shared.generated.resources.copy_comment
import v2compose.shared.generated.resources.ignore_comment
import v2compose.shared.generated.resources.menu_item_thank
import v2compose.shared.generated.resources.menu_item_unthank
import v2compose.shared.generated.resources.n_floor
import v2compose.shared.generated.resources.op
import v2compose.shared.generated.resources.reply_comment
import v2compose.shared.generated.resources.user_home_page

@Composable
fun TopicReply(
    index: Int,
    reply: TopicInfo.Reply,
    replyWrapper: ReplyWrapper?,
    opName: String,
    isLoggedIn: Boolean,
    content: String,
    highlightOpReply: Boolean,
    onUserAvatarClick: (String, String) -> Unit,
    onUriClick: (String, TopicInfo.Reply) -> Unit,
    onClick: (TopicInfo.Reply) -> Unit,
    onMenuItemClick: (ReplyMenuItem) -> Unit,
    loadHtmlImage: (String, String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
    shakeable: Boolean = false,
    onShakeFinished: (() -> Unit)? = null,
) {
    val isOp = opName == reply.userName

    val containerColor = if (highlightOpReply && isOp) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        Color.Transparent
    }

    ShakeAnimation(shake = shakeable, onShakeFinished = { onShakeFinished?.invoke() }) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(enabled = isLoggedIn) { onClick(reply) }
                .background(color = containerColor)
                .padding(start = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TopicUserAvatar(
                    userName = reply.userName,
                    userAvatar = reply.avatar,
                    modifier = Modifier.padding(top = 12.dp),
                    onUserAvatarClick = { onUserAvatarClick(reply.userName, reply.avatar) })
                ReplyFloor(
                    floor = reply.floor,
                    modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(top = 12.dp, end = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserName(userName = reply.userName)
                        Spacer(Modifier.width(4.dp))
                        if (isOp) {
                            OpLabel()
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = reply.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    HtmlContent(
                        content = content,
                        sourceContent = reply.replyContent,
                        selectable = false,
                        linkFloor = true,
                        onUriClick = { onUriClick(it, reply) },
                        onClick = { if (isLoggedIn) onClick(reply) },
                        loadImage = loadHtmlImage,
                        onHtmlImageClick = onHtmlImageClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                ListDivider(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.BottomCenter),
                )

                if (showActions) {
                    TopicReplyActions(
                        isLoggedIn = isLoggedIn,
                        reply = reply,
                        replyWrapper = replyWrapper,
                        onMenuItemClick = onMenuItemClick,
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                }
            }
        }
    }

}

@Composable
private fun TopicReplyActions(
    isLoggedIn: Boolean,
    reply: Reply,
    replyWrapper: ReplyWrapper?,
    onMenuItemClick: (ReplyMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val hadThanked = replyWrapper?.thanked ?: reply.hadThanked()
    val thanksCount = remember(reply, replyWrapper) {
        val thanked = replyWrapper?.thanked
        var thanksDiff = 0
        if (thanked != null && reply.hadThanked() != thanked) {
            thanksDiff = if (thanked) 1 else -1
        }
        reply.thanksCount() + thanksDiff
    }
    var bottomSheetVisible by remember { mutableStateOf(false) }

    val actionContentColor = MaterialTheme.colorScheme.secondary

    Box(modifier = modifier.padding(end = 4.dp)) {
        Row {
            if (isLoggedIn || thanksCount > 0) {
                val menuItem = remember(hadThanked) {
                    if (hadThanked) ReplyMenuItem.Thanked else ReplyMenuItem.Thank
                }
                IconButton(
                    enabled = isLoggedIn && !hadThanked,
                    onClick = { onMenuItemClick(menuItem) }) {
                    Icon(
                        imageVector = menuItem.icon,
                        contentDescription = menuItem.name,
                        tint = actionContentColor
                    )
                }
            }
            IconButton(
                onClick = { bottomSheetVisible = true }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "more",
                    tint = actionContentColor
                )
            }
        }
        if (thanksCount > 0) {
            Text(
                thanksCount.toString(),
                color = actionContentColor,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 36.dp)
            )
        }
    }

    if (bottomSheetVisible) {
        ReplyBottomSheet(
            isLoggedIn = isLoggedIn,
            onDismiss = { bottomSheetVisible = false },
            onItemClick = {
                bottomSheetVisible = false
                onMenuItemClick(it)
            })
    }
}

@Composable
fun OpLabel() {
    Text(
        stringResource(Res.string.op),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(2.dp)
            )
            .padding(horizontal = 2.dp),
    )
}


@Composable
fun UserTopicReply(
    index: Int,
    reply: TopicInfo.Reply,
    content: String,
    onUriClick: (String, TopicInfo.Reply) -> Unit,
    loadHtmlImage: (String, String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        if (index != 0) {
            ListDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
        }
        Spacer(Modifier.height(8.dp))
        HtmlContent(
            content = content,
            sourceContent = reply.replyContent,
            linkFloor = true,
            onUriClick = { onUriClick(it, reply) },
            loadImage = loadHtmlImage,
            onHtmlImageClick = onHtmlImageClick
        )
        Row {
            ReplyFloor(floor = reply.floor)
            Spacer(modifier = Modifier.width(8.dp))
            PublishedTime(time = reply.time)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun UserName(userName: String, modifier: Modifier = Modifier) {
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
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ReplyFloor(floor: Int, modifier: Modifier = Modifier) {
    Text(
        stringResource(Res.string.n_floor, floor),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

enum class ReplyMenuItem(val icon: ImageVector, val label: StringResource) {
    Thank(Icons.Rounded.FavoriteBorder, Res.string.menu_item_thank),
    Thanked(Icons.Rounded.Favorite, Res.string.menu_item_unthank),
    Reply(Icons.AutoMirrored.Rounded.Reply, Res.string.reply_comment),
    Copy(Icons.Rounded.ContentCopy, Res.string.copy_comment),
    Ignore(Icons.Rounded.VisibilityOff, Res.string.ignore_comment),
    HomePage(Icons.Rounded.Person, Res.string.user_home_page),
}

@Composable
fun ReplyBottomSheet(
    isLoggedIn: Boolean,
    onDismiss: () -> Unit,
    onItemClick: (ReplyMenuItem) -> Unit
) {
    val menuItems = remember {
        if (isLoggedIn) {
            listOf(
                ReplyMenuItem.Reply,
                ReplyMenuItem.Copy,
                ReplyMenuItem.Ignore,
                ReplyMenuItem.HomePage,
            )
        } else {
            listOf(
                ReplyMenuItem.Copy,
                ReplyMenuItem.HomePage,
            )
        }
    }

    val transitionState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() }
        ) {
            AnimatedVisibility(
                visibleState = transitionState,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    menuItems.forEach { item ->
                        ReplySheetItem(
                            item = item,
                            modifier = Modifier.clickable { onItemClick(item) })
                    }
                }
            }
        }
    }
}

@Composable
fun ReplySheetItem(item: ReplyMenuItem, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(item.label),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        ListDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun ShakeAnimation(
    shake: Boolean,
    onShakeFinished: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    if (shake) {
        LaunchedEffect(true) {
            (0 until 3).forEach { _ ->
                offsetX.animateTo(10f, tween(100, easing = LinearEasing))
                offsetX.animateTo(0f, tween(100, easing = LinearEasing))
                offsetX.animateTo(-10f, tween(100, easing = LinearEasing))
                offsetX.animateTo(0f, tween(100, easing = LinearEasing))
            }
            onShakeFinished()
        }
    }
    Box(modifier.offset { IntOffset(offsetX.value.roundToInt(), 0) }) {
        content()
    }
}
