package io.github.v2compose.ui.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
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
)