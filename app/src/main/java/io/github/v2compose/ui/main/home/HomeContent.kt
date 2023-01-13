package io.github.v2compose.ui.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.ui.main.home.tab.NewsTab
import kotlinx.coroutines.launch

private val tabRowHeight = 32.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel = hiltViewModel(),
    onNewsItemClick: (NewsInfo.Item) -> Unit,
) {
    val pagerState = rememberPagerState(0)
    val coroutineScope = rememberCoroutineScope()
    val tabInfos = viewModel.newsTabInfos

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = tabRowHeight),
            pageCount = tabInfos.size,
            state = pagerState,
            key = { tabInfos[it].value },
        ) { page ->
            rememberSaveableStateHolder().SaveableStateProvider(key = page) {
                NewsTab(newsTabInfo = tabInfos[page], onNewsItemClick = onNewsItemClick)
            }
        }

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp,
        ) {
            tabInfos.forEachIndexed { index, tabInfo ->
                val selected = index == pagerState.currentPage
                Tab(
                    selected = selected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier.sizeIn(
                        minWidth = 64.dp,
                        minHeight = 32.dp,
                        maxHeight = 32.dp
                    ),
                ) {
                    Text(
                        tabInfo.name,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 440, heightDp = 880)
@Composable
fun HomeContentPreview() {
    HomeContent(onNewsItemClick = {})
}