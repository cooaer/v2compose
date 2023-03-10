package io.github.v2compose.ui.login.twostep

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

const val twoStepLoginNavigationRoute = "/2fa"

fun NavController.navigateToTwoStepLogin() {
    navigate(twoStepLoginNavigationRoute)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.twoStepLoginScreen(
    onCloseClick: () -> Unit,
) {
    composable(
        twoStepLoginNavigationRoute,
    ) {
        TwoStepLoginScreenRoute(
            onCloseClick = onCloseClick,
        )
    }
}