package io.github.v2compose

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.v2compose.ui.main.mainNavigationRoute
import io.github.v2compose.ui.main.mainScreen

@Composable
fun AppNavGraph() {
    NavHost(navController = rememberNavController(), startDestination = mainNavigationRoute) {
        mainScreen()
    }
}