package io.github.v2compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.v2compose.ui.main.mainNavigationRoute
import io.github.v2compose.ui.main.mainScreen
import io.github.v2compose.ui.topic.navigateToTopic
import io.github.v2compose.ui.topic.topicScreen

@Composable
fun AppNavGraph(navController: NavHostController, onBackClick: () -> Unit) {
    NavHost(navController = navController, startDestination = mainNavigationRoute) {
        //TODO
        mainScreen(onNewsItemClick = { navController.navigateToTopic(it.id) })
        topicScreen(onBackClick = onBackClick)
    }
}