package io.github.v2compose.ui.webview

import com.multiplatform.webview.setting.WebSettings

internal fun WebSettings.applyBaseV2WebSettings() {
    isJavaScriptEnabled = true
    // allowUniversalAccessFromFileURLs only takes effect when the main
    // frame is a file:// URL. WebViewScreen and GoogleLoginScreen are
    // both pointed at https URLs (v2ex pages, Google OAuth), so the
    // flag is not load-bearing here and leaving it on is a CWE-200
    // sandbox-escape vector if a file:// load ever lands in one of
    // these WebViews.
    supportZoom = true
    androidWebSettings.domStorageEnabled = true
    androidWebSettings.useWideViewPort = true
}

internal fun WebSettings.applyGoogleLoginWebSettings() {
    applyBaseV2WebSettings()
    customUserAgentString = googleLoginUserAgent()
}

internal expect fun googleLoginUserAgent(): String?
