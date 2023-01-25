package io.github.v2compose.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.network.bean.NewsInfo

const val mainNavigationRoute = "/"


fun NavController.navigateToMain(navOptions: NavOptions? = null) {
    this.navigate(mainNavigationRoute, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainScreen(
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick:(String, String) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable(route = mainNavigationRoute) {
        MainScreen(
            onNewsItemClick = onNewsItemClick,
            onNodeClick = onNodeClick,
            onUserAvatarClick = onUserAvatarClick,
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
        )
    }
}