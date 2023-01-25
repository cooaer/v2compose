package io.github.v2compose.ui.main.user

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.core.StringDecoder

private const val argsUserName = "userName"
private const val argsAvatar = "userAvatar"

private const val userScreenRoute = "/member/{$argsUserName}?userAvatar={$argsAvatar}"

data class UserArgs(val userName: String, val avatar: String? = null) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        stringDecoder.decodeString(checkNotNull(savedStateHandle[argsUserName])),
        savedStateHandle.get<String>(argsAvatar)?.let { stringDecoder.decodeString(it) },
    )
}

fun NavController.navigateToUser(userName: String, userAvatar: String? = null) {
    val encodedUserName = Uri.encode(userName)
    val encodedUserAvatar = Uri.encode(userAvatar) ?: ""
    navigate("/member/$encodedUserName?userAvatar=$encodedUserAvatar")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.userScreen(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onNodeClick: (String, String) -> Unit,
    openUri: (String) -> Unit,
) {
    composable(
        route = userScreenRoute,
        arguments = listOf(
            navArgument(argsUserName) { type = NavType.StringType },
            navArgument(argsAvatar) {
                type = NavType.StringType
                nullable = true
            },
        )
    ) {
        UserScreenRoute(
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            onNodeClick = onNodeClick,
            openUri = openUri,
        )
    }
}