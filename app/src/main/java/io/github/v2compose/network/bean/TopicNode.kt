package io.github.v2compose.network.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopicNode(
    @SerializedName("name")
    val id: String = "",
    @SerializedName("title")
    val name: String = "",
    val topics: Int = 0,
    val aliases: List<String> = listOf(),
) : Parcelable