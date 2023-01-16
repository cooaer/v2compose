package io.github.v2compose.ui.topic

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.Constants
import io.github.v2compose.core.openInBrowser
import io.github.v2compose.core.share
import io.github.v2compose.network.bean.TopicInfo

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

    fun onMenuClick(item: MenuItem, topicArgs: TopicArgs, topicInfo: TopicInfo?) {
        if (topicInfo == null) return
        when (item) {
            MenuItem.Share -> {
                share(
                    topicInfo.headerInfo.title,
                    Constants.topicUrl(topicArgs.topicId)
                )
            }
            MenuItem.OpenInBrowser -> {
                openInBrowser(Constants.topicUrl(topicArgs.topicId))
            }
            else -> {}
        }
    }
}