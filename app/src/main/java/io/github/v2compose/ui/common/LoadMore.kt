package io.github.v2compose.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import io.github.v2compose.R
import kotlinx.coroutines.delay

fun <T : Any> LazyListScope.pagingRefreshItem(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
) {
    if (lazyPagingItems.loadState.refresh.endOfPaginationReached) return
    if (lazyPagingItems.loadState.refresh is LoadState.NotLoading) return

    item(key = "refresh${lazyPagingItems.itemCount}", contentType = "refresh") {
        PagingLoadState(
            state = lazyPagingItems.loadState.refresh,
            onRetryClick = { lazyPagingItems.retry() }, modifier = modifier,
        )
    }
}

fun <T : Any> LazyListScope.pagingPrependMoreItem(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
) {
    if (lazyPagingItems.loadState.prepend.endOfPaginationReached) return
    if (lazyPagingItems.loadState.prepend is LoadState.NotLoading) return

    item(key = "prependMore${lazyPagingItems.itemCount}", contentType = "prependMore") {
        PagingLoadState(
            state = lazyPagingItems.loadState.prepend,
            onRetryClick = { lazyPagingItems.retry() },
            modifier = modifier,
        )
    }
}

fun <T : Any> LazyListScope.pagingAppendMoreItem(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
) {
    if (lazyPagingItems.loadState.append.endOfPaginationReached) return
    if (lazyPagingItems.loadState.append is LoadState.NotLoading) return

    item(key = "appendMore${lazyPagingItems.itemCount}", contentType = "appendMore") {
        PagingLoadState(
            state = lazyPagingItems.loadState.append,
            onRetryClick = { lazyPagingItems.retry() },
            modifier = modifier,
        )
    }
}

@Composable
fun PagingLoadState(
    state: LoadState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state is LoadState.NotLoading) return
    if (state is LoadState.Loading) {
        var showLoading by remember { mutableStateOf(false) }
        LaunchedEffect(state) {
            //修复：缓存 Pager 的数据后，再次使用缓存数据，会从loading状态快速切换到loaded状态，loading状态会闪现；
            //延迟 20ms 展示loading状态可以避免这种情况；
            delay(20)
            showLoading = true
        }
        if (showLoading) {
            Loading(modifier = modifier)
        }
    } else if (state is LoadState.Error) {
        LoadError(error = state.error, onRetryClick = onRetryClick, modifier = modifier)
    }
}

@Composable
fun LoadMore(
    hasError: Boolean,
    modifier: Modifier = Modifier,
    onRetryClick: (() -> Unit),
    error: Throwable? = null,
) {
    Box(contentAlignment = Alignment.Center) {
        if (hasError) {
            LoadError(error = error, onRetryClick = onRetryClick, modifier = modifier)
        } else {
            Loading(modifier = modifier)
        }
    }
}

private val LoadModifier = Modifier
    .fillMaxWidth()
    .padding(16.dp)
    .sizeIn(minHeight = 96.dp)

@Composable
fun LoadError(error: Throwable?, onRetryClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.then(LoadModifier),
    ) {
        Text(error?.message ?: stringResource(R.string.load_failed))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetryClick) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.then(LoadModifier),
    ) {
        CircularProgressIndicator()
//        Spacer(modifier = Modifier.width(8.dp))
//        Text(stringResource(id = R.string.loading))
    }
}