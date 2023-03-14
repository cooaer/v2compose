package io.github.cooaer.htmltext

import android.net.Uri
import android.util.Log
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

private const val TAG = "HtmlText"


//TODO 待解决问题：1,斜体
@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    baseUrl: String? = null,
    onImageClick: ((clicked: Img, all: List<Img>) -> Unit)? = null,
    onLinkClick: ((uri: String) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    loadImage: ((String) -> Unit)? = null,
) {
    val document = remember(html) { Jsoup.parse(html) }
    HtmlText(
        document,
        modifier,
        selectable,
        textStyle,
        baseUrl,
        onImageClick,
        onLinkClick,
        onClick,
        loadImage
    )
}

@Composable
fun HtmlText(
    document: Document,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    baseUrl: String? = null,
    onImageClick: ((clicked: Img, all: List<Img>) -> Unit)? = null,
    onLinkClick: ((href: String) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    loadImage: ((String) -> Unit)? = null,
) {
    val allImgs = remember(document) { document.select("img") }

    val scope = HtmlElementsScope(
        baseUrl = baseUrl,
        linkColor = MaterialTheme.colorScheme.tertiary,
        onImageClick = { img ->
            if (onImageClick != null) {
                onImageClick(
                    img.copy(src = img.src.fullUrl(baseUrl)),
                    allImgs.map { ele -> Img(ele).let { it.copy(src = it.src.fullUrl(baseUrl)) } })
            } else {
                onClick?.invoke()
            }
        },
        onLinkClick = onLinkClick,
        onClick = onClick,
        loadImage = loadImage,
    )

    if (selectable) {
        SelectionContainer(modifier = modifier) {
            scope.Block(element = document.body(), textStyle = textStyle)
        }
    } else {
        Box(modifier = modifier) {
            scope.Block(element = document.body(), textStyle = textStyle)
        }
    }
}


@Composable
private fun HtmlElementsScope.BlockToInlineNodes(
    element: Element,
    textStyle: TextStyle,
    clickable: Boolean = true,
) {
    val iterator = element.childNodes().iterator()
    var prevNode: Node? = null
    val tempNodes = mutableListOf<Node>()
    while (iterator.hasNext()) {
        val node = iterator.next()
        if (node is Element) {
            if (node.isBlock || node.onlyContainsImgs() || node.isIframe()) {
//            if (node.isBlock || node.isIframe()) {
                if (tempNodes.isNotBlank()) {
                    InlineNodes(tempNodes.toList(), prevNode, node, textStyle)
                }
                tempNodes.clear()
                prevNode = node
                Block(node, textStyle, clickable)
                if (tempNodes.isNotBlank()) {
                    InlineNodes(tempNodes.toList(), prevNode, node, textStyle)
                }
                tempNodes.clear()
            } else {
                tempNodes.add(node)
            }
        } else if (node is TextNode) {
            if (node.text().isNotEmpty()) {
                tempNodes.add(node)
            }
        }
    }
    if (tempNodes.isNotBlank()) {
        InlineNodes(tempNodes, prevNode, null, textStyle)
    }
}

private fun Collection<Node>.isNotBlank(): Boolean {
    return this.any { it !is TextNode || !it.isBlank }
}

private fun Collection<Node>.isLastBlock(): Boolean {
    return this.isNotEmpty() && this.last().let { last -> last is Element && last.isBlock }
}

//=========== Block Elements Start ============

@Composable
private fun HtmlElementsScope.Block(
    element: Element,
    textStyle: TextStyle,
    clickable: Boolean = true,
) {
    when (element.tagName().lowercase()) {
        "body" -> Body(element, textStyle)
        "h1" -> Hx(element, textStyle.copy(fontSize = 22.sp))
        "h2" -> Hx(element, textStyle.copy(fontSize = 18.sp))
        "h3" -> Hx(element, textStyle.copy(fontSize = 16.sp))
        "h4" -> Hx(element, textStyle.copy(fontSize = 14.sp))
        "h5" -> Hx(element, textStyle.copy(fontSize = 12.sp))
        "h6" -> Hx(element, textStyle.copy(fontSize = 10.sp))
        "p" -> P(element, textStyle)
        "div" -> Div(element, textStyle)
        "ol" -> OlUl(element, true, textStyle)
        "ul" -> OlUl(element, false, textStyle)
        "table" -> Table(element, textStyle)
        "blockquote" -> Blockquote(element, textStyle)
        "hr" -> Hr(element, textStyle)
        "pre" -> Pre(element, textStyle)
        "iframe" -> Iframe(element)
        //将img作为block渲染，解决img嵌入Text时无法调整大小的问题
        "img" -> Img(element, textStyle, clickable)
        "a" -> A(element, textStyle)
        else -> BlockToInlineNodes(element, textStyle)
    }
}

@Composable
private fun HtmlElementsScope.Hr(element: Element, textStyle: TextStyle) {
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
private fun HtmlElementsScope.Body(element: Element, textStyle: TextStyle) {
    Column {
        BlockToInlineNodes(element, textStyle)
    }
}

@Composable
private fun HtmlElementsScope.Blockquote(element: Element, textStyle: TextStyle) {
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
        BlockToInlineNodes(element, textStyle)
    }
}

@Composable
private fun HtmlElementsScope.Hx(element: Element, textStyle: TextStyle) {
    BlockToInlineNodes(element, textStyle.copy(fontWeight = FontWeight.Medium))
}

@Composable
private fun HtmlElementsScope.Table(element: Element, textStyle: TextStyle) {
    val thead =
        element.getElementsByTag("thead").first()?.getElementsByTag("tr")?.first()?.children()
    val tbody = element.getElementsByTag("tbody").first()?.getElementsByTag("tr") ?: return

    val columnCount = if (thead == null) {
        val tbodyTrTds = tbody.first()?.children() ?: throw RuntimeException("bad table")
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
                Column {
                    BlockToInlineNodes(it, textStyle.copy(fontWeight = FontWeight.SemiBold))
                }
            }
        }
        for (row in tbody) {
            items(values = row.children()) {
                Column {
                    BlockToInlineNodes(it, textStyle)
                }
            }
        }
    }
}

