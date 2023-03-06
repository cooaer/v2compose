package io.github.v2compose.ui.webview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.web.*
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.core.extension.castOrNull
import io.github.v2compose.network.NetConstants
import io.github.v2compose.ui.common.CloseButton
import io.github.v2compose.ui.webview.client.V2exWebViewClient

@Composable
fun WebViewScreenRoute(url: String, onCloseClick: () -> Unit, openUri: (String) -> Unit) {
    WebViewScreen(url = url, onCloseClick = onCloseClick, openUri = openUri)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebViewScreen(url: String, onCloseClick: () -> Unit, openUri: (String) -> Unit) {
    val webViewState = rememberSaveableWebViewState(
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
                        userAgentString = System.getProperty("http.agent")
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        allowUniversalAccessFromFileURLs = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                    }
                }, client = remember(openUri) { V2exWebViewClient(openUri) })
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
                text = pageTitle ?: stringResource(id = R.string.v2ex),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = { CloseButton(onCloseClick) },
    )
}

@Composable
private fun rememberSaveableWebViewState(
    url: String,
    additionalHttpHeaders: Map<String, String> = emptyMap()
): WebViewState =
// Rather than using .apply {} here we will recreate the state, this prevents
    // a recomposition loop when the webview updates the url itself.
    rememberSaveable(
        url,
        additionalHttpHeaders,
        saver = listSaver(
            save = { state ->
                state.content.castOrNull<WebContent.Url>()
                    ?.let { listOf(it.url, it.additionalHttpHeaders) } ?: listOf()
            },
            restore = {
                if(it.isEmpty()){
                    WebViewState(WebContent.Url(url, additionalHttpHeaders))
                }else{
                    WebViewState(WebContent.Url(it[0] as String, it[1] as Map<String, String>))
                }
            }
        )
    ) {
        WebViewState(
            WebContent.Url(
                url = url,
                additionalHttpHeaders = additionalHttpHeaders
            )
        )
    }