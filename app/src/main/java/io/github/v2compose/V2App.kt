package io.github.v2compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.github.v2compose.bean.DarkMode
import io.github.v2compose.ui.theme.V2composeTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun V2App(viewModel: V2AppViewModel = viewModel()) {
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

    val darkTheme = when (appSettings.darkMode) {
        DarkMode.FollowSystem -> isSystemInDarkTheme()
        DarkMode.Off -> false
        DarkMode.On -> true
    }

    V2composeTheme(androidTheme = true, darkTheme = darkTheme) {
        val navController = rememberAnimatedNavController()
        val appState = rememberV2AppState(navHostController = navController)
        V2AppNavGraph(
            navController = navController,
            appState = appState,
            appSettings = appSettings,
            viewModel = viewModel,
        )
    }
}
