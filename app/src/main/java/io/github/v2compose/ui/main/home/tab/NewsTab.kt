package io.github.v2compose.ui.main.home.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.ui.common.NodeTag
import io.github.v2compose.ui.main.home.NewsTabInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NewsTab(
    newsTabInfo: NewsTabInfo,
) {
    val viewModel: NewsViewModel = rememberNewsViewModel(newsTabInfo.value)

    var newsUiState = viewModel.newsInfo.value
    if (newsUiState !is NewsUiState.Success) {
        newsUiState = viewModel.newsInfo.collectAsStateWithLifecycle().value
    }

    NewsContent(newsUiState)
}

@Composable
private fun rememberNewsViewModel(tabValue: String): NewsViewModel {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val factory = if (viewModelStoreOwner is NavBackStackEntry) {
        HiltViewModelFactory(
            context = LocalContext.current,
            navBackStackEntry = viewModelStoreOwner
        )
    } else {
        null
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
    return MutableCreationExtras().apply {
        set(SAVED_STATE_REGISTRY_OWNER_KEY, LocalSavedStateRegistryOwner.current)
        LocalViewModelStoreOwner.current?.let {
            set(VIEW_MODEL_STORE_OWNER_KEY, it)
        }
        set(DEFAULT_ARGS_KEY, bundleOf(NewsViewModel.KEY_TAB to tabValue))
    }
}

@Composable
fun NewsContent(newsUiState: NewsUiState) {
    when (newsUiState) {
        is NewsUiState.Success -> {
            NewsList(newsInfo = newsUiState.newsInfo)
        }
        is NewsUiState.Loading -> {
            NewsLoading()
        }
        is NewsUiState.Error -> {
            NewsError()
        }
    }
}

@Composable
private fun NewsError() {
    Text("error")
}

@Composable
private fun NewsLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NewsList(newsInfo: NewsInfo, onNewsItemClick: Function1<NewsInfo.Item, Unit>? = null) {
    PullToRefresh(onRefresh = {
        delay(2_000)
    }) {
        val lazyListState = rememberLazyListState()
        LazyColumn(state = lazyListState) {
            items(newsInfo.items, key = { it.id }) {
                NewsItem(it, onNewsItemClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PullToRefresh(onRefresh: suspend () -> Unit, content: @Composable () -> Unit) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        onRefresh()
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = ::refresh
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

@Composable
private fun NewsItem(item: NewsInfo.Item, onItemClick: Function1<NewsInfo.Item, Unit>? = null) {
    Box(modifier = Modifier.clickable { onItemClick?.invoke(item) }) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NewsUserAvatar(item.avatar)
                Spacer(modifier = Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.userName, style = MaterialTheme.typography.bodyLarge)

                    Row {
                        Text(
                            item.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            stringResource(io.github.v2compose.R.string.n_comment, item.replies),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                NodeTag(nodeName = item.tagName, nodeId = item.tagId)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.title, style = MaterialTheme.typography.bodyLarge)
        }
        Divider(
            color = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
@OptIn(ExperimentalGlideComposeApi::class)
private fun NewsUserAvatar(avatar: String) {
    GlideImage(
        model = avatar,
        contentDescription = "avatar",
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
        contentScale = ContentScale.Crop,
    )
}