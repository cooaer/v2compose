package io.github.v2compose.ui.topic

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.v2compose.core.StringDecoder

const val argTopicId: String = "topicId"

const val topicNavigationRoute = "topic/{$argTopicId}"

class TopicArgs(val topicId: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        stringDecoder.decodeString(
            checkNotNull(savedStateHandle[argTopicId])
        )
    )
}

fun NavController.navigateToTopic(topicId: String) {
    val encodedTopicId = Uri.encode(topicId)
    navigate("topic/$topicId")
}

fun NavGraphBuilder.topicScreen(onBackClick: () -> Unit) {
    composable(
        topicNavigationRoute,
        arguments = listOf(navArgument(argTopicId) { type = NavType.StringType })
    ) {
        TopicRoute(onBackClick = onBackClick)
    }
}