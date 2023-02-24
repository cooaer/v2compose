package io.github.v2compose.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseScreenState(
    protected val context: Context,
    protected val coroutineScope: CoroutineScope,
    val snackbarHostState: SnackbarHostState,
) {

    fun showMessage(@StringRes messageResId: Int) {
        showMessage(context.getString(messageResId))
    }

    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

}