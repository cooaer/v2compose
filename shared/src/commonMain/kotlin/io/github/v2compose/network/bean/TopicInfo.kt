package io.github.v2compose.network.bean

import androidx.compose.runtime.Immutable
import io.github.fruit.annotations.Attrs
import io.github.fruit.annotations.Pick
import io.github.fruit.annotations.Slice

@Immutable
@Slice
data class TopicInfo(
    @property:Pick("div#Wrapper")
    val headerInfo: HeaderInfo? = null,
    @property:Pick("div.content div.box")
    val contentInfo: ContentInfo? = null,
    @property:Pick("div.problem")
    val problem: Problem? = null,
    @property:Pick("div[id^=r_]")
    val replies: List<Reply> = emptyList(),
    @property:Pick(value = "input[name=once]", attr = "value")
    val once: String = "",
    @property:Pick(value = "meta[property=og:url]", attr = "content")
    val topicLink: String = "",
    @property:Pick(value = "a[onclick*=/report/topic/]", attr = "onclick")
    val reportLink: String = "",
    @property:Pick(value = "div#Wrapper div.box div.inner span.fade")
    val hasRePortStr: String = "",
    @property:Pick(value = "a[onclick*=/fade/topic/]", attr = "onclick")
    val fadeStr: String = "",
    @property:Pick(value = "a[onclick*=/sticky/topic/]", attr = "onclick")
    val stickyStr: String = "",
) {
    fun canSticky(): Boolean = stickyUrl().isNotEmpty()

    fun canFade(): Boolean = fadeUrl().isNotEmpty()

    fun hasReported(): Boolean {
        return hasRePortStr.isNotEmpty() && hasRePortStr.contains("已对本主题进行了报告")
    }

    fun hasReportPermission(): Boolean = hasReported() || reportLink.isNotEmpty()

    fun fadeUrl(): String {
        if (fadeStr.isEmpty()) return ""
        val start = fadeStr.indexOf("/fade/topic/")
        val end = fadeStr.lastIndexOf("'")
        return if (start >= 0 && end > start) fadeStr.substring(start, end) else ""
    }

    fun stickyUrl(): String {
        if (stickyStr.isEmpty()) return ""
        val start = stickyStr.indexOf("/sticky/topic/")
        val end = stickyStr.lastIndexOf("'")
        return if (start >= 0 && end > start) stickyStr.substring(start, end) else ""
    }

    fun totalPage(): Int = headerInfo?.getTotalPage() ?: 0

    fun toReplyMap(content: String): Map<String, String> {
        return mapOf("once" to once, "content" to content)
    }

    fun isValid(): Boolean = headerInfo?.isValid() == true

    @Immutable
    @Slice
    data class Problem(
        @property:Pick(attr = Attrs.OWN_TEXT)
        val title: String = "",
        @property:Pick("ul li")
        val tips: List<String> = emptyList(),
    ) {
        fun isEmpty(): Boolean = tips.isEmpty() && title.isEmpty()
    }

    @Immutable
    @Slice
    data class ContentInfo(
        @property:Pick(attr = Attrs.HTML)
        val html: String = "",
        @property:Pick(value = "div.cell div.topic_content", attr = Attrs.HTML)
        val content: String = "",
        @property:Pick("div.subtle")
        val supplements: List<Supplement> = emptyList(),
    ) {
        fun formattedHtml(): String? {
            if (html.isEmpty()) return null
            val cleaned = html
                .replace(Regex("""(?s)<div[^>]*class="header"[^>]*>.*?</div>"""), "")
                .replace(Regex("""(?s)<div[^>]*class="inner"[^>]*>.*?</div>"""), "")
                .trim()
            val textOnly = cleaned.replace(Regex("<[^>]*>"), "").trim()
            val hasVideo = cleaned.contains("embedded_video_wrapper")
            return if (textOnly.isEmpty() && !hasVideo) null else cleaned
        }

        fun isValid(): Boolean = !formattedHtml().isNullOrEmpty()

        @Immutable
        @Slice
        data class Supplement(
            @property:Pick("span.fade")
            val title: String = "",
            @property:Pick(value = "div.topic_content", attr = Attrs.HTML)
            val content: String = "",
        )
    }

    @Immutable
    @Slice
    data class HeaderInfo(
        @property:Pick(value = "div.box img.avatar", attr = "src")
        val avatar: String = "",
        @property:Pick("div.box small.gray a")
        val userName: String = "",
        @property:Pick(value = "div.box small.gray", attr = "ownText")
        val timeText: String = "",
        @property:Pick("div.box a[href^=/go]")
        val tag: String = "",
        @property:Pick(value = "div.box a[href^=/go]", attr = Attrs.HREF)
        val tagLink: String = "",
        @property:Pick("div.cell span.gray:contains(回复)")
        val comment: String = "",
        @property:Pick("div.box div.inner a.page_normal:last-of-type")
        val page: Int = 0,
        @property:Pick("div.box div.inner span.page_current")
        val currentPage: Int = 0,
        @property:Pick("div.box h1")
        val title: String = "",
        @property:Pick(value = "div.content div.box:first-child div.inner span:first-child")
        val favoriteText: String = "",
        @property:Pick(value = "div.box a[href*=favorite/]", attr = Attrs.HREF)
        val favoriteLink: String = "",
        @property:Pick(value = "div.box a[onclick*=ignore/]", attr = "onclick")
        val ignoreLink: String = "",
        @property:Pick("div.box div[id=topic_thank]")
        val thankedText: String = "",
        @property:Pick("div.box div.inner div#topic_thank")
        val canSendThanksText: String = "",
        @property:Pick("div.box div.header a.op")
        val appendTxt: String = "",
    ) {
        fun isValid(): Boolean = userName.isNotEmpty() && tag.isNotEmpty()

        fun canAppend(): Boolean = appendTxt == "APPEND"

        fun canSendThanks(): Boolean = canSendThanksText.isNotEmpty()

        fun hadThanked(): Boolean = thankedText.contains("已发送")

        fun hadFavorited(): Boolean = favoriteLink.contains("unfavorite/")

        fun hadIgnored(): Boolean = ignoreLink.contains("unignore/")

        fun getFavoriteCount(): Int {
            return favoriteText.trim().split(" ").getOrNull(0)?.toIntOrNull() ?: 0
        }

        fun getCommentNum(): String = if (comment.isEmpty()) "" else comment.split(" ")[0]

        fun getTagName(): String = tagLink.replace("/go/", "")

        fun getTime(): String {
            return try {
                if (timeText.isNotEmpty() && timeText.contains("·")) {
                    var temp = timeText.split("·")[0].trim().substring(6).replace(" ", "").trim()
                    if (temp.contains("-") && temp.contains("+")) {
                        temp = temp.substring(0, 10)
                    }
                    temp
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }

        fun getViewCount(): Int {
            return try {
                val countStr = timeText.split("·")[1].trim()
                countStr.substring(0, countStr.indexOf(" ")).toInt()
            } catch (e: Exception) {
                0
            }
        }

        fun getAdjustedAvatar(): String = avatar

        fun getTotalPage(): Int = maxOf(maxOf(page, currentPage), 1)
    }

    @Immutable
    @Slice
    data class Reply(
        @property:Pick(value = "div.reply_content", attr = Attrs.HTML)
        val replyContent: String = "",
        @property:Pick("strong a.dark[href^=/member]")
        val userName: String = "",
        @property:Pick(value = "img.avatar", attr = "src")
        val avatar: String = "",
        @property:Pick("span.fade.small:not(:contains(♥))")
        val time: String = "",
        @property:Pick("span.small.fade:has(img)")
        val thanksText: String = "",
        @property:Pick("span.no")
        val floor: Int = 0,
        @property:Pick("div.thank_area.thanked")
        val alreadyThanked: String = "",
        @property:Pick(attr = "id")
        val replyIdText: String = "",
    ) {
        fun replyId(): String = replyIdText.substringAfter("_", "")

        fun hadThanked(): Boolean = alreadyThanked.isNotEmpty()

        fun getAdjustedAvatar(): String = avatar

        fun thanksCount(): Int = thanksText.toIntOrNull() ?: 0
    }
}
