package io.github.v2compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.v2compose.ui.main.mainNavigationRoute
import io.github.v2compose.ui.main.mainScreen
import io.github.v2compose.ui.node.navigateToNode
import io.github.v2compose.ui.node.nodeScreen
import io.github.v2compose.ui.topic.navigateToTopic
import io.github.v2compose.ui.topic.topicScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    appState: AppState = rememberAppState(navHostController = navController)
) {
    NavHost(navController = navController, startDestination = mainNavigationRoute) {
        mainScreen(
            onNewsItemClick = { navController.navigateToTopic(it.id) },
            onNodeClick = { nodeId, nodeName -> navController.navigateToNode(nodeId, nodeName) })
        topicScreen(
            onBackClick = onBackClick,
            onNodeClick = { nodeId, nodeName ->
                navController.navigateToNode(
                    nodeId = nodeId,
                    nodeName = nodeName
                )
            },
            onUserAvatarClick = { userName, avatar -> },
            openUri = appState::openUri
        )
        nodeScreen(
            onBackClick = onBackClick,
            onTopicClick = { item -> navController.navigateToTopic(item.topicId) },
            onUserAvatarClick = { userName, avatar -> },
            openUri = appState::openUri
        )
    }
}