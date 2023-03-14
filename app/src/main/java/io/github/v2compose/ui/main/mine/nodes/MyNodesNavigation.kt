package io.github.v2compose.ui.main.mine.nodes

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.network.bean.MyNodesInfo

const val myNodesRoute = "/my/nodes"

fun NavController.navigateToMyNodes() {
    navigate(myNodesRoute)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.myNodesScreen(
    onBackClick: () -> Unit,
    onNodeClick: (MyNodesInfo.Item) -> Unit
) {
    composable(myNodesRoute) {
        MyNodesScreenRoute(onBackClick = onBackClick, onNodeClick = onNodeClick)
    }
}