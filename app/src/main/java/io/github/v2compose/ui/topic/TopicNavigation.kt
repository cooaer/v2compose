package io.github.v2compose.ui.topic

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import io.github.v2compose.core.StringDecoder

private const val argTopicId: String = "topicId"

private const val topicNavigationRoute = "/t/{$argTopicId}"

class TopicArgs(val topicId: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        stringDecoder.decodeString(
            checkNotNull(savedStateHandle[argTopicId])
        )
    )
}

fun NavController.navigateToTopic(topicId: String) {
    val encodedTopicId = Uri.encode(topicId)
    navigate("/t/$encodedTopicId")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.topicScreen(
    onBackClick: () -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri:(String) -> Unit,
) {
    composable(
        topicNavigationRoute,
        arguments = listOf(navArgument(argTopicId) { type = NavType.StringType })
    ) {
        TopicScreenRoute(
            onBackClick = onBackClick,
            onNodeClick = onNodeClick,
            onUserAvatarClick = onUserAvatarClick,
            openUri = openUri,
        )
    }
}