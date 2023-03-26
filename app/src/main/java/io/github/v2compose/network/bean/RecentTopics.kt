package io.github.v2compose.network.bean

import androidx.compose.runtime.Stable
import io.github.v2compose.util.AvatarUtils
import io.github.v2compose.util.UriUtils.getLastSegment
import me.ghui.fruit.annotations.Pick
import java.io.Serializable

@Pick("div#Wrapper")
class RecentTopics : BaseInfo() {
    @Pick("div.header span.fade")
    private val totalText: String = ""

    @Pick("div.box div.cell.item")
    val items: List<Item> = listOf()

    @Pick("div.inner:last-child strong.fade")
    private val pageInfo: String = ""

    private var _total: Int = -1
    val total: Int
        get() {
            if (_total < 0) {
                _total = totalText.split(" ").getOrNull(1)?.toIntOrNull() ?: -1
            }
            return _total
        }

    private var _currentPage: Int = -1
    val currentPage: Int
        get() {
            if (_currentPage < 0) {
                pageInfo.split("/").getOrNull(0)?.toIntOrNull()?.let { _currentPage = it }
            }
            return _currentPage
        }

    private var _pageCount: Int = -1
    val pageCount: Int
        get() {
            if (_pageCount < 0) {
                pageInfo.split("/").getOrNull(1)?.toIntOrNull()?.let { _pageCount = it }
            }
            return _pageCount
        }

    override fun isValid() = total >= 0

    override fun toString(): String {
        return "RecentTopics(totalText='$totalText', items=$items, pageInfo='$pageInfo', total=$total, currentPage=$currentPage, pageCount=$pageCount)"
    }

    @Stable
    class Item : Serializable {

        @Pick(value = "span.item_title > a")
        val title: String = ""

        @Pick(value = "span.item_title > a", attr = "href")
        private val linkPath: String = ""

        @Pick(value = "td > a > img", attr = "src")
        private val avatarUrl: String = ""

        @Pick(value = "span.small.fade > strong > a")
        val userName: String = ""

        @Pick(value = "span.small.fade:last-child", attr = "ownText")
        private var timeText: String = ""

        @Pick(value = "span.small.fade > a")
        val nodeTitle: String = ""

        @Pick(value = "span.small.fade > a", attr = "href")
        private val nodeLink: String = ""

        @Pick("a[class^=count_]")
        val replies = 0

        private var _id: String = ""
        val id: String
            get() {
                if (_id.isEmpty()) _id = getLastSegment(linkPath)
                return _id
            }

        private var _avatar: String = ""
        val avatar: String
            get() {
                if (_avatar.isEmpty()) _avatar = AvatarUtils.adjustAvatar(avatarUrl)
                return _avatar
            }

        private var _time: String = ""
        val time: String
            get() {
                if (_time.isEmpty() && timeText.contains("•")) {
                    _time = timeText.split("•").first()
                }
                return _time
            }

        private var _nodeName: String = ""
        val nodeName: String
            get() {
                if (_nodeName.isEmpty()) {
                    _nodeName = nodeLink.substring(nodeLink.lastIndexOf("/") + 1)
                }
                return _nodeName
            }

        override fun toString(): String {
            return "Item(title='$title', linkPath='$linkPath', avatarUrl='$avatarUrl', userName='$userName', timeText='$timeText', nodeTitle='$nodeTitle', nodeLink='$nodeLink', replies=$replies, id='$id', avatar='$avatar', time='$time', nodeName='$nodeName')"
        }

    }



}