@Composable
private fun HtmlElementsScope.Div(element: Element, textStyle: TextStyle) {
    BlockToInlineNodes(element, textStyle)
}


/**
 * 特殊处理 <pre><code> ... </code></pre> 的情况
 */
@Composable
private fun HtmlElementsScope.Pre(element: Element, textStyle: TextStyle) {
    val children = element.children()
    if (children.size == 1 && children.first()!!.tagName().lowercase() == "code") {
        val onlyChild = children.first()!!
        Box(modifier = Modifier.padding(vertical = 4.dp)) {
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
                style = textStyle.copy(fontFamily = FontFamily.Monospace)
            )
        }
    } else {
        BlockToInlineNodes(element, textStyle)
    }
}

@Composable
private fun HtmlElementsScope.OlUl(element: Element, isOrdered: Boolean, textStyle: TextStyle) {
    val children = element.children()
    Column(
        modifier = Modifier
    ) {
        children.forEachIndexed { index, element ->
            Li(
                element = element,
                index = if (isOrdered) index + 1 else null,
                textStyle = textStyle,
            )
        }
    }
}

@Composable
private fun HtmlElementsScope.Li(element: Element, index: Int? = null, textStyle: TextStyle) {
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
            BlockToInlineNodes(element, textStyle)
        }
    }
}

@Composable
private fun HtmlElementsScope.P(element: Element, textStyle: TextStyle) {
    BlockToInlineNodes(element, textStyle)
}

private val imageFormats = listOf("png", "jpg", "jpeg", "webp", "gif")

@Composable
private fun HtmlElementsScope.A(element: Element, textStyle: TextStyle) {
    val href: String? = element.attr("href")
    Box(modifier = Modifier
        .padding(vertical = 4.dp)
        .clickable(enabled = !href.isNullOrEmpty()) {
            Log.d(TAG, "A, click, href = $href")
            onLinkClick?.invoke(href!!)
        }) {

        val isImageUrl = Uri.parse(href)?.let { uri ->
            imageFormats.any { format -> uri.lastPathSegment?.endsWith(".$format", true) == true }
        } ?: false

        BlockToInlineNodes(
            element = element,
            textStyle = textStyle,
            clickable = href.isNullOrEmpty() || isImageUrl
        )
    }
}

@Composable
private fun HtmlElementsScope.Img(
    element: Element,
    textStyle: TextStyle,
    clickable: Boolean,
) {
    Log.d(TAG, "Img = $element")

    BoxWithConstraints {
        val img by remember(element) { mutableStateOf(Img(element)) }
        val (realWidth, realHeight) = rememberImageSize(img = img, maxWidth = maxWidth)

        InlineImage(
            img = img,
            width = realWidth,
            height = realHeight,
            clickable = clickable,
        )
    }
}

@Composable
private fun rememberImageSize(img: Img, maxWidth: Dp): Pair<Dp, Dp> {
    return remember(img) {
        val imgWidthDp = img.width?.dp
        val imgHeightDp = img.height?.dp

        if (img.width == null || img.height == null) {
            Pair(maxWidth, maxWidth / 3f)
        } else if (imgWidthDp!! > maxWidth) {
            Pair(maxWidth, maxWidth * img.height / img.width)
        } else {
            Pair(imgWidthDp, imgHeightDp!!)
        }
    }
}

