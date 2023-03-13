package io.github.v2compose.ui.topic.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.v2compose.R
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.ui.common.OnHtmlImageClick
import io.github.v2compose.ui.common.TopicUserAvatar

@Composable
fun UserRepliesDialog(
    userReplies: List<TopicInfo.Reply>,
    sizedHtmls: SnapshotStateMap<String, String>,
    onDismissRequest: () -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onUriClick: (String, TopicInfo.Reply) -> Unit,
    loadHtmlImage: (String, String, String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
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
                UserRepliesTitle(userReplies, onUserAvatarClick)

                UserReplyList(userReplies, sizedHtmls, onUriClick, loadHtmlImage, onHtmlImageClick)
            }
        }
    }
}

@Composable
private fun UserReplyList(
    userReplies: List<TopicInfo.Reply>,
    sizedHtmls: SnapshotStateMap<String, String>,
    onUriClick: (String, TopicInfo.Reply) -> Unit,
    loadHtmlImage: (String, String, String?) -> Unit,
    onHtmlImageClick: OnHtmlImageClick
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 8.dp), modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(items = userReplies, key = { _, item -> item.replyId }) { index, item ->
            val tag = item.replyId
            UserTopicReply(
                index,
                reply = item,
                content = sizedHtmls[tag] ?: item.replyContent,
                onUriClick = onUriClick,
                loadHtmlImage = { html, src -> loadHtmlImage(tag, html, src) },
                onHtmlImageClick = onHtmlImageClick,
            )
        }
    }
}

@Composable
private fun UserRepliesTitle(
    userReplies: List<TopicInfo.Reply>, onUserAvatarClick: (String, String) -> Unit
) {
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
}
