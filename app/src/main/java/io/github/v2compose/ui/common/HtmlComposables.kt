package io.github.v2compose.ui.common

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import io.github.cooaer.htmltext.HtmlText
import io.github.v2compose.Constants
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import kotlin.experimental.xor

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
    onUriClick: ((uri: String) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    loadImage: ((String, String?) -> Unit)? = null,
    onHtmlImageClick: ((String, List<String>) -> Unit)? = null
) {
    val fixedHtml = rememberFixedHtml(content = content)

    HtmlText(
        html = fixedHtml,
        modifier = modifier,
        selectable = selectable,
        textStyle = textStyle,
        baseUrl = baseUrl,
        onLinkClick = onUriClick,
        onClick = onClick,
        loadImage = { src -> loadImage?.invoke(fixedHtml, src) },
        onImageClick = { clicked, all -> onHtmlImageClick?.invoke(clicked.src, all.map { it.src }) }
    )

    LaunchedEffect(true) {
        loadImage?.invoke(content, null)
    }
}

@Composable
private fun rememberFixedHtml(content: String): String {
    val document = remember(content) { Jsoup.parse(content) }
    var encodedEmails by remember(content) { mutableStateOf<Elements?>(document.select(".__cf_email__")) }
    return remember(content, encodedEmails) {
        if (!encodedEmails.isNullOrEmpty()) {
            encodedEmails?.forEach { fixEmailProtected(it) }
            encodedEmails = null
            document.outerHtml()
        } else content
    }
}

private fun fixEmailProtected(ele: Element) {
    val encodedEmail = ele.attr("data-cfemail")
    try {
        val email = cfDecodeEmail(encodedEmail)
        if (email.isNotEmpty()) {
            val parent = ele.parent()
            val siblingIndex = ele.siblingIndex()
            if (parent != null) {
                ele.remove()
                parent.insertChildren(siblingIndex, TextNode(email))
                if(parent.tagName().equals("a")){
                    parent.attr("href", "mailto:$email")
                }
            }
        }

        Log.d(TAG, "fixEmailProtected, encodedEmail = ${ele.outerHtml()}, decodedEmail = $email")
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        Log.d(TAG, "fixEmailProtected, encodedEmail = ${ele.outerHtml()}")
    }
}

private fun cfDecodeEmail(encodedString: String): String {
    val stringBuilder = StringBuilder()
    val bytes = encodedString.decodeHex()
    val r = bytes[0]
    for (index in 1 until bytes.size) {
        val byte = bytes[index] xor r
        stringBuilder.append(byte.toInt().toChar())
    }
    return stringBuilder.toString()
}

internal fun String.decodeHex(): ByteArray {
    require(length % 2 == 0) { "Unexpected hex string: $this" }

    val result = ByteArray(length / 2)
    for (i in result.indices) {
        val d1 = decodeHexDigit(this[i * 2]) shl 4
        val d2 = decodeHexDigit(this[i * 2 + 1])
        result[i] = (d1 + d2).toByte()
    }
    return result
}

private fun decodeHexDigit(c: Char): Int {
    return when (c) {
        in '0'..'9' -> c - '0'
        in 'a'..'f' -> c - 'a' + 10
        in 'A'..'F' -> c - 'A' + 10
        else -> throw IllegalArgumentException("Unexpected hex digit: $c")
    }
}