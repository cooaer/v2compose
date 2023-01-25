package io.github.v2compose.ui.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

private const val settingsScreenRoute = "/settings"

fun NavController.navigateToSettings() {
    navigate(settingsScreenRoute)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsScreen(onBackClick: () -> Unit, openUri: (String) -> Unit) {
    composable(route = settingsScreenRoute) {
        SettingsScreenRoute(onBackClick = onBackClick, openUri = openUri)
    }
}