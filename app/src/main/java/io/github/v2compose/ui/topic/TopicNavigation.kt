package io.github.v2compose.ui.topic

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.ui.common.OnHtmlImageClick

private const val argsTopicId: String = "topicId"
private const val argsReplyFloor: String = "floor"

const val topicNavigationRoute = "/t/{$argsTopicId}#reply{$argsReplyFloor}"

data class TopicArgs(val topicId: String, val replyFloor: Int) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        stringDecoder.decodeString(checkNotNull(savedStateHandle[argsTopicId])),
        checkNotNull(savedStateHandle[argsReplyFloor]),
    )
}

fun NavController.navigateToTopic(
    topicId: String,
    replyFloor: Int = 0,
    navOptions: NavOptions? = null
) {
    val encodedTopicId = Uri.encode(topicId)
    navigate("/t/$encodedTopicId#reply$replyFloor", navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.topicScreen(
    onBackClick: () -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
    onAddSupplementClick: (String) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
) {
    composable(
        topicNavigationRoute,
        arguments = listOf(
            navArgument(argsTopicId) { type = NavType.StringType },
            navArgument(argsReplyFloor) { type = NavType.IntType },
        )
    ) {
        TopicScreenRoute(
            onBackClick = onBackClick,
            onNodeClick = onNodeClick,
            onUserAvatarClick = onUserAvatarClick,
            openUri = openUri,
            onAddSupplementClick = onAddSupplementClick,
            onHtmlImageClick = onHtmlImageClick,
        )
    }
}