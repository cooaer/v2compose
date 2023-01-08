package io.github.cooaer.htmltext

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

//TODO 1,斜体，2，图片，3，内联代码块的样式
@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
) {
    val document = Jsoup.parse(html)
    val scope = HtmlElementsScope()
    SelectionContainer(modifier = modifier) {
        scope.Block(element = document.body())
    }
}

@Composable
private fun HtmlElementsScope.BlockOrInline(
    element: Element,
    textStyle: TextStyle = TextStyle.Default
) {
    val iterator = element.childNodes().iterator()
    val tempNodes = mutableListOf<Node>()
    while (iterator.hasNext()) {
        val node = iterator.next()
        if (node is Element) {
            if (node.isBlock) {
                if (tempNodes.isNotEmpty()) {
                    InlineNodes(tempNodes.toList(), textStyle)
                    tempNodes.clear()
                }
                Block(node, textStyle)
            } else {
                tempNodes.add(node)
            }
        } else if (node is TextNode) {
            if (node.text().isNotBlank()) {
                tempNodes.add(node)
            }
        }
    }
    if (tempNodes.isNotEmpty()) {
        InlineNodes(tempNodes, textStyle)
    }
}


//=========== Block Elements Start ============

@Composable
private fun HtmlElementsScope.Block(element: Element, textStyle: TextStyle = TextStyle.Default) {
    when (element.tagName().lowercase()) {
        "body" -> Body(element)
        "h1" -> Hx(element, textStyle.copy(fontSize = 32.sp))
        "h2" -> Hx(element, textStyle.copy(fontSize = 26.sp))
        "h3" -> Hx(element, textStyle.copy(fontSize = 22.sp))
        "h4" -> Hx(element, textStyle.copy(fontSize = 20.sp))
        "h5" -> Hx(element, textStyle.copy(fontSize = 16.sp))
        "h6" -> Hx(element, textStyle.copy(fontSize = 14.sp))
        "p" -> P(element)
        "div" -> Div(element)
        "ol" -> OlUl(element, true)
        "ul" -> OlUl(element, false)
        "table" -> Table(element)
        "blockquote" -> Blockquote(element)
        "hr" -> Hr(element)
        "pre" -> Pre(element)
    }
}


@Composable
private fun HtmlElementsScope.Hr(element: Element) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )
    )
}

@Composable
private fun HtmlElementsScope.Body(element: Element) {
    Column() {
        BlockOrInline(element)
    }
}

@Composable
private fun HtmlElementsScope.Blockquote(element: Element) {
    val backgroundColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
    val leftBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(color = backgroundColor)
                drawRect(
                    color = leftBorderColor,
                    size = size.copy(width = 4.dp.toPx())
                )
            }
            .padding(start = 8.dp, top = 8.dp, end = 4.dp, bottom = 8.dp)
    ) {
        BlockOrInline(element)
    }
}

@Composable
private fun HtmlElementsScope.Hx(element: Element, textStyle: TextStyle) {
    BlockOrInline(element, textStyle.copy(fontWeight = FontWeight.SemiBold))
}

@Composable
private fun HtmlElementsScope.Table(element: Element) {
    val thead =
        element.getElementsByTag("thead").first()?.getElementsByTag("tr")?.first()?.children()
    val tbody = element.getElementsByTag("tbody").first()?.getElementsByTag("tr") ?: return

    val columnCount = if (thead == null) {
        val tbodyTrTds =
            tbody.first()?.getElementsByTag("td") ?: throw RuntimeException("bad table")
        tbodyTrTds.size
    } else {
        thead.size
    }

    Grid(
        columnCount = columnCount,
        modifier = Modifier
    ) {
        thead?.let {
            items(values = it) {
                Column() {
                    BlockOrInline(it, TextStyle(fontWeight = FontWeight.SemiBold))
                }
            }
        }
        for (row in tbody) {
            items(values = row.children()) {
                Column() {
                    BlockOrInline(it)
                }
            }
        }
    }
}

@Composable
private fun HtmlElementsScope.Div(element: Element) {
    BlockOrInline(element)
}


/**
 * 特殊处理 <pre><code> ... </code></pre> 的情况
 */
@Composable
private fun HtmlElementsScope.Pre(element: Element) {
    val children = element.children()
    if (children.size == 1 && children.first()!!.tagName().lowercase() == "code") {
        val onlyChild = children.first()!!
        Text(
            onlyChild.text(),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                .padding(8.dp),
            style = TextStyle(fontFamily = FontFamily.Monospace)
        )
    } else {
        BlockOrInline(element)
    }
}


@Composable
private fun HtmlElementsScope.OlUl(element: Element, isOrdered: Boolean) {
    val children = element.children()
    Column(
        modifier = Modifier
    ) {
        children.forEachIndexed { index, element ->
            Li(
                element = element,
                index = if (isOrdered) index + 1 else null
            )
        }
    }
}

