package io.github.v2compose.ui.topic

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.Constants
import io.github.v2compose.core.openInBrowser
import io.github.v2compose.core.share
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.ui.topic.composables.TopicMenuItem

@Composable
fun rememberTopicScreenState(context: Context = LocalContext.current): TopicScreenState {
    return remember(context) {
        TopicScreenState(context)
    }
}

@Stable
class TopicScreenState(private val context: Context) {
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
                    Constants.topicUrl(topicArgs.topicId)
                )
            }
            TopicMenuItem.OpenInBrowser -> {
                openInBrowser(Constants.topicUrl(topicArgs.topicId))
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
    }


}