package io.github.v2compose.network.bean

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 节点详情
 * https://www.v2ex.com/api/nodes/show.json?name=qna
 */
@Immutable
@Serializable
data class NodeInfo(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("topics")
    val topics: Int = 0,
    @SerialName("stars")
    val stars: Int = 0,
    @SerialName("header")
    val header: String = "",
    @SerialName("created")
    val created: Long = 0,
    @SerialName("avatar_large")
    val avatar: String = "",
) {
    fun isValid(): Boolean = name.isNotEmpty()
}
