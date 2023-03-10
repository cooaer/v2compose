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

private const val ArgsNodeId = "nodeId"
private const val ArgsNodeName = "nodeName"

const val nodeNavigationNavigationRoute = "/go/{$ArgsNodeId}?nodeName={$ArgsNodeName}"

data class NodeArgs(val nodeId: String, val nodeName: String? = null) {
    constructor(
        savedStateHandle: SavedStateHandle,
        stringDecoder: StringDecoder
    ) : this(
        nodeId = stringDecoder.decodeString(checkNotNull(savedStateHandle[ArgsNodeId])),
        nodeName = savedStateHandle.get<String>(ArgsNodeName)
            .let { if (it == null) null else stringDecoder.decodeString(it) }
    )
}

fun NavController.navigateToNode(nodeId: String, nodeName: String? = null) {
    val encodedNodeId = Uri.encode(nodeId)
    val encodedNodeName = Uri.encode(nodeName)
    navigate("/go/$encodedNodeId?nodeName=${encodedNodeName ?: ""}")
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
            navArgument(ArgsNodeId) { type = NavType.StringType },
            navArgument(ArgsNodeName) {
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