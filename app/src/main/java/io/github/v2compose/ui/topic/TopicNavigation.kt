package io.github.v2compose.ui.topic

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.ui.common.OnHtmlImageClick

private const val argTopicId: String = "topicId"

private const val topicNavigationRoute = "/t/{$argTopicId}"

fun topicRoute(topicId: String) = "/t/$topicId"

class TopicArgs(val topicId: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        stringDecoder.decodeString(
            checkNotNull(savedStateHandle[argTopicId])
        )
    )
}

fun NavController.navigateToTopic(topicId: String, navOptions: NavOptions? = null) {
    val encodedTopicId = Uri.encode(topicId)
    navigate("/t/$encodedTopicId", navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.topicScreen(
    onBackClick: () -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
    onAddSupplementClick:(String) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
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
            onAddSupplementClick = onAddSupplementClick,
            onHtmlImageClick = onHtmlImageClick,
        )
    }
}