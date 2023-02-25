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
import io.github.v2compose.ui.common.TopicUserAvatar
import io.github.v2compose.ui.user.UserUiState
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingToolbarScope.UserToolbar(
    userUiState: UserUiState,
    scaffoldState: CollapsingToolbarScaffoldState,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onFollowClick: (Boolean) -> Unit,
    onBlockClick: (Boolean) -> Unit,
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
        userPageInfo?.let {
            val followed = userPageInfo.hadFollowed()
            IconButton(
                onClick = { onFollowClick(!followed) },
                modifier = Modifier.graphicsLayer(alpha = 1 - scaffoldState.toolbarState.progress),
            ) {
                Icon(if (followed) Icons.Rounded.Done else Icons.Rounded.Add, "follow")
            }
        }
        IconButton(onClick = onShareClick) {
            Icon(Icons.Rounded.Share, contentDescription = "share")
        }
    })

    UserHeader(
        userPageInfo = userPageInfo,
        onFollowClick = onFollowClick,
        onBlockClick = onBlockClick,
        modifier = Modifier
            .parallax(0.5f)
            .graphicsLayer(alpha = scaffoldState.toolbarState.progress)
    )
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
            if (userPageInfo.isOnline) {
                Text(
                    text = stringResource(id = R.string.user_online),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.user_offline),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun UserHeader(
    userPageInfo: UserPageInfo?,
    onFollowClick: (Boolean) -> Unit,
    onBlockClick: (Boolean) -> Unit,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserActions(
    userPageInfo: UserPageInfo,
    onFollowClick: (Boolean) -> Unit,
    onBlockClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val followed = userPageInfo.hadFollowed()
    val blocked = userPageInfo.hadBlocked()
    val contentColor = LocalContentColor.current

    var showUnfollowDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }

    if (showUnfollowDialog) {
        TextAlertDialog(
            message = stringResource(R.string.user_unfollow_tips),
            onConfirm = { onFollowClick(false) },
            onDismiss = { showUnfollowDialog = false },
        )
    }

    if (showBlockDialog) {
        TextAlertDialog(
            message = stringResource(R.string.user_block_tips, userPageInfo.userName),
            onConfirm = { onBlockClick(true) },
            onDismiss = { showBlockDialog = false },
        )
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        AssistChip(
            onClick = {
                if (followed) showUnfollowDialog = true else onFollowClick(true)
            },
            leadingIcon = {
                Icon(if (followed) Icons.Rounded.Done else Icons.Rounded.Add, "follow")
            },
            label = {
                Text(
                    stringResource(if (followed) R.string.user_unfollow else R.string.user_follow),
                    color = contentColor.copy(alpha = if (followed) ContentAlpha.medium else ContentAlpha.high)
                )
            },
            shape = RoundedCornerShape(16.dp),
        )

        AssistChip(
            onClick = {
                if (blocked) onBlockClick(false) else showBlockDialog = true
            },
            leadingIcon = {
                Icon(
                    if (blocked) Icons.Rounded.Remove else Icons.Rounded.Block,
                    "block",
                    Modifier.size(18.dp)
                )
            },
            label = {
                Text(
                    stringResource(if (blocked) R.string.user_unblock else R.string.user_block),
                    color = contentColor.copy(alpha = if (blocked) ContentAlpha.medium else ContentAlpha.high)
                )
            },
            shape = RoundedCornerShape(16.dp),
        )

    }
}

@Composable
private fun TextAlertDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(id = R.string.cancel))
            }
        },
    )
}


