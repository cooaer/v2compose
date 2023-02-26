package io.github.v2compose.ui.webview.client

import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.google.accompanist.web.AccompanistWebViewClient

class V2exWebViewClient(private val openUri: (String) -> Unit) : AccompanistWebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if(interceptUrl(request)) return true
        return super.shouldOverrideUrlLoading(view, request)
    }

    private fun interceptUrl(request: WebResourceRequest?): Boolean {
        request?.url?.pathSegments?.firstOrNull()?.let {
            if (listOf("t", "go", "member").contains(it.lowercase())) {
                openUri(request.url.toString())
                return true
            }
        }
        return false
    }

}