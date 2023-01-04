package io.github.v2compose.ui.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.*
import io.github.v2compose.ui.main.home.tab.NewsTab
import kotlinx.coroutines.launch

private val tabRowHeight = 32.dp

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel = viewModel()
) {
    val pagerState = rememberPagerState(0)
    val coroutineScope = rememberCoroutineScope()
    val tabInfos = viewModel.newsTabInfos

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = tabRowHeight),
            count = tabInfos.size,
            state = pagerState,
            flingBehavior = pagerShortFlingBehavior(
                pagerState = pagerState,
                noOfPages = tabInfos.size
            ),
            key = { tabInfos[it].value },
        ) { page ->
            rememberSaveableStateHolder().SaveableStateProvider(key = page) {
                NewsTab(newsTabInfo = tabInfos[page])
            }
        }

        ScrollableTabRow(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp,
            indicator = { tabPosition ->
                TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, tabPosition))
            },
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

@OptIn(ExperimentalPagerApi::class, dev.chrisbanes.snapper.ExperimentalSnapperApi::class)
@Composable
fun pagerShortFlingBehavior(pagerState: PagerState, noOfPages: Int): FlingBehavior {
    val minFlingDistanceDp = 80.dp
    var currentPageIndex = pagerState.currentPage
    return PagerDefaults.flingBehavior(
        state = pagerState,
        snapIndex = { layoutInfo, _, _ ->
            val distanceToStartSnap = layoutInfo.distanceToIndexSnap(currentPageIndex)
            currentPageIndex = when {
                distanceToStartSnap < -(minFlingDistanceDp.value) -> {
                    (currentPageIndex + 1).coerceAtMost(noOfPages - 1)
                }
                distanceToStartSnap > minFlingDistanceDp.value -> {
                    (currentPageIndex - 1).coerceAtLeast(0)
                }
                else -> {
                    currentPageIndex
                }
            }
            currentPageIndex
        }
    )
}

@Preview(showBackground = true, widthDp = 440, heightDp = 880)
@Composable
fun HomeContentPreview() {
    HomeContent()
}