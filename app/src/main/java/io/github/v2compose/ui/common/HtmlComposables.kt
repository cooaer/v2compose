package io.github.v2compose.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import io.github.cooaer.htmltext.HtmlText
import io.github.v2compose.Constants

private const val TAG = "HtmlComposables"

typealias OnHtmlImageClick = (String, List<String>) -> Unit

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
    linkFloor: Boolean = false,
    onUriClick: ((uri: String) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    loadImage: ((html: String, img: String?) -> Unit)? = null,
    onHtmlImageClick: ((String, List<String>) -> Unit)? = null
) {

    HtmlText(
        html = content,
        modifier = modifier,
        selectable = selectable,
        textStyle = textStyle,
        baseUrl = baseUrl,
        onLinkClick = onUriClick,
        onClick = onClick,
        loadImage = { src -> loadImage?.invoke(content, src) },
        onImageClick = { clicked, all -> onHtmlImageClick?.invoke(clicked.src, all.map { it.src }) }
    )

    LaunchedEffect(true) {
        loadImage?.invoke(content, null)
    }
}
