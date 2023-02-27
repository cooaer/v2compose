package io.github.v2compose.ui.supplement

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.ui.BaseScreenState
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberAddSupplementScreenState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): AddSupplementScreenState {
    return remember(context, coroutineScope, snackbarHostState) {
        AddSupplementScreenState(context, coroutineScope, snackbarHostState)
    }
}

@Stable
class AddSupplementScreenState(
    context: Context,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
): BaseScreenState(context, coroutineScope, snackbarHostState) {


}