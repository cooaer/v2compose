package io.github.v2compose.ui.main.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.ui.main.home.tab.NewsTab
import kotlinx.coroutines.launch

private val TabRowHeight = 32.dp

private const val TAG = "HomeContent"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState()

    val coroutineScope = rememberCoroutineScope()
    val tabInfos = viewModel.newsTabInfos

    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = TabRowHeight),
            pageCount = tabInfos.size,
            state = pagerState,
            key = { tabInfos[it].value },
        ) { page ->
            NewsTab(
                newsTabInfo = tabInfos[page],
                onNewsItemClick = onNewsItemClick,
                onNodeClick = onNodeClick,
                onUserAvatarClick = onUserAvatarClick,
            )
        }

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 12.dp,
        ) {
            tabInfos.forEachIndexed { index, tabInfo ->
                val selected = index == pagerState.currentPage
                Tab(
                    selected = selected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                            Log.d(TAG, "animateScrollToPage, index = $index")
                        }
                    },
                    modifier = Modifier.height(TabRowHeight)
                ) {
                    Text(
                        tabInfo.name,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}