@Composable
private fun HtmlElementsScope.Iframe(element: Element) {
    Log.d(TAG, "iframe, attrs = ${element.attributes()}")
    if (element.hasClass("embedded_video")) {
        when (element.id()) {
            "ytplayer" -> {
                element.attr("src").parseYouTubeVideoId()?.let {
                    YouTubePlayer(it)
                }
            }
        }
    }
}


//=========== Block Elements End ============


//=========== Inline Elements Start ============

@Composable
private fun HtmlElementsScope.InlineNodes(
    nodes: List<Node>,
    prevNode: Node?,
    nextNode: Node?,
    textStyle: TextStyle
) {
    //消除将非block元素渲染为block元素后，产生的多余的换行
    val inlineNodes = nodes.toMutableList()
    if (prevNode != null && !prevNode.isBlock()) {
        inlineNodes.firstOrNull()?.let {
            if (it.nodeName().lowercase() == "br") {
                inlineNodes.removeFirst()
            }
        }
    }
    if (nextNode != null && !nextNode.isBlock()) {
        inlineNodes.lastOrNull()?.let {
            if (it.nodeName().lowercase() == "br") {
                inlineNodes.removeLast()
            }
        }
    }

    BoxWithConstraints {
        val density = LocalDensity.current

        val allImgs = remember(inlineNodes) {
            inlineNodes.filterIsInstance<Element>()
                .map { ele -> ele.select("img").map { element -> Img(element) } }
                .flatten()
        }

        val annotatedString by remember(inlineNodes) {
            derivedStateOf {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle()) {
                        inlineNodes.forEach { node ->
                            inlineText(node, this@InlineNodes)
                        }
                    }
                }
            }
        }

        val inlineImageMap by remember {
            derivedStateOf {
                allImgs.associateBy({ it.src }, { img ->
                    createInlineTextImage(
                        img = img,
                        maxWidthDp = maxWidth,
                        density = density,
                        scope = this@InlineNodes,
                    )
                })
            }
        }

        val lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        val inlineCheckboxMap = remember(lineHeight, density) {
            mapOf("checkbox" to createInlineCheckbox(lineHeight, density))
        }

        ClickableText(
            annotatedString,
            modifier = Modifier.innermostBlockPadding(),
            inlineContent = mutableMapOf<String, InlineTextContent>().apply {
                putAll(inlineImageMap)
                putAll(inlineCheckboxMap)
            },
            style = textStyle,
            onClick = { offset ->
                Log.d(TAG, "InlineNodes, onClick, text = $annotatedString, offset = $offset")
                val firstAnnotation = annotatedString.getStringAnnotations(
                    tag = "URL",
                    start = offset,
                    end = offset
                ).firstOrNull()
                if (firstAnnotation != null && onLinkClick != null) {
                    Log.d(TAG, "InlineNodes, openUri, uri = ${firstAnnotation.item}")
                    onLinkClick.invoke(firstAnnotation.item)
                } else {
                    onClick?.invoke()
                }
            }
        )
    }
}


private fun createInlineTextImage(
    img: Img,
    maxWidthDp: Dp,
    density: Density,
    scope: HtmlElementsScope,
): InlineTextContent {
    val imgWidthDp = img.width?.dp
    val imgHeightDp = img.height?.dp

    val (componentWidth, componentHeight) = if (img.width == null || img.height == null) {
        Pair(maxWidthDp, maxWidthDp / 3f)
    } else if (imgWidthDp!! > maxWidthDp) {
        Pair(maxWidthDp, maxWidthDp * img.height / img.width)
    } else {
        Pair(imgWidthDp, imgHeightDp!!)
    }

    return InlineTextContent(
        Placeholder(
            width = with(density) { componentWidth.toSp() },
            height = with(density) { componentHeight.toSp() },
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        scope.InlineImage(
            img = img,
            width = componentWidth,
            height = componentHeight,
        )
    }
}

@Composable
private fun HtmlElementsScope.InlineImage(
    img: Img,
    width: Dp,
    height: Dp,
    clickable: Boolean = true
) {
    var currentLoadState by remember(img.loadState) { mutableStateOf(img.loadState) }

    val modifier = Modifier.size(width, height)

    when (currentLoadState) {
        "loading" -> {
            LoadingImage(modifier = Modifier.size(width, maxOf(height, 48.dp)))
        }
        "success" -> {
            val clickEnabled = clickable && onImageClick != null
            AsyncImage(
                model = img.src.fullUrl(baseUrl),
                contentDescription = img.alt,
                contentScale = ContentScale.Crop,
                modifier = if (clickEnabled) modifier.clickable { onImageClick?.invoke(img) } else modifier,
            )
        }
        "error" -> {
            ErrorImage(
                modifier = modifier.clickable {
                    currentLoadState = "loading"
                    loadImage?.invoke(img.src)
                },
            )
        }
        "" -> {
            AutoLoadInlineImage(img = img, modifier = modifier)
        }
    }
}

@Composable
private fun HtmlElementsScope.AutoLoadInlineImage(
    img: Img,
    modifier: Modifier
) {
    var retryTimes by remember { mutableStateOf(0) }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(img.src.fullUrl(baseUrl))
            .setParameter("retryTimes", retryTimes)
            .build(),
        contentDescription = img.alt,
        modifier = modifier,
        success = {
            val imgModifier =
                Modifier.clickable(enabled = onImageClick != null) { onImageClick?.invoke(img) }
            Image(
                painter = rememberAsyncImagePainter(model = it.result.drawable),
                contentDescription = img.alt,
                modifier = imgModifier,
            )
        },
        loading = {
            LoadingImage(modifier = Modifier.fillMaxSize())
        },
        error = {
            ErrorImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { retryTimes++ },
                error = it.result.throwable
            )
        },
        onSuccess = {
            with(it.result.drawable) {
                Log.d(
                    TAG,
                    "load image success, url = ${img.src}, width = ${intrinsicWidth}, height = $intrinsicHeight"
                )
            }
        },
        onError = {
            Log.d(TAG, "load image error, url = ${img.src}, error = ${it.result.throwable}")
        }
    )
}


