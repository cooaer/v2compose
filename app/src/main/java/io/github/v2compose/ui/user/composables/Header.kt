package io.github.v2compose.ui.user.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.v2compose.R
import io.github.v2compose.core.extension.castOrNull
import io.github.v2compose.network.bean.UserPageInfo
import io.github.v2compose.ui.common.BackIcon
import io.github.v2compose.ui.common.TextAlertDialog
import io.github.v2compose.ui.common.TopicUserAvatar
import io.github.v2compose.ui.user.UserUiState
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingToolbarScope.UserToolbar(
    userUiState: UserUiState,
    isLoggedIn: Boolean,
    scaffoldState: CollapsingToolbarScaffoldState,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onFollowClick: () -> Unit,
    onBlockClick: () -> Unit,
) {
    val userPageInfo = userUiState.castOrNull<UserUiState.Success>()?.userPageInfo

    TopAppBar(navigationIcon = { BackIcon(onBackClick = onBackClick) }, title = {
        userPageInfo?.let {
            UserTopAppBarTitle(
                userPageInfo = it,
                modifier = Modifier.graphicsLayer(alpha = 1 - scaffoldState.toolbarState.progress),
            )
        }
    }, actions = {
        if (isLoggedIn) {
            userPageInfo?.let {
                if (userPageInfo.followUrl != null) {
                    FollowIcon(
                        userPageInfo.hadFollowed(),
                        onFollowClick = onFollowClick,
                        modifier = Modifier.graphicsLayer(alpha = 1 - scaffoldState.toolbarState.progress),
                    )
                }
            }
        }
        IconButton(onClick = onShareClick) {
            Icon(Icons.Rounded.Share, contentDescription = "share")
        }
    })

    UserHeader(
        userPageInfo = userPageInfo,
        isLoggedIn = isLoggedIn,
        onFollowClick = onFollowClick,
        onBlockClick = onBlockClick,
        modifier = Modifier
            .parallax(0.5f)
            .graphicsLayer(alpha = scaffoldState.toolbarState.progress)
    )
}

@Composable
private fun FollowIcon(
    followed: Boolean,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentFollowed by remember(followed) { mutableStateOf(followed) }
    val followColor =
        if (currentFollowed) LocalContentColor.current.copy(alpha = ContentAlpha.medium) else MaterialTheme.colorScheme.primary
    var showUnfollowDialog by remember { mutableStateOf(false) }
    val onFollowClickInternal = {
        if (currentFollowed == followed) {
            if (currentFollowed) {
                showUnfollowDialog = true
            } else {
                currentFollowed = true
                onFollowClick()
            }
        }
    }

    if (showUnfollowDialog) {
        TextAlertDialog(
            message = stringResource(R.string.user_unfollow_tips),
            onConfirm = {
                currentFollowed = false
                onFollowClick()
            },
            onDismiss = { showUnfollowDialog = false },
        )
    }

    IconButton(
        onClick = { onFollowClickInternal() },
        modifier = modifier,
    ) {
        Icon(
            if (currentFollowed) Icons.Rounded.RemoveCircleOutline else Icons.Rounded.AddCircleOutline,
            "follow", Modifier.size(20.dp), followColor,
        )
    }
}

@Composable
private fun UserTopAppBarTitle(userPageInfo: UserPageInfo, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        TopicUserAvatar(userName = userPageInfo.userName, userAvatar = userPageInfo.avatar)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                userPageInfo.userName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(4.dp))
            val online = userPageInfo.isOnline
            val colorScheme = MaterialTheme.colorScheme
            Text(
                text = stringResource(id = if (online) R.string.user_online else R.string.user_offline),
                style = MaterialTheme.typography.labelSmall,
                color = if (online) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = if (online) colorScheme.primaryContainer else colorScheme.surfaceVariant)
                    .padding(horizontal = 6.dp)
            )
        }
    }
}

