package io.github.v2compose.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.v2compose.Constants
import org.jetbrains.compose.resources.stringResource
import v2compose.shared.generated.resources.Res
import v2compose.shared.generated.resources.n_comment
import v2compose.shared.generated.resources.n_views

@Composable
fun SimpleTopic(
    userName: String,
    userAvatar: String,
    time: String,
    replyCount: String,
    viewCount: Int? = null,
    nodeName: String,
    nodeTitle: String,
    title: String,
    titleOverview: Boolean = false,
    modifier: Modifier = Modifier,
    onItemClick: (() -> Unit)? = null,
    onUserAvatarClick: (() -> Unit)? = null,
    onNodeClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onItemClick != null) { onItemClick?.invoke() },
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TopicUserAvatar(
                    userName = userName,
                    userAvatar = userAvatar,
                    onUserAvatarClick = onUserAvatarClick,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.FirstLineTop,
                            ),
                        ),
                    )

                    Row {
                        Text(
                            time,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(Res.string.n_comment, replyCount.ifBlank { "0" }),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        viewCount?.let {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                stringResource(Res.string.n_views, viewCount.toString()),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                NodeTag(
                    nodeTitle = nodeTitle,
                    nodeName = nodeName,
                    onItemClick = { _, _ -> onNodeClick?.invoke() },
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (titleOverview) Constants.topicTitleOverviewMaxLines else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
            )
        }
        ListDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun TopicUserAvatar(
    userName: String,
    userAvatar: String,
    modifier: Modifier = Modifier,
    onUserAvatarClick: (() -> Unit)? = null,
) {
    AsyncImage(
        model = userAvatar,
        contentDescription = "$userName's avatar",
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            .clickable(enabled = onUserAvatarClick != null) { onUserAvatarClick?.invoke() },
        contentScale = ContentScale.Crop,
    )
}
