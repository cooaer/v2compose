package io.github.v2compose.bean

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import io.github.v2compose.network.bean.TopicNode

@JsonClass(generateAdapter = true)
data class DraftTopic(
    val title: String = "",
    val content: String = "",
    val node: TopicNode? = null,
) {

    companion object {

        val Empty = DraftTopic()

        @OptIn(ExperimentalStdlibApi::class)
        fun fromJson(moshi: Moshi, json: String): DraftTopic {
            return moshi.adapter<DraftTopic>().fromJson(json) ?: Empty
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    fun toJson(moshi: Moshi): String {
        return moshi.adapter<DraftTopic>().toJson(this)
    }
}

