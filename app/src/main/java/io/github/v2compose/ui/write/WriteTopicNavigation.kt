package io.github.v2compose.ui.write

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.core.StringDecoder

private const val argsNode = "node"
private const val argsNodeName = "node_name"
private const val createTopicRoute =
    "/write?$argsNode={$argsNode}&$argsNodeName={$argsNodeName}"

data class WriteTopicArgs(val nodeId: String?, val nodeName: String?) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        savedStateHandle.get<String>(argsNode)?.let { stringDecoder.decodeString(it) },
        savedStateHandle.get<String>(argsNodeName)?.let { stringDecoder.decodeString(it) },
    )
}

fun NavController.navigateToWriteTopic(node: String? = null, nodeName: String? = null) {
    val encodedNode = Uri.encode(node) ?: ""
    val encodedNodeName = Uri.encode(nodeName) ?: ""
    navigate("/write?$argsNode=$encodedNode&$argsNodeName=$encodedNodeName")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.writeTopicScreen(
    onCloseClick: () -> Unit,
    openUri: (String) -> Unit,
    onCreateTopicSuccess: (topicId: String) -> Unit,
) {
    composable(
        route = createTopicRoute,
        arguments = listOf(
            navArgument(argsNode) { type = NavType.StringType },
            navArgument(argsNodeName) { type = NavType.StringType },
        )
    ) {
        WriteTopicScreenRoute(
            onCloseClick = onCloseClick,
            openUri = openUri,
            onCreateTopicSuccess = onCreateTopicSuccess
        )
    }
}