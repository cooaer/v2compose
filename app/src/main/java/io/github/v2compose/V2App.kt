package io.github.v2compose

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import io.github.v2compose.ui.theme.V2composeTheme

@Composable
fun V2App() {
    V2composeTheme(androidTheme = true) {
        val navController = rememberNavController()
        val appState = rememberV2AppState(navHostController = navController)
        V2AppNavGraph(
            navController = navController,
            appState = appState,
        )
    }
}
