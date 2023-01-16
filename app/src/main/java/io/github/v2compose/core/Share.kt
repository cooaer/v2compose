package io.github.v2compose.core

import android.content.Context
import android.content.Intent

fun Context.share(title: String, url: String) {
    val text = "$title\n$url"
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareChooser = Intent.createChooser(sendIntent, null)
    startActivity(shareChooser)
}

