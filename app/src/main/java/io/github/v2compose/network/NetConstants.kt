package io.github.v2compose.network

import android.os.Build

/**
 * Created by ghui on 25/03/2017.
 */
object NetConstants {
    const val HTTPS_SCHEME = "https:"
    const val HTTP_SCHEME = "http:"
    const val BASE_URL = "$HTTPS_SCHEME//www.v2ex.com"

    val wapUserAgent =
        "Mozilla/5.0 (Linux; Android 10; V2compose Build/${Build.MODEL}) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Mobile Safari/537.36"

    const val webUserAgent =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4; V2er) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36"
    const val keyUserAgent = "user-agent"

    private const val defaultSystemUserAgent =
        "Dalvik/2.1.0 (Linux; U; Android 12; Mi 10 Build/SKQ1.211006.001)"

    val systemUserAgent: String = System.getProperty("http.agent") ?: defaultSystemUserAgent
}