package io.github.v2compose.network.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Node(
    val id: Int = 0,
    val name: String = "",
    val title: String = "",
    val url: String = "",
    val topics: Int = 0,
    val stars: Int = 0,
    @Json(name = "avatar_large")
    val avatarLarge: String = "",
    @Json(name = "avatar_normal")
    val avatarNormal: String = "",
    @Json(name = "avatar_mini")
    val avatarMini: String = "",
    @Json(name = "title_alternative")
    val titleAlternative: String = "",
    @Json(name = "header")
    val header: String = "",
    @Json(name = "footer")
    val footer: String = "",
    val root: Boolean = false,
    @Json(name = "parent_node_name")
    val parentNodeName: String = "",
    val aliases: List<String> = listOf(),
) {
    val avatar: String
        get() = avatarLarge.ifEmpty { avatarNormal.ifEmpty { avatarMini } }
}
