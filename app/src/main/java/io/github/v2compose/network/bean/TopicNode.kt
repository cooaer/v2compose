package io.github.v2compose.network.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class TopicNode(
    val name: String = "",
    val title: String = "",
    val topics: Int = 0,
    val aliases: List<String> = listOf(),
) : Parcelable