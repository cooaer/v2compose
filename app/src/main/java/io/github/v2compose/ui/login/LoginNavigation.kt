package io.github.v2compose.ui.login

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable

private const val argsNext = "next"
const val loginNavigationRoute = "/signin?next={$argsNext}"

fun NavController.navigateToLogin(
    next: String? = null,
    navOptions: NavOptions? = null,
) {
    val encodedNext = Uri.encode(next) ?: ""
    navigate("/signin?next=$encodedNext", navOptions = navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.loginScreen(
    onCloseClick: () -> Unit,
    onSignInWithGoogleClick: (String) -> Unit,
) {
    composable(
        route = loginNavigationRoute,
        arguments = listOf(navArgument(argsNext) {
            type = NavType.StringType
            nullable = true
        })
    ) {
        val redirect = it.arguments?.getString(argsNext)
        LoginScreenRoute(
            onCloseClick = onCloseClick,
            onSignInWithGoogleClick = onSignInWithGoogleClick,
            redirect = redirect,
        )
    }
}