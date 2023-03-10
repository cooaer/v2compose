package io.github.v2compose.ui.topic

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.LocalSnackbarHostState
import io.github.v2compose.R
import io.github.v2compose.V2exUri
import io.github.v2compose.core.openInBrowser
import io.github.v2compose.core.share
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.ui.BaseScreenState
import io.github.v2compose.ui.topic.composables.TopicMenuItem
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberTopicScreenState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current,
): TopicScreenState {
    return remember(context, coroutineScope, snackbarHostState) {
        TopicScreenState(context, coroutineScope, snackbarHostState)
    }
}

@Stable
class TopicScreenState(
    context: Context,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) : BaseScreenState(context, coroutineScope, snackbarHostState) {
    fun share(title: String, url: String) {
        context.share(title, url)
    }

    fun openInBrowser(url: String) {
        context.openInBrowser(url)
    }

    fun onMenuClick(item: TopicMenuItem, topicArgs: TopicArgs, topicInfo: TopicInfo?) {
        if (topicInfo == null) return
        when (item) {
            TopicMenuItem.Share -> {
                share(
                    topicInfo.headerInfo.title,
                    V2exUri.topicUrl(topicArgs.topicId)
                )
            }
            TopicMenuItem.OpenInBrowser -> {
                openInBrowser(V2exUri.topicUrl(topicArgs.topicId))
            }
            else -> {}
        }
    }

    fun copy(reply: TopicInfo.Reply) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboardManager.setPrimaryClip(
            ClipData.newPlainText(
                ClipDescription.MIMETYPE_TEXT_HTML,
                reply.replyContent
            )
        )
        showMessage(R.string.copy_comment_success_tips)
    }


}