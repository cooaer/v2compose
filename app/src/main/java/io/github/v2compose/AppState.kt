package io.github.v2compose

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import io.github.v2compose.core.extension.fullUrl
import io.github.v2compose.core.extension.tryParse
import io.github.v2compose.core.openInBrowser
import io.github.v2compose.ui.node.navigateToNode
import io.github.v2compose.ui.topic.navigateToTopic
import javax.inject.Inject

private const val TAG = "AppState"

@Composable
fun rememberAppState(
    navHostController: NavHostController,
    context: Context = LocalContext.current,
): AppState {
    return remember(navHostController, context) {
        AppState(context, navHostController)
    }
}

class AppState @Inject constructor(
    private val context: Context,
    private val navHostController: NavHostController,
) {

    fun openUri(uri: String) {
        if (!innerOpenUri(uri)) {
            context.openInBrowser(uri)
        }
    }

    private fun innerOpenUri(uri: String): Boolean {
        var path: String? = ""
        if (uri.startsWith("/")) {
            path = uri
        } else if (uri.startsWith("//") || uri.startsWith("http://") || uri.startsWith("https://")) {
            val uriObj = uri.fullUrl(baseUrl = Constants.baseUrl).tryParse() ?: return false
            path = uriObj.path
        }
        if (path.isNullOrEmpty()) {
            Log.e(TAG, "can't parse uri, uri = $uri")
            return false
        }
        val pathParts = path.split('/')
        val screenType = pathParts.getOrNull(1) ?: return false
        val screenId = pathParts.getOrNull(2) ?: return false
        when (screenType) {
            "t" -> {
                navHostController.navigateToTopic(screenId)
                return true
            }
            "go" -> {
                navHostController.navigateToNode(screenId)
                return true
            }
            "member" -> {

            }
        }
        return false
    }

}









