package io.github.v2compose.ui.login.twostep

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

private const val twoStepLoginRoute = "/2fa"

fun NavController.navigateToTwoStepLogin() {
    navigate(twoStepLoginRoute)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.twoStepLoginScreen(
    onCloseClick: () -> Unit,
) {
    composable(
        twoStepLoginRoute,
    ) {
        TwoStepLoginScreenRoute(
            onCloseClick = onCloseClick,
        )
    }
}