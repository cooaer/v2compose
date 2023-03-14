package io.github.v2compose.util

import android.util.Log
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import kotlin.experimental.xor

object CfEmailUtils {
    private const val TAG = "CfEmailUtils"

    fun fixEmailProtected(ele: Element) {
        val encodedEmail = ele.attr("data-cfemail")
        try {
            val email = cfDecodeEmail(encodedEmail)
            if (email.isNotEmpty()) {
                val parent = ele.parent()
                val siblingIndex = ele.siblingIndex()
                if (parent != null) {
                    ele.remove()
                    parent.insertChildren(siblingIndex, TextNode(email))
                    if (parent.tagName().equals("a")) {
                        parent.attr("href", "mailto:$email")
                    }
                }
            }

            Log.d(
                TAG,
                "fixEmailProtected, encodedEmail = ${ele.outerHtml()}, decodedEmail = $email"
            )
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Log.d(TAG, "fixEmailProtected, encodedEmail = ${ele.outerHtml()}")
        }
    }

    private fun cfDecodeEmail(encodedString: String): String {
        val stringBuilder = StringBuilder()
        val bytes = encodedString.decodeHex()
        val r = bytes[0]
        for (index in 1 until bytes.size) {
            val byte = bytes[index] xor r
            stringBuilder.append(byte.toInt().toChar())
        }
        return stringBuilder.toString()
    }

    internal fun String.decodeHex(): ByteArray {
        require(length % 2 == 0) { "Unexpected hex string: $this" }

        val result = ByteArray(length / 2)
        for (i in result.indices) {
            val d1 = decodeHexDigit(this[i * 2]) shl 4
            val d2 = decodeHexDigit(this[i * 2 + 1])
            result[i] = (d1 + d2).toByte()
        }
        return result
    }

    private fun decodeHexDigit(c: Char): Int {
        return when (c) {
            in '0'..'9' -> c - '0'
            in 'a'..'f' -> c - 'a' + 10
            in 'A'..'F' -> c - 'A' + 10
            else -> throw IllegalArgumentException("Unexpected hex digit: $c")
        }
    }
}