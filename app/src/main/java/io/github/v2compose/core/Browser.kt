package io.github.v2compose.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION


fun Context.getDefaultBrowser(): String? {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.v2ex.com"))
    val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        ?: return null
    val activityInfo = resolveInfo.activityInfo ?: return null
    return activityInfo.packageName
}

fun Context.getCustomTabsBrowsers(): List<String> {
    // Get default VIEW intent handler.
    val activityIntent = Intent()
        .setAction(Intent.ACTION_VIEW)
        .addCategory(Intent.CATEGORY_BROWSABLE)
        .setData(Uri.fromParts("http", "", null))

    // Get all apps that can handle VIEW intents.
    val resolvedActivityList = packageManager.queryIntentActivities(activityIntent, 0)
    val packagesSupportingCustomTabs = ArrayList<ResolveInfo>()
    for (info in resolvedActivityList) {
        val serviceIntent = Intent()
        serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
        serviceIntent.setPackage(info.activityInfo.packageName)
        // Check if this package also resolves the Custom Tabs service.
        if (packageManager.resolveService(serviceIntent, 0) != null) {
            packagesSupportingCustomTabs.add(info)
        }
    }
    return packagesSupportingCustomTabs.map { it.activityInfo.packageName }
}