package io.github.v2compose.util

import android.net.Uri
import java.net.URL

/**
 * Created by ghui on 02/06/2017.
 */
object UriUtils {

    @JvmStatic
    fun getLastSegment(url: String): String {
        var newUrl = url
        if (Check.isEmpty(newUrl)) return ""
        if (newUrl.contains("#")) {
            newUrl = newUrl.substring(0, newUrl.indexOf("#"))
        }
        return newUrl.replaceFirst(".*/([^/?]+).*".toRegex(), "$1")
    }

    @JvmStatic
    fun getParamValue(url: String?, paramName: String?): String? {
        return if (Check.isEmpty(url)) null else Uri.parse(url).getQueryParameter(paramName)
    }

    fun checkSchema(url: String): String? {
        if (Check.isEmpty(url)) return null
        var newUrl = url
        if (!url.startsWith("http") || !url.startsWith("https")) {
            val schema = if (url.contains("i.v2ex.co")) "https" else "http"
            newUrl = if (url.startsWith("//")) {
                "$schema:$url"
            } else {
                "$schema://$url"
            }
        }
        return if (!isValideUrl(newUrl)) "" else newUrl
    }

    fun isValideUrl(url: String?): Boolean {
        try {
            URL(url).toURI()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * 获取 mimeType
     */
    fun getMimeType(url: String): String {
        return if (url.endsWith(".png") || url.endsWith(".PNG")) {
            "data:image/png;base64,"
        } else if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".JPG") || url.endsWith(
                ".JPEG"
            )
        ) {
            "data:image/jpg;base64,"
        } else if (url.endsWith(".gif") || url.endsWith(".GIF")) {
            "data:image/gif;base64,"
        } else {
            ""
        }
    }

    fun isImg(url: String): Boolean {
        val REGULAR_RULE =
            "(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*\\.(?:jpg|jpeg|gif|png|JPG|JPEG|GIF|PNG))(?:\\?([^#]*))?(?:#(.*))?"
        return url.matches(Regex(REGULAR_RULE))
    }
}