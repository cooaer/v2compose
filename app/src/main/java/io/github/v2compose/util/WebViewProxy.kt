package io.github.v2compose.util

import android.util.Log
import androidx.webkit.ProxyConfig
import androidx.webkit.ProxyController
import androidx.webkit.WebViewFeature
import io.github.v2compose.bean.ProxyInfo
import io.github.v2compose.bean.ProxyType
import java.util.concurrent.ExecutorService

private const val TAG = "WebViewProxy"

object WebViewProxy {

    fun updateProxy(proxy: ProxyInfo, executorService: ExecutorService) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            if (proxy.type == ProxyType.Http || proxy.type == ProxyType.Socks) {
                //https_proxy=http://127.0.0.1:7890 http_proxy=http://127.0.0.1:7890 all_proxy=socks5://127.0.0.1:7890
                val address = "${proxy.address}:${proxy.port}"
                val proxyConfig = ProxyConfig.Builder().apply {
                    if (proxy.type == ProxyType.Http) {
                        addProxyRule("http://$address")
                    } else {
                        addProxyRule("socks://$address")
                    }
                }.build()
                ProxyController.getInstance().setProxyOverride(proxyConfig, executorService) {
                    Log.d(TAG, "proxy has set, $proxy")
                }
            } else {
                ProxyController.getInstance().clearProxyOverride(executorService) {
                    Log.d(TAG, "proxy has cleared, $proxy")
                }
            }
        }
    }
}