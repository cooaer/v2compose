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
private const val argsNodeTitle = "node_title"
const val createTopicNavigationRoute =
    "/write?$argsNode={$argsNode}&$argsNodeTitle={$argsNodeTitle}"

data class WriteTopicArgs(val nodeName: String?, val nodeTitle: String?) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        savedStateHandle.get<String>(argsNode)?.let { stringDecoder.decodeString(it) },
        savedStateHandle.get<String>(argsNodeTitle)?.let { stringDecoder.decodeString(it) },
    )
}

fun NavController.navigateToWriteTopic(node: String? = null, nodeTitle: String? = null) {
    val encodedNode = Uri.encode(node) ?: ""
    val encodedNodeTitle = Uri.encode(nodeTitle) ?: ""
    navigate("/write?$argsNode=$encodedNode&$argsNodeTitle=$encodedNodeTitle")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.writeTopicScreen(
    onCloseClick: () -> Unit,
    openUri: (String) -> Unit,
    onCreateTopicSuccess: (topicId: String) -> Unit,
) {
    composable(
        route = createTopicNavigationRoute,
        arguments = listOf(
            navArgument(argsNode) { type = NavType.StringType },
            navArgument(argsNodeTitle) { type = NavType.StringType },
        )
    ) {
        WriteTopicScreenRoute(
            onCloseClick = onCloseClick,
            openUri = openUri,
            onCreateTopicSuccess = onCreateTopicSuccess
        )
    }
}