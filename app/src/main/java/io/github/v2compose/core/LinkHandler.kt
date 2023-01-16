package io.github.v2compose.core

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.NavHostController
import io.github.v2compose.Constants
import io.github.v2compose.core.extension.fullUrl
import io.github.v2compose.core.extension.tryParse

fun Context.openInBrowser(url: String) {
    val uri = url.tryParse() ?: return
    val defaultBrowser = getDefaultBrowser()
    val customTabsBrowsers = getCustomTabsBrowsers()
    if (customTabsBrowsers.contains(defaultBrowser)) {
        val customTabs = CustomTabsIntent.Builder().build()
        customTabs.intent.setPackage(defaultBrowser)
        customTabs.launchUrl(this, uri)
    } else if (customTabsBrowsers.isNotEmpty()) {
        val customTabs = CustomTabsIntent.Builder().build()
        customTabs.intent.setPackage(customTabsBrowsers[0])
        customTabs.launchUrl(this, uri)
    } else {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}