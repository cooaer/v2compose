package io.github.v2compose.ui.main

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.LocalSnackbarHostState
import io.github.v2compose.ui.BaseScreenState
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberMainScreenState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current
): MainScreenState {
    return remember(context, snackbarHostState) {
        MainScreenState(context, coroutineScope, snackbarHostState)
    }
}

class MainScreenState(
    context: Context,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) : BaseScreenState(context, coroutineScope, snackbarHostState)