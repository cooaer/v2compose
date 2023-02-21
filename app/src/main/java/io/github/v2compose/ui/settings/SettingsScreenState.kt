package io.github.v2compose.ui.settings

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.R
import io.github.v2compose.network.bean.Release
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@Composable
fun rememberSettingsScreenState(
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): SettingsScreenState {
    return remember(context, snackbarHostState) {
        SettingsScreenState(context, snackbarHostState)
    }
}

class SettingsScreenState @Inject constructor(
    private val context: Context,
    val snackbarHostState: SnackbarHostState,
) {

    suspend fun checkForUpdates(
        checkForUpdates: suspend () -> Release,
        onNewRelease: (Release) -> Unit,
    ) = coroutineScope {
        val showSnackbar =
            async {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.checking_for_updates),
                    duration = SnackbarDuration.Short,
                )
            }
        val check = async { checkForUpdates() }
        val release = check.await()
        showSnackbar.cancel()
        if (release.isValid()) {
            onNewRelease(release)
        } else {
            snackbarHostState.showSnackbar(
                context.getString(R.string.no_updates),
                duration = SnackbarDuration.Short,
            )
        }
    }

    suspend fun logout(logout: suspend () -> Unit) {
        logout()
        snackbarHostState.showSnackbar(
            context.getString(R.string.logout_success),
            duration = SnackbarDuration.Short
        )
    }

}