@Composable
private fun UserHeader(
    userPageInfo: UserPageInfo?,
    isLoggedIn: Boolean,
    onFollowClick: () -> Unit,
    onBlockClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val topBarHeight = 56.dp
    Box(modifier = modifier) {
        UserInfo(
            userPageInfo = userPageInfo, colorScheme = colorScheme,
            modifier = Modifier.padding(
                start = 16.dp, top = topBarHeight + 4.dp, end = 16.dp, bottom = 4.dp
            ),
        )

        if (isLoggedIn) {
            userPageInfo?.let {
                UserActions(
                    userPageInfo = it,
                    onFollowClick = onFollowClick,
                    onBlockClick = onBlockClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = topBarHeight, end = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun UserInfo(
    userPageInfo: UserPageInfo?,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AsyncImage(
            model = userPageInfo?.avatar ?: "",
            contentDescription = "${userPageInfo?.userName}'s avatar",
            modifier = Modifier.size(48.dp).clip(CircleShape).also { mod ->
                userPageInfo?.let {
                    mod.background(color = colorScheme.onBackground.copy(alpha = 0.1f))
                }
            },
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(userPageInfo?.userName ?: "", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(4.dp))
            userPageInfo?.let {
                val online = userPageInfo.isOnline
                Text(
                    text = stringResource(id = if (online) R.string.user_online else R.string.user_offline),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (online) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = if (online) colorScheme.primaryContainer else colorScheme.surfaceVariant)
                        .padding(horizontal = 6.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            userPageInfo?.desc ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium),
            modifier = Modifier.heightIn(min = 40.dp)
        )
    }
}

@Composable
private fun UserActions(
    userPageInfo: UserPageInfo,
    onFollowClick: () -> Unit,
    onBlockClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        userPageInfo.followUrl?.let {
            FollowButton(userPageInfo, onFollowClick)
        }
        userPageInfo.blockUrl?.let {
            BlockButton(userPageInfo, onBlockClick)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BlockButton(
    userPageInfo: UserPageInfo,
    onBlockClick: () -> Unit
) {
    var blocked by remember(userPageInfo) { mutableStateOf(userPageInfo.hadBlocked()) }
    val blockColor =
        LocalContentColor.current.copy(alpha = if (blocked) ContentAlpha.high else ContentAlpha.medium)
    var showBlockDialog by remember { mutableStateOf(false) }
    val onBlockClickInternal = {

        if (blocked == userPageInfo.hadBlocked()) {
            if (blocked) {
                blocked = false
                onBlockClick()
            } else {
                showBlockDialog = true
            }
        }
    }
    if (showBlockDialog) {
        TextAlertDialog(
            message = stringResource(R.string.user_block_tips, userPageInfo.userName),
            onConfirm = {
                blocked = true
                onBlockClick()
            },
            onDismiss = { showBlockDialog = false },
        )
    }
    AssistChip(
        onClick = { onBlockClickInternal() },
        leadingIcon = {
            Icon(
                if (blocked) Icons.Rounded.RemoveCircleOutline else Icons.Rounded.Block,
                "block", Modifier.size(20.dp), blockColor,
            )
        },
        label = {
            Text(
                stringResource(if (blocked) R.string.user_unblock else R.string.user_block),
                color = blockColor
            )
        },
        shape = RoundedCornerShape(16.dp),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FollowButton(
    userPageInfo: UserPageInfo,
    onFollowClick: () -> Unit
) {
    var followed by remember(userPageInfo) { mutableStateOf(userPageInfo.hadFollowed()) }
    val followColor =
        if (followed) LocalContentColor.current.copy(alpha = ContentAlpha.medium) else MaterialTheme.colorScheme.primary
    var showUnfollowDialog by remember { mutableStateOf(false) }
    val onFollowClickInternal = {
        if (followed == userPageInfo.hadFollowed()) {
            if (followed) {
                showUnfollowDialog = true
            } else {
                followed = true
                onFollowClick()
            }
        }
    }
    if (showUnfollowDialog) {
        TextAlertDialog(
            message = stringResource(R.string.user_unfollow_tips),
            onConfirm = {
                followed = false
                onFollowClick()
            },
            onDismiss = { showUnfollowDialog = false },
        )
    }
    AssistChip(
        onClick = { onFollowClickInternal() },
        leadingIcon = {
            Icon(
                if (followed) Icons.Rounded.RemoveCircleOutline else Icons.Rounded.AddCircleOutline,
                "follow", Modifier.size(20.dp), followColor,
            )
        },
        label = {
            Text(
                stringResource(if (followed) R.string.user_unfollow else R.string.user_follow),
                color = followColor,
            )
        },
        shape = RoundedCornerShape(16.dp),
    )
}


