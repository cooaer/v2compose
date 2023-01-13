package io.github.v2compose.core.extension

import android.net.Uri

fun String.tryParse(): Uri? {
    return try {
        Uri.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.fullUrl(baseUrl: String? = null): String {
    if (startsWith("//")) {
        return "https:$this"
    } else if (startsWith("/")) {
        if (baseUrl != null) {
            return baseUrl.dropLastWhile { it == '/' } + this
        }
    }
    return this
}