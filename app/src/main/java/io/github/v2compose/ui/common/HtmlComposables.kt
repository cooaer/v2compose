package io.github.v2compose.ui.common

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import io.github.cooaer.htmltext.HtmlText
import io.github.v2compose.Constants
import org.jsoup.Jsoup

private const val TAG = "HtmlComposables"

@Composable
fun HtmlContent(
    content: String,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.3.sp,
    ),
    baseUrl: String = Constants.baseUrl,
    onUriClick: ((uri: String) -> Unit)? = null,
    onContentChanged: ((String) -> Unit)? = null,
) {
    val document = remember(content) { Jsoup.parse(content) }
    val imgElements = remember(content) { document.select("img") }

    val uriHandler = remember(onUriClick) {
        object : UriHandler {
            override fun openUri(uri: String) {
                onUriClick?.invoke(uri)
            }
        }
    }

    CompositionLocalProvider(LocalUriHandler provides uriHandler) {
        HtmlText(
            html = content,
            modifier = modifier,
            selectable = selectable,
            textStyle = textStyle,
            baseUrl = baseUrl,
            onImageClick = { img, allImgs -> Log.d(TAG, "onHtmlImageClick, img = $img") },
            onImageLoaded = { img ->
                onContentChanged?.let {
                    document.select("img[src=\"${img.src}\"]").forEach { ele ->
                        ele.attr("width", img.width.toString())
                        ele.attr("height", img.height.toString())

                        Log.d(TAG, "onContentChanged, newImg = $ele")
                    }
                    val newHtml = document.outerHtml()
                    if (newHtml != content) {
                        Log.d(TAG, "onContentChanged, callback, outerHtml = $newHtml")
                        it(newHtml)
                    }
                }
            },
        )
    }
}