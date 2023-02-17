package io.github.v2compose.ui.webview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.network.NetConstants
import io.github.v2compose.ui.common.CloseButton

@Composable
fun WebViewScreenRoute(url: String, onCloseClick: () -> Unit) {
    WebViewScreen(url = url, onCloseClick = onCloseClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebViewScreen(url: String, onCloseClick: () -> Unit) {
    val webViewState = rememberWebViewState(
        url = url,
        additionalHttpHeaders = mapOf("Refer" to Constants.baseUrl)
    )

    val loadingState = webViewState.loadingState
    val loadingProgress: Float = remember(loadingState) {
        when (loadingState) {
            is LoadingState.Initializing -> 0f
            is LoadingState.Loading -> loadingState.progress / 100f
            is LoadingState.Finished -> 1f
        }
    }

    Scaffold(topBar = {
        WebViewTopBar(webViewState.pageTitle, onCloseClick)
    }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize(),
                captureBackPresses = true,
                onCreated = {
                    it.settings.apply {
                        userAgentString = NetConstants.wapUserAgent
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        allowUniversalAccessFromFileURLs = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                    }
                })
            if (webViewState.isLoading) {
                LinearProgressIndicator(progress = loadingProgress)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun WebViewTopBar(
    pageTitle: String?,
    onCloseClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = pageTitle ?: stringResource(id = R.string.app_name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = { CloseButton(onCloseClick) },
    )
}