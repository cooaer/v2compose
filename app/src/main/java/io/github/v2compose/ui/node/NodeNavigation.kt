package io.github.v2compose.ui.node

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.network.bean.NodeTopicInfo

private const val ArgsNodeName = "nodeName"
private const val ArgsNodeTitle = "nodeTitle"

const val nodeNavigationNavigationRoute = "/go/{$ArgsNodeName}?$ArgsNodeTitle={$ArgsNodeTitle}"

data class NodeArgs(val nodeName: String, val nodeTitle: String? = null) {
    constructor(
        savedStateHandle: SavedStateHandle,
        stringDecoder: StringDecoder
    ) : this(
        nodeName = stringDecoder.decodeString(checkNotNull(savedStateHandle[ArgsNodeName])),
        nodeTitle = savedStateHandle.get<String>(ArgsNodeTitle)
            .let { if (it == null) null else stringDecoder.decodeString(it) }
    )
}

fun NavController.navigateToNode(nodeName: String, nodeTitle: String? = null) {
    val encodedNodeName = Uri.encode(nodeName)
    val encodedNodeTitle = Uri.encode(nodeTitle)
    navigate("/go/$encodedNodeName?$ArgsNodeTitle=${encodedNodeTitle ?: ""}")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.nodeScreen(
    onBackClick: () -> Unit,
    onTopicClick: (NodeTopicInfo.Item) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
) {
    composable(
        route = nodeNavigationNavigationRoute,
        arguments = listOf(
            navArgument(ArgsNodeName) { type = NavType.StringType },
            navArgument(ArgsNodeTitle) {
                type = NavType.StringType
                nullable = true
            })
    ) {
        NodeRoute(
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            onUserAvatarClick = onUserAvatarClick,
            openUri = openUri,
        )
    }
}