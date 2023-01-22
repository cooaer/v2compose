package io.github.v2compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.v2compose.ui.main.mainNavigationRoute
import io.github.v2compose.ui.main.mainScreen
import io.github.v2compose.ui.main.user.navigateToUser
import io.github.v2compose.ui.main.user.userScreen
import io.github.v2compose.ui.node.navigateToNode
import io.github.v2compose.ui.node.nodeScreen
import io.github.v2compose.ui.search.navigateToSearch
import io.github.v2compose.ui.search.searchScreen
import io.github.v2compose.ui.topic.navigateToTopic
import io.github.v2compose.ui.topic.topicScreen

@Composable
fun V2AppNavGraph(
    navController: NavHostController,
    appState: V2AppState,
) {
    NavHost(navController = navController, startDestination = mainNavigationRoute) {
        mainScreen(
            onNewsItemClick = { navController.navigateToTopic(it.id) },
            onNodeClick = navController::navigateToNode,
            onUserAvatarClick = navController::navigateToUser,
            onSearchClick = navController::navigateToSearch,
            onSettingsClick = {},
        )
        topicScreen(
            onBackClick = appState::back,
            onNodeClick = navController::navigateToNode,
            onUserAvatarClick = navController::navigateToUser,
            openUri = appState::openUri
        )
        nodeScreen(
            onBackClick = appState::back,
            onTopicClick = { item -> navController.navigateToTopic(item.topicId) },
            onUserAvatarClick = navController::navigateToUser,
            openUri = appState::openUri
        )
        searchScreen(
            goBack = appState::back,
            onTopicClick = { item -> navController.navigateToTopic(item.source.id) },
        )
        userScreen(
            onBackClick = appState::back,
            onTopicClick = appState::openUri,
            onNodeClick = { nodePath, nodeName -> appState.openUri(nodePath) },
            openUri = appState::openUri
        )
    }
}