private fun createInlineCheckbox(lineHeightSp: TextUnit, density: Density): InlineTextContent {
    return InlineTextContent(
        Placeholder(
            width = lineHeightSp,
            height = lineHeightSp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        with(density) {
            Checkbox(
                checked = false,
                enabled = false,
                modifier = Modifier
                    .size(width = lineHeightSp.toDp(), height = lineHeightSp.toDp())
                    .padding(horizontal = 4.dp),
                onCheckedChange = {})
        }
    }
}

private fun AnnotatedString.Builder.inlineText(node: Node, scope: HtmlElementsScope) {
    if (node is TextNode) {
        val text = node.text()
        if (text.isEmpty()) {
            return
        }
        if (text.isBlank()) append(" ") else append(text)
    } else if (node is Element) {
        when (node.tagName().lowercase()) {
            "br" -> {
                append('\n')
            }
            "em" -> {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    childNodesInlineText(node, scope)
                }
            }
            "strong" -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    childNodesInlineText(node, scope)
                }
            }
            "a" -> {
                val href = node.attr("href")
                pushStringAnnotation(tag = "URL", annotation = href)
                withStyle(
                    SpanStyle(
                        color = scope.linkColor,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    childNodesInlineText(node, scope)
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
                    childNodesInlineText(node, scope)
                }
            }
            "img" -> {
                val src = node.attr("src")
                if (src.isNotEmpty()) {
                    appendInlineContent(src)
                }
            }
            "input" -> {
                val type = node.attr("type")
                val checked = node.hasAttr("checked")
                if (type.lowercase() == "checkbox") {
                    appendInlineContent("checkbox")
                }
            }
        }
    }
}

private fun AnnotatedString.Builder.childNodesInlineText(
    element: Element,
    scope: HtmlElementsScope
) {
    for (node in element.childNodes()) {
        if (node is TextNode) {
            append(node.text())
        } else if (node is Element) {
            inlineText(node, scope)
        }
    }
}


//=========== Inline Elements End ============

private data class HtmlElementsScope(
    val baseUrl: String? = null,
    val linkColor: Color = Color.Blue,
    val onImageClick: ((Img) -> Unit)? = null,
    val onLinkClick: ((String) -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
    val loadImage: ((src: String) -> Unit)? = null
)

private fun Modifier.innermostBlockPadding() = this.padding(PaddingValues(vertical = 5.dp))

private fun Node.isBlock(): Boolean = this is Element && this.isBlock

private fun Node.firstChildNode(): Node? = if (childNodeSize() == 0) null else childNode(0)

private fun Element.onlyContainsImgs(): Boolean {
    if (tagName().lowercase() == "img") {
        return true
    }
    if (childNodeSize() == 0) {
        return false
    }
    return childNodes().all { it is Element && it.tagName().lowercase() == "img" }
}

private fun Element.isIframe(): Boolean {
    return tagName().lowercase() == "iframe"
}

@Stable
data class Img(
    val src: String,
    val alt: String,
    val width: Int? = null,
    val height: Int? = null,
    val loadState: String,
) {
    constructor(element: Element) : this(
        element.attr("src"),
        element.attr("alt"),
        element.attr("width").toIntOrNull(),
        element.attr("height").toIntOrNull(),
        element.attr("loadState"),
    )

    fun size() = if (width == null || height == null) null else Size(width, height)

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
