package io.github.v2compose.core.extension

import okhttp3.internal.http.StatusLine
import java.net.HttpURLConnection


val Int.isRedirect: Boolean
    get() = when (this) {
        StatusLine.HTTP_PERM_REDIRECT,
        StatusLine.HTTP_TEMP_REDIRECT,
        HttpURLConnection.HTTP_MULT_CHOICE,
        HttpURLConnection.HTTP_MOVED_PERM,
        HttpURLConnection.HTTP_MOVED_TEMP,
        HttpURLConnection.HTTP_SEE_OTHER -> true
        else -> false
    }