package io.github.v2compose.util

import java.util.regex.Pattern

object InetValidator {

    private const val hostOrIpRegex = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$|^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)+([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])\$"

    private val hostOrIpPattern: Pattern = Pattern.compile(hostOrIpRegex)

    fun isValidHostOrIp(hostOrIp:String):Boolean{
        val matcher = hostOrIpPattern.matcher(hostOrIp)
        return matcher.matches()
    }

    fun isValidInetPort(inetPort: Int): Boolean {
        return inetPort in 0..65535
    }
}