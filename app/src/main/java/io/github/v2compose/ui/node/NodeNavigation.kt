package io.github.v2compose.ui.node

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.network.bean.NodeTopicInfo

private const val ArgsNodeId = "nodeId"
private const val ArgsNodeName = "nodeName"

private const val nodeNavigationRoute = "/go/{$ArgsNodeId}?nodeName={$ArgsNodeName}"

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

fun NavGraphBuilder.nodeScreen(
    onBackClick: () -> Unit,
    onTopicClick: (NodeTopicInfo.Item) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
) {
    composable(
        route = nodeNavigationRoute,
        arguments = listOf(
            navArgument(ArgsNodeId) { type = NavType.StringType },
            navArgument(ArgsNodeName) { type = NavType.StringType })
    ) {
        NodeRoute(
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            onUserAvatarClick = onUserAvatarClick,
            openUri = openUri,
        )
    }
}