package io.github.v2compose.ui.common

import android.util.Log
import android.util.Size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
    htmlImageSizes: Map<String, Size> = emptyMap(),
    selectable: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.3.sp,
    ),
    baseUrl: String = Constants.baseUrl,
    onUriClick: ((uri: String) -> Unit)? = null,
) {
    val document = remember(content) { Jsoup.parse(content) }
    val imgElements = remember(document) { document.select("img") }
    val changedImgElements by remember(htmlImageSizes) {
        derivedStateOf {
            imgElements.associateBy { it.attr("src") }.filter {
                Log.d(TAG, "filter, src = ${it.key}, img = ${it.value}")
                htmlImageSizes.containsKey(it.key) }
        }
    }

    val newHtml = if (changedImgElements.isNotEmpty()) {
        changedImgElements.forEach { (src, ele) ->
            htmlImageSizes[src]?.let {
                ele.attr("width", it.width.toString())
                ele.attr("height", it.height.toString())
                Log.d(TAG, "resetImage, newImg = $ele")
            }
        }
        document.outerHtml()
    } else content

    val uriHandler = remember(onUriClick) {
        object : UriHandler {
            override fun openUri(uri: String) {
                onUriClick?.invoke(uri)
            }
        }
    }

    CompositionLocalProvider(LocalUriHandler provides uriHandler) {
        HtmlText(
            html = newHtml,
            modifier = modifier,
            selectable = selectable,
            textStyle = textStyle,
            baseUrl = baseUrl,
            onImageClick = { img, allImgs -> Log.d(TAG, "onHtmlImageClick, img = $img") },
        )
    }
}