@Composable
private fun HtmlElementsScope.Li(element: Element, index: Int? = null) {
    Row(
        modifier = Modifier,
    ) {
        DisableSelection {
            if (index == null) {
                Text("・", modifier = Modifier.innermostBlockPadding())
            } else {
                val orderMinWidth = with(LocalDensity.current) {
                    (LocalTextStyle.current.fontSize * 1.4f).toDp()
                }
                Text(
                    "${index}.",
                    modifier = Modifier
                        .innermostBlockPadding()
                        .defaultMinSize(minWidth = orderMinWidth),
                    textAlign = TextAlign.End
                )
            }
        }
        Column {
            BlockOrInline(element)
        }
    }

}

@Composable
private fun HtmlElementsScope.P(element: Element) {
    BlockOrInline(element)
}

//=========== Block Elements End ============


//=========== Inline Elements Start ============

@Composable
private fun HtmlElementsScope.InlineNodes(nodes: List<Node>, textStyle: TextStyle) {
    BoxWithConstraints() {

        val allImgs = parseImgs(nodes.filterIsInstance<Element>())
        val annotatedString = buildAnnotatedString {
            withStyle(style = ParagraphStyle()) {
                for (node in nodes) {
                    inlineText(node)
                }
            }
        }

        val inlineContentMap = allImgs.associateBy({ it.src }, { img ->
            with(LocalDensity.current) {
                var width by remember { mutableStateOf(img.width?.toDp() ?: maxWidth) }
                var height by remember { mutableStateOf(img.height?.toDp() ?: (maxWidth / 3)) }

                InlineTextContent(
                    Placeholder(
                        width = width.toSp(),
                        height = height.toSp(),
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                    )
                ) {
                    BoxWithConstraints {
                        AsyncImage(model = img.src,
                            contentDescription = img.alt,
                            modifier = Modifier.size(width, height),
                            contentScale = ContentScale.FillWidth,
                            placeholder = rememberAsyncImagePainter(
                                R.drawable.image_holder_loading,
                                imageLoader = rememberGifLoader()
                            ),
                            error = painterResource(R.drawable.image_holder_failed),
                            onLoading = { loading ->

                            },
                            onSuccess = { success ->
                                with(success.result.drawable) {
                                    width = min(intrinsicWidth.toDp(), maxWidth)
                                    height = if (intrinsicWidth > maxWidth.toPx()) {
                                        maxWidth * intrinsicHeight / intrinsicWidth
                                    } else {
                                        intrinsicHeight.toDp()
                                    }
                                }
                            },
                            onError = { error ->

                            })
                    }
                }
            }
        })

        val uriHandler = LocalUriHandler.current

        ClickableText(
            annotatedString,
            modifier = Modifier.innermostBlockPadding(),
            inlineContent = inlineContentMap,
            style = textStyle,
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    tag = "URL",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    try {
                        uriHandler.openUri(it.item)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )
    }

}

@Composable
private fun rememberGifLoader(): ImageLoader {
    return ImageLoader.Builder(LocalContext.current).components {
        if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
    }.build()
}

private fun AnnotatedString.Builder.inlineText(node: Node) {
    if (node is TextNode) {
        append(node.text())
    } else if (node is Element) {
        when (node.tagName().lowercase()) {
            "br" -> {
                append('\n')
            }
            "em" -> {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    childNodesInlineText(node)
                }
            }
            "strong" -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    childNodesInlineText(node)
                }
            }
            "a" -> {
                val href = node.attr("href")
                pushStringAnnotation(tag = "URL", annotation = href)
                withStyle(
                    SpanStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    childNodesInlineText(node)
                }
                pop()
            }
            "code" -> {
                //使用对代码优化显示的字体
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color(0x1f000000)
                    )
                ) {
                    childNodesInlineText(node)
                }
            }
            "img" -> {
                val src = node.attr("src")
                if (src.isNotEmpty()) {
                    appendInlineContent(src)
                }
            }

        }
    }
}

private fun AnnotatedString.Builder.childNodesInlineText(element: Element) {
    for (node in element.childNodes()) {
        if (node is TextNode) {
            append(node.text())
        } else if (node is Element) {
            inlineText(node)
        }
    }
}


//=========== Inline Elements End ============

data class HtmlElementsScope(val baseUrl: String? = null)

private fun Modifier.innermostBlockPadding() = this.padding(PaddingValues(vertical = 5.dp))

private fun Node.isBlock(): Boolean = this is Element && this.isBlock

private fun Node.firstChildNode(): Node? = if (childNodeSize() == 0) null else childNode(0)

private data class Img(
    val src: String,
    val alt: String? = null,
    val width: Int? = null,
    val height: Int? = null,
)

private fun parseImgs(elements: List<Element>): List<Img> {
    val result = mutableListOf<Img>()
    for (element in elements) {
        if (element.tagName().lowercase() == "img") {
            result.add(
                Img(
                    src = element.attr("src"),
                    alt = element.attr("alt"),
                    width = element.attr("width").toIntOrNull(),
                    height = element.attr("height").toIntOrNull(),
                )
            )
        } else {
            parseImgs(element.children())
        }
    }
    return result
}