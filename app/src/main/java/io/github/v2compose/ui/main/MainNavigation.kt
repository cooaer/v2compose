package io.github.v2compose.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.network.bean.RecentTopics
import io.github.v2compose.ui.common.OnHtmlImageClick

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
    onRecentItemClick: (RecentTopics.Item) -> Unit,
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
    onHtmlImageClick: OnHtmlImageClick,
) {
    composable(route = mainNavigationRoute) {
        MainScreenRoute(
            onNewsItemClick = onNewsItemClick,
            onRecentItemClick = onRecentItemClick,
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
            onHtmlImageClick = onHtmlImageClick,
        )
    }
}