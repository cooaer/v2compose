package io.github.v2compose

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import io.github.v2compose.core.extension.tryParse
import io.github.v2compose.core.openInBrowser
import io.github.v2compose.ui.user.navigateToUser
import io.github.v2compose.ui.node.navigateToNode
import io.github.v2compose.ui.topic.navigateToTopic
import javax.inject.Inject

private const val TAG = "AppState"

@Composable
fun rememberV2AppState(
    navHostController: NavHostController,
    context: Context = LocalContext.current,
): V2AppState {
    return remember(navHostController, context) {
        V2AppState(context, navHostController)
    }
}

class V2AppState @Inject constructor(
    private val context: Context,
    private val navHostController: NavHostController,
) {

    fun back() {
        navHostController.popBackStack()
    }

    fun openUri(uri: String, inExternalBrowser: Boolean = false) {
        Log.d(TAG, "openUri, uri = $uri")
        if (!innerOpenUri(uri)) {
            context.openInBrowser(uri, inExternalBrowser)
        }
    }

    private fun innerOpenUri(uri: String): Boolean {
        if (uri.isEmpty()) {
            Log.e(TAG, "can't parse uri, uri = $uri")
            return false
        }
        val uriObj = uri.tryParse() ?: return false
        val host = uriObj.host
        if (!host.isNullOrEmpty() && !host.endsWith(Constants.host)) {
            return false
        }
        val screenType = uriObj.pathSegments.getOrNull(0) ?: return false
        val screenId = uriObj.pathSegments.getOrNull(1) ?: return false
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
                navHostController.navigateToUser(userName = screenId)
                return true
            }
        }
        return false
    }

}









