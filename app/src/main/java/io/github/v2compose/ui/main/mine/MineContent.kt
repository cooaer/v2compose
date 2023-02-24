package io.github.v2compose.ui.main.mine

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.cooaer.htmltext.HtmlText
import io.github.v2compose.R
import io.github.v2compose.bean.Account
import io.github.v2compose.core.extension.isBeforeTodayByUTC
import io.github.v2compose.ui.HandleSnackbarMessage
import io.github.v2compose.ui.common.ListDivider


@Composable
fun MineContent(
    onLoginClick: () -> Unit,
    onMyHomePageClick: () -> Unit,
    onCreateTopicClick: () -> Unit,
    onMyNodesClick: () -> Unit,
    onMyTopicsClick: () -> Unit,
    onMyFollowingClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: MineViewModel = hiltViewModel(),
    mineContentState: MineContentState = rememberMineContentState(),
) {
    val account by viewModel.account.collectAsStateWithLifecycle()
    val lastCheckInTime by viewModel.lastCheckInTime.collectAsStateWithLifecycle()
    val hasCheckingInTips by viewModel.hasCheckingInTips.collectAsStateWithLifecycle()

    HandleSnackbarMessage(viewModel, mineContentState)

    MineContainer(
        account = account,
        lastCheckInTime = lastCheckInTime,
        hasCheckingInTips = hasCheckingInTips,
        onLoginClick = onLoginClick,
        onMyHomePageClick = onMyHomePageClick,
        onCheckInClick = viewModel::doCheckIn,
        onCreateTopicClick = {
            if (account.isValid()) {
                onCreateTopicClick()
            } else {
                mineContentState.showMessage(R.string.login_first)
            }
        },
        onMyNodesClick = mineContentState::notImplemented,
        onMyTopicsClick = mineContentState::notImplemented,
        onMyFollowingClick = mineContentState::notImplemented,
        onSettingsClick = onSettingsClick
    )
}

@Composable
private fun MineContainer(
    account: Account,
    lastCheckInTime: Long,
    hasCheckingInTips: Boolean,
    onLoginClick: () -> Unit,
    onMyHomePageClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onCreateTopicClick: () -> Unit,
    onMyNodesClick: () -> Unit,
    onMyTopicsClick: () -> Unit,
    onMyFollowingClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = ContentAlpha.disabled))
    ) {
        Column {
            MineHeader(
                account = account,
                lastCheckInTime = lastCheckInTime,
                hasCheckingInTips = hasCheckingInTips,
                onLoginClick = onLoginClick,
                onMyHomePageClick = onMyHomePageClick,
                onCheckInClick = onCheckInClick,
            )
            Spacer(Modifier.height(8.dp))
            MineEntry(
                leadingIcon = Icons.Rounded.Edit,
                title = stringResource(id = R.string.create_topic),
                onEntryClick = onCreateTopicClick,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            )
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                MineEntry(
                    leadingIcon = Icons.Rounded.Category,
                    title = stringResource(id = R.string.my_nodes),
                    subtitle = account.nodes.toString(),
                    onEntryClick = onMyNodesClick,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                )
                ListDivider()
                MineEntry(
                    leadingIcon = Icons.Rounded.Topic,
                    title = stringResource(id = R.string.my_topics),
                    subtitle = account.topics.toString(),
                    onEntryClick = onMyTopicsClick,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                )
                ListDivider()
                MineEntry(
                    leadingIcon = Icons.Rounded.People,
                    title = stringResource(id = R.string.my_following),
                    subtitle = account.following.toString(),
                    onEntryClick = onMyFollowingClick,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                )
            }
            Spacer(Modifier.height(8.dp))
            MineEntry(
                leadingIcon = Icons.Rounded.Settings,
                title = stringResource(id = R.string.settings),
                onEntryClick = onSettingsClick,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MineHeader(
    account: Account,
    lastCheckInTime: Long,
    hasCheckingInTips: Boolean,
    onLoginClick: () -> Unit,
    onMyHomePageClick: () -> Unit,
    onCheckInClick: () -> Unit,
) {
    val contentColor = LocalContentColor.current
    Row(
        modifier = Modifier
            .clickable { if (account.isValid()) onMyHomePageClick() else onLoginClick() }
            .background(color = MaterialTheme.colorScheme.background)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = account.userAvatar,
            contentDescription = "user avatar",
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape
                ),
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                if (account.isValid()) account.userName else stringResource(id = R.string.login),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .wrapContentHeight()
            )
            if (account.isValid()) {
                val canCheckIn = hasCheckingInTips || lastCheckInTime.isBeforeTodayByUTC()
                AssistChip(
                    onClick = onCheckInClick,
                    enabled = canCheckIn,
                    label = {
                        Text(
                            stringResource(if (canCheckIn) R.string.daily_mission else R.string.daily_mission_ok),
                            color = contentColor.copy(alpha = ContentAlpha.medium),
                        )
                    },
                    leadingIcon = {
                        if (!canCheckIn){
                            Icon(
                                Icons.Rounded.Check,
                                "daily mission",
                                tint = contentColor.copy(alpha = ContentAlpha.medium),
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
        Icon(
            Icons.Rounded.NavigateNext,
            "goto user center",
            tint = contentColor.copy(alpha = ContentAlpha.medium),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun MineEntry(
    leadingIcon: ImageVector,
    title: String,
    onEntryClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    val contentColor = LocalContentColor.current
    Row(
        modifier = modifier
            .height(56.dp)
            .clickable { onEntryClick() }
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            leadingIcon,
            "icon",
            modifier = Modifier.size(24.dp),
            tint = contentColor.copy(alpha = ContentAlpha.medium),
        )
        Spacer(Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        if (!subtitle.isNullOrEmpty()) {
            Spacer(Modifier.width(8.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = ContentAlpha.medium),
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.Rounded.NavigateNext,
            "enter",
            modifier = Modifier.size(32.dp),
            tint = contentColor.copy(alpha = ContentAlpha.medium),
        )
    }
}

@Composable
private fun TestHtmlText() {
    HtmlText(
        html = TEST_HTML_TEXT + TEST_HTML_TEXT_2,
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    )
}