package io.github.v2compose.ui.main.home.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.core.os.bundleOf
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.ui.common.LoadMore
import io.github.v2compose.ui.common.SimpleTopic
import io.github.v2compose.ui.main.home.NewsTabInfo

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NewsTab(
    newsTabInfo: NewsTabInfo,
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
) {
    val viewModel: NewsViewModel = newsViewModel(newsTabInfo.value)

    val newsUiState by viewModel.newsInfoFlow.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshingFlow.collectAsStateWithLifecycle()

    NewsContent(
        refreshing = refreshing,
        newsUiState = newsUiState,
        onNewsItemClick = onNewsItemClick,
        onRefreshList = { viewModel.refresh() },
        onRetryClick = { viewModel.retry() },
        onNodeClick = onNodeClick,
    )
}

@Composable
private fun newsViewModel(tabValue: String): NewsViewModel {
    val context = LocalContext.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val factory = remember(context, viewModelStoreOwner) {
        if (viewModelStoreOwner is NavBackStackEntry) {
            HiltViewModelFactory(context = context, navBackStackEntry = viewModelStoreOwner)
        } else {
            null
        }
    }
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = tabValue,
        factory = factory,
        extras = rememberCreationExtras(tabValue)
    )
}

@Composable
private fun rememberCreationExtras(tabValue: String): CreationExtras {
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    return remember(tabValue, savedStateRegistryOwner, viewModelStoreOwner) {
        MutableCreationExtras().apply {
            set(SAVED_STATE_REGISTRY_OWNER_KEY, savedStateRegistryOwner)
            viewModelStoreOwner?.let {
                set(VIEW_MODEL_STORE_OWNER_KEY, it)
            }
            set(DEFAULT_ARGS_KEY, bundleOf(NewsViewModel.KEY_TAB to tabValue))
        }
    }
}

@Composable
fun NewsContent(
    refreshing: Boolean,
    newsUiState: NewsUiState,
    onNewsItemClick: ((NewsInfo.Item) -> Unit),
    onNodeClick: (String, String) -> Unit,
    onRetryClick: () -> Unit,
    onRefreshList: () -> Unit
) {
    when (newsUiState) {
        is NewsUiState.Success -> {
            NewsList(
                refreshing = refreshing,
                newsInfo = newsUiState.newsInfo,
                onRefresh = onRefreshList,
                onNewsItemClick = onNewsItemClick,
                onNodeClick = onNodeClick,
            )
        }
        else -> {
            LoadMore(
                hasError = newsUiState is NewsUiState.Error,
                error = if (newsUiState is NewsUiState.Error) newsUiState.throwable else null,
                modifier = Modifier.fillMaxSize(),
                onRetryClick = onRetryClick
            )
        }
    }
}

@Composable
private fun NewsList(
    refreshing: Boolean,
    newsInfo: NewsInfo,
    onRefresh: () -> Unit,
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
) {
    PullToRefresh(refreshing = refreshing, onRefresh = onRefresh) {
        val lazyListState = rememberLazyListState()
        LazyColumn(state = lazyListState) {
            items(newsInfo.items, key = { it.id }) { item ->
                SimpleTopic(
                    title = item.title,
                    userName = item.userName,
                    avatar = item.avatar,
                    time = item.time,
                    replyNum = item.replies.toString(),
                    nodeId = item.tagId,
                    nodeName = item.tagName,
                    onItemClick = { onNewsItemClick(item) },
                    onNodeClick = { onNodeClick(item.tagId, item.tagName) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PullToRefresh(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = onRefresh
    )

    Box(
        Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {

        content()

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}