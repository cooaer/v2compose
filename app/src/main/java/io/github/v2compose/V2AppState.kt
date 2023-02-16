package io.github.v2compose

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import io.github.v2compose.bean.RedirectEvent
import io.github.v2compose.core.extension.tryParse
import io.github.v2compose.core.openInBrowser
import io.github.v2compose.ui.main.mainNavigationRoute
import io.github.v2compose.ui.node.navigateToNode
import io.github.v2compose.ui.topic.navigateToTopic
import io.github.v2compose.ui.user.navigateToUser
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

private const val TAG = "AppState"

@Composable
fun rememberV2AppState(
    navHostController: NavHostController,
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): V2AppState {
    val v2AppState = remember(navHostController, context, snackbarHostState) {
        V2AppState(context, navHostController, snackbarHostState)
    }
    DisposableEffect(lifecycleOwner, v2AppState) {
        lifecycleOwner.lifecycle.addObserver(v2AppState)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(v2AppState)
        }
    }
    return v2AppState
}


class V2AppState @Inject constructor(
    private val context: Context,
    private val navHostController: NavHostController,
    val snackbarHostState: SnackbarHostState,
) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        EventBus.getDefault().register(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        EventBus.getDefault().unregister(this)
    }

    fun back() {
        Log.d(TAG, "back, currentDestination = ${navHostController.currentDestination}")
        //修复连续点击返回键出现返回到空白页面的问题
        if (navHostController.currentDestination?.route != mainNavigationRoute) {
            navHostController.popBackStack()
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedirectEvent(event: RedirectEvent) {
        Log.d(TAG, "onRedirectEvent, location = ${event.location}")
        val uri = Uri.parse(event.location)
        val firstPathSegment = uri.lastPathSegment?.firstOrNull() ?: ""
        // ege : /
        navHostController.navigate(event.location) {
            if (firstPathSegment == "") {
                popUpTo(mainNavigationRoute) {
                    inclusive = true
                }
            }
        }
    }

}





