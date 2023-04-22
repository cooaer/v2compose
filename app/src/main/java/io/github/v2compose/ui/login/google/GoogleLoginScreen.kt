package io.github.v2compose.ui.login.google

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.ui.common.CloseButton

private const val TAG = "GoogleLogin"
private const val googleLoginUrlRefer = "${Constants.baseUrl}/signin?next=/mission/daily"

@Composable
fun GoogleLoginScreenRoute(
    once: String,
    onCloseClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: GoogleLoginViewModel = hiltViewModel()
) {
    val account by viewModel.account.collectAsStateWithLifecycle()
    if (account.isValid()) {
        LaunchedEffect(true) {
            onLoginSuccess()
        }
    }

    val googleLoginUrl = remember(once) { "${Constants.baseUrl}/auth/google?once=$once" }
    GoogleLoginScreen(
        loginUrl = googleLoginUrl,
        onCloseClick = onCloseClick,
        tryToFetchUserInfo = viewModel::fetchUserInfo,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoogleLoginScreen(
    loginUrl: String,
    onCloseClick: () -> Unit,
    tryToFetchUserInfo: suspend () -> Unit,
) {
    val webViewState = rememberWebViewState(
        url = loginUrl,
        additionalHttpHeaders = mapOf("Refer" to googleLoginUrlRefer)
    )

    val loadingState = webViewState.loadingState
    val loadingProgress: Float = remember(loadingState) {
        when (loadingState) {
            is LoadingState.Initializing -> 0f
            is LoadingState.Loading -> loadingState.progress / 100f
            is LoadingState.Finished -> 1f
        }
    }

    val fetchUserInfo by rememberUpdatedState(tryToFetchUserInfo)
    webViewState.lastLoadedUrl?.let {
        Log.d(TAG, "currentUrl = $it")
        if (it.startsWith("${Constants.baseUrl}/auth/google")) {
            return@let
        }
        if (it.startsWith(Constants.baseUrl)) {
            LaunchedEffect(true) {
                fetchUserInfo()
            }
        }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(id = R.string.sign_in_with_google)) },
            navigationIcon = { CloseButton(onCloseClick) })
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
                })
            if (webViewState.isLoading) {
                LinearProgressIndicator(progress = loadingProgress)
            }
        }
    }
}