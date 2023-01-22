package io.github.v2compose.network.bean

import me.ghui.fruit.Attrs
import me.ghui.fruit.annotations.Pick

@Pick("div#Wrapper")
class UserTopics : BaseInfo() {
    @Pick("div.header strong.gray")
    val total: Int = -1

    @Pick("div.box div.cell.item")
    val items: List<Item> = listOf()

    @Pick("div.inner:last-child strong.fade")
    private val pageInfo: String = ""

    @Pick("div.cell .topic_content")
    val visibility: String = ""

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
        return "UserTopics(" +
                "total=$total, " +
                "items=$items, " +
                "pageInfo='$pageInfo', " +
                "currentPage=$currentPage, " +
                "pageCount=$pageCount" +
                ")"
    }


    class Item : java.io.Serializable {

        @Pick(value = "span.item_title a", attr = Attrs.HREF)
        val link: String = ""

        @Pick("strong > a[href^=/member/]:first-child")
        val userName: String = ""

        @Pick("span.item_title")
        val title: String = ""

        @Pick(value = "a.node", attr = Attrs.HREF)
        val nodeLink: String = ""

        @Pick("a.node")
        val nodeName: String = ""

        @Pick("span.small.fade:last-child")
        val lastReply: String = ""

        @Pick("a[class^=count_]")
        val repliesNum: Int = 0

        override fun toString(): String {
            return "Item(link='$link', userName='$userName', title='$title', nodeLink='$nodeLink', nodeName='$nodeName', lastReply='$lastReply', repliesNum=$repliesNum)"
        }

    }

}