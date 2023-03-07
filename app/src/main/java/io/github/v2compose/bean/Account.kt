package io.github.v2compose.bean

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter

@JsonClass(generateAdapter = true)
data class Account(
    val userName: String = "",
    val userAvatar: String = "",
    val description: String = "",
    val nodes: Int = 0,
    val topics: Int = 0,
    val following: Int = 0,
) {

    companion object {
        val Empty = Account()

        @OptIn(ExperimentalStdlibApi::class)
        fun fromJson(moshi: Moshi, json: String): Account {
            return moshi.adapter<Account>().fromJson(json) ?: Empty
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun toJson(moshi: Moshi): String {
        return moshi.adapter<Account>().toJson(this)
    }

    fun isValid(): Boolean {
        return userName.isNotEmpty()
    }

}