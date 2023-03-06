package io.github.v2compose

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import io.github.v2compose.bean.RedirectEvent
import io.github.v2compose.core.extension.fullUrl
import io.github.v2compose.core.extension.tryParse
import io.github.v2compose.core.openInBrowser
import io.github.v2compose.ui.BaseScreenState
import io.github.v2compose.ui.main.mainNavigationRoute
import io.github.v2compose.ui.node.navigateToNode
import io.github.v2compose.ui.topic.navigateToTopic
import io.github.v2compose.ui.user.navigateToUser
import io.github.v2compose.ui.webview.navigateToWebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import javax.inject.Inject

private const val TAG = "AppState"

@Composable
fun rememberV2AppState(
    navHostController: NavHostController,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): V2AppState {
    val v2AppState = remember(navHostController, context, coroutineScope, snackbarHostState) {
        V2AppState(context, navHostController, coroutineScope, snackbarHostState)
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
    context: Context,
    private val navHostController: NavHostController,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) : BaseScreenState(context, coroutineScope, snackbarHostState), DefaultLifecycleObserver {

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

    fun openUri(uri: String) {
        Log.d(TAG, "openUri, uri = $uri")
        if (!navHostController.innerOpenUri(uri)) {
            context.openInBrowser(uri, true)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedirectEvent(event: RedirectEvent) {
        Log.d(TAG, "onRedirectEvent, location = ${event.location}")
        val uri = Uri.parse(event.location)
        val screenType = uri.pathSegments?.getOrNull(0) ?: ""
        when (screenType) {
            "", "signin", "2fa" -> navHostController.navigate(event.location) {
                if (screenType == "") {
                    popUpTo(mainNavigationRoute) {
                        inclusive = true
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    fun saveImage(url: String) = coroutineScope.launch {
        val imageName = Uri.parse(url).lastPathSegment ?: return@launch
        val pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appImageDir = File(pictureDir, "v2compose").also {
            it.mkdirs()
        }
        context.imageLoader.diskCache?.get(url)?.let { snapshot ->
            val newFile = File(appImageDir, imageName)
            snapshot.data.toFile().copyTo(newFile, overwrite = true)
            snapshot.close()
            showMessage(R.string.save_image_success)
            return@launch
        }
        showMessage(R.string.save_image_failed)
        return@launch
    }

}

fun NavController.innerOpenUri(uri: String): Boolean {
    if (uri.isEmpty()) {
        Log.e(TAG, "can't parse uri, uri = $uri")
        return false
    }
    val uriObj = uri.tryParse() ?: return false
    val host = uriObj.host
    if (!host.isNullOrEmpty() && !host.endsWith(Constants.host)) {
        return false
    }
    val screenType = uriObj.pathSegments.getOrNull(0) ?: ""
    val screenId = uriObj.pathSegments.getOrNull(1) ?: ""
    when (screenType) {
        "t" -> navigateToTopic(screenId)
        "go" -> navigateToNode(screenId)
        "member" -> navigateToUser(userName = screenId)
        else -> navigateToWebView(uri.fullUrl(Constants.baseUrl))
    }
    return true
}


