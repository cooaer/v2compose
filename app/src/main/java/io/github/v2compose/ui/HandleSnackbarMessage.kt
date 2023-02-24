package io.github.v2compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HandleSnackbarMessage(
    viewModel: BaseViewModel,
    screenState: BaseScreenState
) {
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    if (!snackbarMessage.isNullOrEmpty()) {
        LaunchedEffect(snackbarMessage) {
            screenState.showMessage(snackbarMessage!!)
        }
    }
}