package io.github.v2compose.ui.common

import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.children
import androidx.core.view.setPadding
import coil.imageLoader
import io.github.v2compose.R
import io.github.v2compose.bean.ContentFormat
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import kotlinx.coroutines.launch

private val ContentBarHeight = 40.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextEditor(
    content: String,
    placeholder: String,
    contentFormat: ContentFormat,
    onContentChanged: (content: String) -> Unit,
    onContentFormatChanged: (format: ContentFormat) -> Unit,
    modifier: Modifier = Modifier,
    contentFocusRequester: FocusRequester = remember { FocusRequester() },
) {
    val tabTitles = remember(contentFormat) {
        if (contentFormat == ContentFormat.Markdown) {
            listOf(R.string.content_body, R.string.content_preview)
        } else {
            listOf(R.string.content_body)
        }
    }
    val pagerState = rememberPagerState()

    Box(modifier = modifier) {
        HorizontalPager(
            pageCount = tabTitles.size,
            state = pagerState,
            key = { tabTitles[it] },
            modifier = Modifier.padding(top = ContentBarHeight)
        ) { index ->
            when (index) {
                0 -> ContentEditor(
                    content = content,
                    placeholder = placeholder,
                    onContentChanged = onContentChanged,
                    modifier = Modifier.focusRequester(contentFocusRequester),
                )
                1 -> MarkdownPreview(content)
            }
        }

        ContentBar(tabTitles, contentFormat, pagerState, onContentFormatChanged)

        ListDivider(modifier = Modifier.padding(top = ContentBarHeight))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentBar(
    tabTitles: List<Int>,
    contentFormat: ContentFormat,
    pagerState: PagerState,
    onContentFormatChanged: (ContentFormat) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(ContentBarHeight)
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        val currentPage =
            if (pagerState.currentPage >= tabTitles.size) 0 else pagerState.currentPage
        TabRow(
            selectedTabIndex = currentPage,
            modifier = Modifier.width(64.dp * tabTitles.size),
            divider = {},
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = index == currentPage,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier.height(ContentBarHeight),
                ) {
                    Text(stringResource(id = title))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        val segments = listOf(ContentFormat.Original, ContentFormat.Markdown)
        var selectedSegment by remember(contentFormat) { mutableStateOf(contentFormat) }
        SegmentedControl(
            segments = segments,
            selectedSegment = selectedSegment,
            onSegmentSelected = {
                selectedSegment = it
                onContentFormatChanged(it)
            },
            modifier = Modifier.sizeIn(maxWidth = 192.dp),
        ) {
            val segmentResId = when (it) {
                ContentFormat.Original -> R.string.content_format_original
                ContentFormat.Markdown -> R.string.content_format_markdown
            }
            Text(stringResource(segmentResId), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentEditor(
    content: String,
    placeholder: String,
    onContentChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(content, TextRange(content.length)))
    }

    TextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onContentChanged(it.text)
        },
        modifier = modifier.fillMaxSize(),
        colors = TextFieldDefaults.textFieldColors(
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Transparent,
            placeholderColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        ),
        placeholder = { Text(placeholder) },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 15.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.3.sp,
        ),
    )
}

@Composable
private fun MarkdownPreview(content: String) {
    val markdown = rememberMarkwon()
    val contentPadding = with(LocalDensity.current) { 16.dp.roundToPx() }
    val contentColor = LocalContentColor.current
    val bodyLineSpacing = with(LocalDensity.current) { 6.sp.toPx() }
    AndroidView(
        factory = { context ->
            val bodyView = TextView(context).apply {
                setPadding(contentPadding)
                setTextColor(contentColor.toArgb())
                textSize = 15f
                setLineSpacing(bodyLineSpacing, 1f)
            }
            ScrollView(context).apply {
                addView(bodyView)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = {
            it.setPadding(0)
            val textView = it.children.first() as TextView
            markdown.setMarkdown(textView, content)
        },
    )

}

@Composable
fun rememberMarkwon(): Markwon {
    val context = LocalContext.current
    return remember(context) {
        Markwon.builder(context).usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(CoilImagesPlugin.create(context, context.imageLoader))
            .usePlugin(MarkwonInlineParserPlugin.create())
            .build()
    }
}

@Preview(widthDp = 440, heightDp = 960, device = "id:Nexus 5")
@Composable
private fun TextEditorPreview() {
    TextEditor(
        content = "",
        placeholder = "",
        contentFormat = ContentFormat.Markdown,
        onContentChanged = {},
        onContentFormatChanged = {},
        modifier = Modifier.fillMaxSize(),
    )
}