package io.github.v2compose.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import io.github.v2compose.R

@Composable
fun <T : Any> PagingAppendMore(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit,
) {
    with(lazyPagingItems.loadState.append) {
        LoadMore(
            hasError = this is LoadState.Error,
            error = if (this is LoadState.Error) error else null,
            modifier = modifier,
            onRetryClick = onRetryClick
        )
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
    .sizeIn(minHeight = 96.dp);

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