package io.github.v2compose.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.network.bean.NewsInfo

const val mainNavigationRoute = "/"

fun NavController.navigateToMain() {
    navigate(mainNavigationRoute) {
        popUpTo(mainNavigationRoute) {
            inclusive = true
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainScreen(
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onSearchClick: () -> Unit,
    onLoginClick: () -> Unit,
    onMyHomePageClick: () -> Unit,
    onCreateTopicClick: () -> Unit,
    onMyNodesClick: () -> Unit,
    onMyTopicsClick: () -> Unit,
    onMyFollowingClick: () -> Unit,
    onSettingsClick: () -> Unit,
    openUri: (String) -> Unit,
) {
    composable(route = mainNavigationRoute) {
        MainScreenRoute(
            onNewsItemClick = onNewsItemClick,
            onNodeClick = onNodeClick,
            onUserAvatarClick = onUserAvatarClick,
            onSearchClick = onSearchClick,
            onLoginClick = onLoginClick,
            onMyHomePageClick = onMyHomePageClick,
            onCreateTopicClick = onCreateTopicClick,
            onMyNodesClick = onMyNodesClick,
            onMyTopicsClick = onMyTopicsClick,
            onMyFollowingClick = onMyFollowingClick,
            onSettingsClick = onSettingsClick,
            openUri = openUri,
        )
    }
}