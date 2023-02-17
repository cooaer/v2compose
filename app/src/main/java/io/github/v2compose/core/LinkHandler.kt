package io.github.v2compose.core

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import io.github.v2compose.core.extension.tryParse

fun Context.openInBrowser(url: String, inExternalBrowser: Boolean = true) {
    val uri = url.tryParse() ?: return
    if (inExternalBrowser) {
        val defaultBrowser = getDefaultBrowser()
        val customTabsBrowsers = getCustomTabsBrowsers()
        if (customTabsBrowsers.contains(defaultBrowser)) {
            val customTabs = CustomTabsIntent.Builder().build()
            customTabs.intent.setPackage(defaultBrowser)
            customTabs.launchUrl(this, uri)
            return
        } else if (customTabsBrowsers.isNotEmpty()) {
            val customTabs = CustomTabsIntent.Builder().build()
            customTabs.intent.setPackage(customTabsBrowsers[0])
            customTabs.launchUrl(this, uri)
            return
        }
    }
    startActivity(Intent(Intent.ACTION_VIEW, uri))
}