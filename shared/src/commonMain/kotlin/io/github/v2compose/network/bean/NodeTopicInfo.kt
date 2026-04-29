package io.github.v2compose.network.bean

import androidx.compose.runtime.Immutable
import io.github.fruit.annotations.Attrs
import io.github.fruit.annotations.Pick
import io.github.fruit.annotations.Slice

/**
 * https://www.v2ex.com/go/python
 */
@Immutable
@Slice("div#Wrapper")
data class NodeTopicInfo(
    @property:Pick("span.topic-count strong")
    val totalText: String = "",
    @property:Pick(value = "a[href*=favorite/] ", attr = Attrs.HREF)
    val favoriteLink: String = "",
    @property:Pick("div.box:nth-child(2) div.cell:has(table)")
    val items: List<Item> = emptyList(),
) {
    fun total(): Int = totalText.replace(",", "").toIntOrNull() ?: 0

    val fullFavoriteLink: String
        get() = "https://www.v2ex.com$favoriteLink"

    val hasStared: Boolean
        get() = favoriteLink.contains("/unfavorite/node/")

    val once: String?
        get() = Regex("once=([^&]+)").find(favoriteLink)?.groupValues?.getOrNull(1)

    fun isValid(): Boolean = items.isEmpty() || items[0].userName.isNotEmpty()

    @Immutable
    @Slice
    data class Item(
        @property:Pick(value = "img.avatar", attr = Attrs.SRC)
        val avatar: String = "",
        @property:Pick("span.item_title")
        val title: String = "",
        @property:Pick("span.small.fade strong")
        val userName: String = "",
        @property:Pick(value = "span.small.fade", attr = Attrs.OWN_TEXT)
        val clickedAndContentLength: String = "",
        @property:Pick("a[class^=count_]")
        val commentNum: Int = 0,
        @property:Pick(value = "span.item_title a", attr = Attrs.HREF)
        val topicLinkText: String = "",
    ) {
        val topicId: String
            get() {
                val end = topicLinkText.indexOf('#')
                return if (end > 3) topicLinkText.substring(3, end) else ""
            }

        val topicLink: String
            get() = topicLinkText

        val avatarUrl: String
            get() = if (avatar.startsWith("http")) avatar else "https:$avatar"

        val clickNum: Int
            get() {
                return try {
                    clickedAndContentLength.substringAfterLast("•")
                        .replace("[^0-9]".toRegex(), "")
                        .toInt()
                } catch (e: Exception) {
                    0
                }
            }

        val contentLength: Int
            get() {
                return try {
                    val result = clickedAndContentLength.trim().substringBeforeLast("•").trim()
                    result.split(" ")[1].trim().toInt()
                } catch (e: Exception) {
                    0
                }
            }
    }
}
