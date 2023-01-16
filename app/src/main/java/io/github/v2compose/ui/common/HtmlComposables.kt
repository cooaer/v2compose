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
import io.github.cooaer.htmltext.HtmlText
import io.github.v2compose.Constants

private const val TAG = "HtmlComposables"

@Composable
fun HtmlContent(
    html: String,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    baseUrl: String = Constants.baseUrl,
    onUriClick:((uri: String) -> Unit)? = null,
) {
    val uriHandler = remember(onUriClick) {
        object : UriHandler {
            override fun openUri(uri: String) {
                onUriClick?.invoke(uri)
            }
        }
    }
    CompositionLocalProvider(LocalUriHandler provides uriHandler) {
        HtmlText(
            html = html,
            modifier = modifier,
            selectable = selectable,
            textStyle = textStyle,
            baseUrl = baseUrl,
            onImageClick = { img, allImgs -> Log.d(TAG, "onHtmlImageClick, img = $img") })
    }
}