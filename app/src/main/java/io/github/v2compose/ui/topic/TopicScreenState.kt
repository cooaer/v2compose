package io.github.v2compose.ui.topic

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import io.github.v2compose.Constants
import io.github.v2compose.core.extension.fullUrl
import io.github.v2compose.core.extension.tryParse
import io.github.v2compose.core.getCustomTabsBrowsers
import io.github.v2compose.core.getDefaultBrowser

@Composable
fun rememberTopicScreenState(context: Context = LocalContext.current): TopicScreenState {
    return remember(context) {
        TopicScreenState(context)
    }
}

@Stable
class TopicScreenState(private val context: Context) : UriHandler {
    fun share(title: String, url: String) {
        val text = "$title\n$url"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareChooser = Intent.createChooser(sendIntent, null)
        context.startActivity(shareChooser)
    }

    fun openInBrowser(url: String) {
        val uri = url.tryParse() ?: return
        val defaultBrowser = context.getDefaultBrowser()
        val customTabsBrowsers = context.getCustomTabsBrowsers()
        if (customTabsBrowsers.contains(defaultBrowser)) {
            val customTabs = CustomTabsIntent.Builder().build()
            customTabs.intent.setPackage(defaultBrowser)
            customTabs.launchUrl(context, uri)
        } else if (customTabsBrowsers.isNotEmpty()) {
            val customTabs = CustomTabsIntent.Builder().build()
            customTabs.intent.setPackage(customTabsBrowsers[0])
            customTabs.launchUrl(context, uri)
        } else {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    override fun openUri(uri: String) {
        openInBrowser(uri.fullUrl(Constants.baseUrl))
    }
}