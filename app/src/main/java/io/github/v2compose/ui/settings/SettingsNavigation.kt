package io.github.v2compose.ui.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

const val settingsScreenNavigationRoute = "/settings"

fun NavController.navigateToSettings() {
    navigate(settingsScreenNavigationRoute)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    openUri: (String) -> Unit,
    onLogoutSuccess: () -> Unit
) {
    composable(route = settingsScreenNavigationRoute) {
        SettingsScreenRoute(
            onBackClick = onBackClick,
            openUri = openUri,
            onLogoutSuccess = onLogoutSuccess
        )
    }
}