package io.github.v2compose.ui.webview

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable

private const val argsUrl = "url"
private const val webViewRoute = "/webview?$argsUrl={$argsUrl}"

fun NavController.navigateToWebView(url: String) {
    val encodeUrl = Uri.encode(url)
    navigate("/webview?url=$encodeUrl")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.webViewScreen(onCloseClick: () -> Unit) {
    composable(
        webViewRoute,
        arguments = listOf(navArgument(argsUrl) { type = NavType.StringType })
    ) {
        val url = Uri.decode(it.arguments?.getString(argsUrl)) ?: ""
        WebViewScreenRoute(url = url, onCloseClick = onCloseClick)
    }
}