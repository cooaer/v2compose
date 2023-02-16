package io.github.v2compose.ui.login.google

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable

private const val argsOnce = "once"
private const val googleLoginRoute = "/login/google?$argsOnce={$argsOnce}"

fun NavController.navigateToGoogleLogin(once: String) {
    navigate("/login/google?$argsOnce=$once")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.googleLoginScreen(onCloseClick: () -> Unit, onLoginSuccess: () -> Unit) {
    composable(
        googleLoginRoute,
        arguments = listOf(navArgument(argsOnce) { type = NavType.StringType })
    ) {
        val once = it.arguments?.getString(argsOnce) ?: ""
        GoogleLoginScreenRoute(
            once = once,
            onCloseClick = onCloseClick,
            onLoginSuccess = onLoginSuccess,
        )
    }
}
