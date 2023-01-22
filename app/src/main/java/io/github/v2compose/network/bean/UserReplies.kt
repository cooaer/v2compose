package io.github.v2compose.network.bean

import me.ghui.fruit.Attrs
import me.ghui.fruit.annotations.Pick
import java.io.Serializable

@Pick("div#Wrapper")
class UserReplies : BaseInfo() {

    @Pick("div.header strong.gray")
    val total: Int = -1

    @Pick("div.box:last-child > div.dock_area")
    private val dockItems: List<ReplyDockItem> = listOf()

    @Pick("div.box:last-child div.reply_content")
    private val replyContentItems: List<ReplyContentItem> = listOf()

    val items: List<Item>
        get() = dockItems.zip(replyContentItems) { dock, content -> Item(dock, content) }

    @Pick("div.inner:last-child strong.fade")
    private val pageInfo: String = ""

    val currentPage: Int
        get() {
            return pageInfo.split("/").getOrNull(0)?.toIntOrNull() ?: -1
        }

    val pageCount: Int
        get() {
            return pageInfo.split("/").getOrNull(1)?.toIntOrNull() ?: -1
        }

    override fun isValid(): Boolean {
        return total >= 0
    }

    override fun toString(): String {
        return "UserReplies(" +
                "total=$total, " +
                "dockItems=$dockItems, " +
                "replyContentItems=$replyContentItems, " +
                "items=$items, " +
                "pageInfo='$pageInfo', " +
                "currentPage=$currentPage, " +
                "pageCount=$pageCount" +
                ")"
    }


    class ReplyDockItem : Serializable {
        @Pick("span.gray")
        val title: String = ""

        @Pick(value = "span.gray > a", attr = Attrs.HREF)
        val link: String = ""

        @Pick("span.fade")
        val time: String = ""

        override fun toString(): String {
            return "ReplyDockItem{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", time='" + time + '\'' +
                    '}'
        }
    }

    class ReplyContentItem : Serializable {
        @Pick(attr = Attrs.INNER_HTML)
        val content: String = ""

        override fun toString(): String {
            return "ReplyContentItem{" +
                    "content='" + content + '\'' +
                    '}'
        }
    }

    data class Item(val dock: ReplyDockItem, val content: ReplyContentItem)

}