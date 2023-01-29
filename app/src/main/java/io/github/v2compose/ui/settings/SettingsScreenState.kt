package io.github.v2compose.ui.settings

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import io.github.v2compose.R
import io.github.v2compose.network.bean.Release
import javax.inject.Inject

@Composable
fun rememberSettingsScreenState(
    navHostController: NavHostController,
    context: Context = LocalContext.current,
): SettingsScreenState {
    return remember(navHostController, context) {
        SettingsScreenState(context, navHostController)
    }
}

class SettingsScreenState @Inject constructor(
    private val context: Context,
    private val navHostController: NavHostController,
) {

}