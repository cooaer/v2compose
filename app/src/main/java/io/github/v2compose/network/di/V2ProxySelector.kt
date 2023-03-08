package io.github.v2compose.network.di

import android.text.TextUtils
import io.github.v2compose.bean.ProxyInfo
import io.github.v2compose.bean.ProxyType
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.util.InetValidator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class V2ProxySelector @Inject constructor(appPreferences: AppPreferences) : ProxySelector() {

    private lateinit var proxyInfo: ProxyInfo
    private lateinit var delegation: ProxySelector

    init {
        val localProxyInfo = runBlocking { appPreferences.proxyInfo.first() }
        updateProxy(localProxyInfo)
    }

    fun updateProxy(value: ProxyInfo) {
        proxyInfo = value
        delegation = when (proxyInfo.type) {
            ProxyType.System -> getDefault() ?: NullProxySelector()
            else -> NullProxySelector()
        }
    }

    override fun select(uri: URI): List<Proxy> {
        val type = proxyInfo.type
        if (type == ProxyType.Http || type == ProxyType.Socks) {
            try {
                val ip: String = proxyInfo.address
                val port: Int = proxyInfo.port
                if (!TextUtils.isEmpty(ip) && InetValidator.isValidInetPort(port)) {
                    val inetAddress = InetAddress.getByName(ip)
                    val socketAddress: SocketAddress = InetSocketAddress(inetAddress, port)
                    return listOf(
                        Proxy(
                            if (type == ProxyType.Http) Proxy.Type.HTTP else Proxy.Type.SOCKS,
                            socketAddress
                        )
                    )
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return delegation.select(uri)
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
        ioe?.printStackTrace()
    }

}

class NullProxySelector : ProxySelector() {
    override fun select(uri: URI?): List<Proxy> {
        requireNotNull(uri) { "uri must not be null" }
        return listOf(Proxy.NO_PROXY)
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
    }
}