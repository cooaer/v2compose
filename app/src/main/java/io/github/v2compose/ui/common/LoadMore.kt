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
        AppendMore(
            hasError = this is LoadState.Error,
            error = if (this is LoadState.Error) error else null,
            modifier = modifier,
            onRetryClick = onRetryClick
        )
    }
}

@Composable
fun AppendMore(
    hasError: Boolean,
    modifier: Modifier = Modifier,
    error: Throwable? = null,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .sizeIn(minHeight = 96.dp),
        contentAlignment = Alignment.Center
    ) {
        if (hasError) {
            LoadError(error = error, onRetryClick = onRetryClick)
        } else {
            Loading()
        }
    }
}


@Composable
private fun LoadError(error: Throwable?, onRetryClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(error?.message ?: stringResource(R.string.load_failed))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetryClick) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Composable
private fun Loading() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator()
//        Spacer(modifier = Modifier.width(8.dp))
//        Text(stringResource(id = R.string.loading))
    }
}