package io.github.v2compose.core

interface StringDecoder {
    fun decodeString(encodedString: String): String
}