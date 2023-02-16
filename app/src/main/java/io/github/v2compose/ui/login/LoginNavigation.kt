package io.github.v2compose.ui.login

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.network.bean.DailyInfo

private const val argsNext = "next"
private const val loginRoute = "/signin?next={$argsNext}"

fun NavController.navigateToLogin(next: String? = null) {
    val encodedNext = Uri.encode(next) ?: ""
    navigate("/signin?next=$encodedNext")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.loginScreen(
    onCloseClick: () -> Unit,
    onSignInWithGoogleClick: (String) -> Unit,
) {
    composable(
        route = loginRoute,
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