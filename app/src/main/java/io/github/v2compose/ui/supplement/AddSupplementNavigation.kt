package io.github.v2compose.ui.supplement

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable

private const val argsTopicId = "topicId"
const val addSupplementNavigationRoute = "/append/topic/{$argsTopicId}"

class AddSupplementArgs(val topicId: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        checkNotNull(
            savedStateHandle.get<String>(argsTopicId)
        )
    )
}

fun NavController.navigateToAddSupplement(topicId: String) {
    navigate("/append/topic/$topicId")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addSupplementScreen(
    onCloseClick: () -> Unit,
    onAddSupplementSuccess: (String) -> Unit,
    openUri: (String) -> Unit,
) {
    composable(
        addSupplementNavigationRoute,
        arguments = listOf(navArgument(argsTopicId) { type = NavType.StringType })
    ) {
        AddSupplementScreenRoute(
            onCloseClick = onCloseClick,
            onAddSupplementSuccess = onAddSupplementSuccess,
            openUri = openUri
        )
    }
}

