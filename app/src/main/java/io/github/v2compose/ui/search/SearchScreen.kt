package io.github.v2compose.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.v2compose.R
import io.github.v2compose.core.extension.toTimeText
import io.github.v2compose.network.bean.SoV2EXSearchResultInfo
import io.github.v2compose.ui.common.pagingAppendMoreItem
import io.github.v2compose.ui.common.pagingRefreshItem
import io.github.v2compose.ui.common.rememberLazyListState
import kotlinx.coroutines.delay

@Composable
fun SearchScreenRoute(
    goBack: () -> Unit,
    onTopicClick: (SoV2EXSearchResultInfo.Hit) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val keyword by viewModel.keyword.collectAsStateWithLifecycle()
    val lazyPagingItems = viewModel.topics.collectAsLazyPagingItems()

    SearchScreen(
        keyword = keyword,
        lazyPagingItems = lazyPagingItems,
        onCloseClick = goBack,
        onTopicClick = onTopicClick,
        onSearchClick = { viewModel.updateKeyword(it) },
    )
}

//TODO: 支持更多选项，1选择时间范围（一个小时内、一天内、一周内、一个月内、一年内，时间不限），2排序方式(默认，发帖时间)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    keyword: String?,
    lazyPagingItems: LazyPagingItems<SoV2EXSearchResultInfo.Hit>,
    onCloseClick: () -> Unit,
    onTopicClick: (SoV2EXSearchResultInfo.Hit) -> Unit,
    onSearchClick: (String) -> Unit,
) {
//    val backgroundColor = if (lazyPagingItems.itemCount == 0) {
//        MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.disabled)
//    } else {
//        MaterialTheme.colorScheme.background
//    }
    val backgroundColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)

    Scaffold(modifier = Modifier.background(color = backgroundColor)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .padding(it)
        ) {
            SearchResult(
                keyword = keyword,
                lazyPagingItems = lazyPagingItems,
                onTopicClick = onTopicClick
            )
            SearchBar(keyword = keyword, onCloseClick = onCloseClick, onSearchClick = onSearchClick)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(keyword: String?, onCloseClick: () -> Unit, onSearchClick: (String) -> Unit) {
    var initialKeyword by remember(keyword) { mutableStateOf(keyword ?: "") }
    var autoShowKeyboard by rememberSaveable { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = initialKeyword,
        onValueChange = { initialKeyword = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(12.dp),
            )
            .focusRequester(focusRequester)
            .onFocusChanged { },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            onSearchClick(initialKeyword)
            keyboard?.hide()
        }),
        singleLine = true,
        placeholder = {
            Icon(
                painter = painterResource(id = R.drawable.logo_sov2ex),
                contentDescription = "sov2ex logo",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.disabled)
            )
        },
        trailingIcon = {
            Icon(imageVector = Icons.Rounded.Close,
                contentDescription = "close",
                modifier = Modifier.clickable {
                    if (initialKeyword.isNotEmpty()) {
                        initialKeyword = ""
                    } else {
                        onCloseClick()
                    }
                })
        },
        shape = RoundedCornerShape(12.dp),
    )

    LaunchedEffect(focusRequester) {
        if (autoShowKeyboard) {
            focusRequester.requestFocus()
            delay(100)
            keyboard?.show()
            autoShowKeyboard = false
        }
    }
}

@Composable
private fun SearchResult(
    keyword: String?,
    lazyPagingItems: LazyPagingItems<SoV2EXSearchResultInfo.Hit>,
    onTopicClick: (SoV2EXSearchResultInfo.Hit) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 72.dp),
        state = lazyPagingItems.rememberLazyListState(),
    ) {
        if (!keyword.isNullOrEmpty()) {
            pagingRefreshItem(lazyPagingItems)
        }
        itemsIndexed(items = lazyPagingItems,
            key = { index, item -> item.source.id }) { index, item ->
            item?.let {
                SearchTopic(topic = item, onTopicClick = onTopicClick)
            }
        }
        if (!keyword.isNullOrEmpty()) {
            pagingAppendMoreItem(lazyPagingItems)
        }
    }
}

@Composable
private fun SearchTopic(
    topic: SoV2EXSearchResultInfo.Hit,
    onTopicClick: (SoV2EXSearchResultInfo.Hit) -> Unit,
) {
    val highlightContent: String = remember(topic) {
        val content = topic.highlight.content.firstOrNull()
        val supplement = topic.highlight.postscriptListContent.firstOrNull()
        val reply = topic.highlight.replyListContent.firstOrNull()
        listOf(content, supplement, reply).filterIsInstance<String>()
            .joinToString(separator = "...").ifEmpty { topic.source.content }
    }

    Column {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { onTopicClick(topic) }
            .background(color = MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp, horizontal = 16.dp)) {
            SearchTopicText(
                text = topic.highlight.title.firstOrNull() ?: topic.source.title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            SearchTopicText(
                text = highlightContent,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.3.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(
                    id = R.string.search_user_time_replies,
                    topic.source.creator,
                    topic.source.time.toTimeText(LocalContext.current),
                    topic.source.replies,
                ),
                style = MaterialTheme.typography.labelSmall,
                color = LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SearchTopicText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    val annotatedString = buildAnnotatedString {
        var start = 0
        while (true) {
            val index = text.indexOf("<em>", startIndex = start)
            if (index < 0 || start > index) {
                break
            }
            append(text.substring(start, index))
            val endIndex = text.indexOf("</em>", startIndex = start)
            if (endIndex < 0) {
                break
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append(text.substring(index + 4, endIndex))
            }
            start = endIndex + 5
        }
        if (start < text.length) {
            append(text.substring(start))
        }
    }
    Text(
        text = annotatedString,
        modifier = modifier,
        style = style,
        maxLines = maxLines,
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    SearchBar(keyword = "", onCloseClick = {}, onSearchClick = {})
}