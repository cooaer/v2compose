package io.github.v2compose.bean

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter

@JsonClass(generateAdapter = true)
data class ProxyInfo(
    val type: ProxyType = ProxyType.Direct,
    val address: String = "",
    val port: Int = 0,
) {
    companion object {
        val Default = ProxyInfo()

        @OptIn(ExperimentalStdlibApi::class)
        fun fromJson(moshi: Moshi, json: String): ProxyInfo {
            return moshi.adapter<ProxyInfo>().fromJson(json) ?: Default
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun toJson(moshi: Moshi): String {
        return moshi.adapter<ProxyInfo>().toJson(this)
    }
}

enum class ProxyType { System, Direct, Http, Socks }