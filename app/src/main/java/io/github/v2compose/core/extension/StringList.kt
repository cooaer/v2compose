package io.github.v2compose.core.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


fun List<String>.toJson(moshi: Moshi): String {
    val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    val adapter: JsonAdapter<List<String>> = moshi.adapter(stringListType)
    return adapter.toJson(this)
}

fun String.toStringList(moshi: Moshi): List<String>? {
    val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    val adapter: JsonAdapter<List<String>> = moshi.adapter(stringListType)
    return adapter.fromJson(this)
}