package io.github.v2compose.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.github.v2compose.network.bean.NewsInfo

const val mainNavigationRoute = "main"


fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    this.navigate(mainNavigationRoute, navOptions)
}

fun NavGraphBuilder.mainScreen(onNewsItemClick: (NewsInfo.Item) -> Unit) {
    composable(route = mainNavigationRoute) {
        MainScreen(onNewsItemClick = onNewsItemClick)
    }
}