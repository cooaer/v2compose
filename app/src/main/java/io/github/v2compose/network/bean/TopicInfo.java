package io.github.v2compose.network.bean;

import android.text.TextUtils;

import androidx.compose.runtime.Stable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.v2compose.network.NetConstants;
import io.github.v2compose.util.AvatarUtils;
import io.github.v2compose.util.Check;
import io.github.v2compose.util.UriUtils;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;


/**
 * Created by ghui on 04/05/2017.
 */
@Stable
public class TopicInfo extends BaseInfo {
    @Pick("div#Wrapper")
    private HeaderInfo headerInfo;
    @Pick("div.content div.box")
    private ContentInfo contentInfo;
    @Pick("div.problem")
    private Problem problem;
    @Pick("div[id^=r_]")
    private List<Reply> replies;
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick(value = "meta[property=og:url]", attr = "content")
    private String topicLink;
    @Pick(value = "a[onclick*=/report/topic/]", attr = "onclick")
    private String reportLink;
    @Pick(value = "div#Wrapper div.box div.inner span.fade")
    private String hasRePortStr;
    @Pick(value = "a[onclick*=/fade/topic/]", attr = "onclick")
    private String fadeStr;
    @Pick(value = "a[onclick*=/sticky/topic/]", attr = "onclick")
    private String stickyStr;

    public Problem getProblem() {
        return problem;
    }

    public String getTopicLink() {
        return topicLink;
    }

    public boolean canSticky() {
        return Check.notEmpty(stickyStr());
    }

    public boolean canfade() {
        return Check.notEmpty(fadeUrl());
    }

    public boolean hasReported() {
        return !TextUtils.isEmpty(hasRePortStr) && hasRePortStr.contains("已对本主题进行了报告");
    }

    public boolean hasReportPermission() {
        return hasReported() || !TextUtils.isEmpty(reportLink);
    }

    public String reportUrl() {
        if (hasReported()) return null;
        //if (confirm('你确认需要报告这个主题？')) { location.href = '/report/topic/390988?t=1456813618'; }
        int sIndex = reportLink.indexOf("/report/topic/");
        int eIndex = reportLink.lastIndexOf("'");
        return reportLink.substring(sIndex, eIndex);
    }

    public String fadeUrl() {
        if (TextUtils.isEmpty(fadeStr)) return null;
        int sIndex = fadeStr.indexOf("/fade/topic/");
        int eIndex = fadeStr.lastIndexOf("'");
        return fadeStr.substring(sIndex, eIndex);
    }

    public String stickyStr() {
        if (TextUtils.isEmpty(stickyStr)) return null;
        int sIndex = stickyStr.indexOf("/sticky/topic/");
        int eIndex = stickyStr.lastIndexOf("'");
        return stickyStr.substring(sIndex, eIndex);
    }

    public String getOnce() {
        return once;
    }

    public Map<String, String> toReplyMap(String content) {
        HashMap<String, String> map = new HashMap<>();
        map.put("once", once);
        map.put("content", content);
        return map;
    }

    public List<Reply> getReplies() {
        return replies == null ? Collections.emptyList() : replies;
    }

    public ContentInfo getContentInfo() {
        return contentInfo;
    }

    /**
     * replies是否已经reversed
     *
     * @return
     */
    private boolean hasReversed() {
        if (replies.size() >= 2) {
            return replies.get(0).floor > replies.get(1).floor;
        }
        return true;
    }

    public HeaderInfo getHeaderInfo() {
        return headerInfo;
    }

    public void setHeaderInfo(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public int getTotalPage() {
        return headerInfo.getTotalPage();
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
                "topicLink=" + topicLink +
                ", headerInfo=" + headerInfo +
                ", replies=" + replies +
                ", once=" + once +
                '}';
    }

    @Override
    public boolean isValid() {
        if (headerInfo == null) return false;
        return headerInfo.isValid();
    }

    public static class Problem implements Serializable {
        @Pick(attr = Attrs.OWN_TEXT)
        private String title;
        @Pick("ul li")
        private List<String> tips;

        public boolean isEmpty() {
            return Check.isEmpty(tips) && Check.isEmpty(title);
        }

        public List<String> getTips() {
            return tips;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return "Problem{" +
                    "title='" + title + '\'' +
                    ", tips=" + tips +
                    '}';
        }
    }

    public static class ContentInfo extends BaseInfo {
        @Pick(attr = Attrs.HTML)
        private String html;

        private String formatedHtml;

        @Pick(value = "div.cell div.topic_content", attr = Attrs.HTML)
        private String content;

        @Pick("div.subtle")
        private List<Supplement> supplements;

        /**
         * 得到处理后的html, 移除最后一个element(时间，收藏，等不需要显示的信息)
         * @return
         */
        public String getFormattedHtml() {
            if (formatedHtml != null) return formatedHtml;
            Document parentNode = Jsoup.parse(html);
            parentNode.getElementsByClass("header").remove();
            parentNode.getElementsByClass("inner").remove();
            if ("".equals(parentNode.text())
                    && parentNode.getElementsByClass("embedded_video_wrapper") == null) {
                formatedHtml = null;
                return formatedHtml;
            } else {
                formatedHtml = parentNode.body().html();
            }
            return formatedHtml;
        }

        public String getContent() {
            return content == null ? "" : content;
        }

        public List<Supplement> getSupplements() {
            return supplements != null ? supplements : Collections.EMPTY_LIST;
        }

        @Override
        public boolean isValid() {
            return !TextUtils.isEmpty(getFormattedHtml());
        }

        public static class Supplement implements Serializable {
            @Pick("span.fade")
            private String title;
            @Pick(value = "div.topic_content", attr = Attrs.HTML)
            private String content;

            public String getTitle() {
                return title;
            }

            public String getContent() {
                return content;
            }
        }
    }

    public static class HeaderInfo extends BaseInfo {
        @Pick(value = "div.box img.avatar", attr = "src")
        private String avatar;
        @Pick("div.box small.gray a")
        private String userName;
        @Pick(value = "div.box small.gray", attr = "ownText")
        private String time;
        @Pick("div.box a[href^=/go]")
        private String tag;
        @Pick(value = "div.box a[href^=/go]", attr = Attrs.HREF)
        private String tagLink;
        @Pick("div.cell span.gray:contains(回复)")
        private String comment;
        @Pick("div.box div.inner a.page_normal:last-of-type")
        private int page;
        @Pick("div.box div.inner span.page_current")
        private int currentPage;
        @Pick("div.box h1")
        private String title;
        @Pick(value = "div.content div.box div.inner span:first-child")
        private String favoriteText;
        @Pick(value = "div.box a[href*=favorite/]", attr = Attrs.HREF)
        private String favoriteLink;
        @Pick(value = "div.box a[onclick*=ignore/]", attr = "onclick")
        private String ignoreLink;
        @Pick("div.box div[id=topic_thank]")
        private String thankedText;// 感谢已发送
        @Pick("div.box div.inner div#topic_thank")
        private String canSendThanksText;
        private String computedTime;
        @Pick("div.box div.header a.op")
        private String appendTxt;

        public HeaderInfo() {
        }

        private HeaderInfo(TopicBasicInfo basicInfo) {
            this.avatar = basicInfo.getAvatar();
            this.title = basicInfo.getTitle();
            this.userName = basicInfo.getAuthor();
            this.tag = basicInfo.getTag();
            this.tagLink = basicInfo.getTagLink();
        }

        public static HeaderInfo build(TopicBasicInfo basicInfo) {
            return new HeaderInfo(basicInfo);
        }

        @Override
        public boolean isValid() {
            return Check.notEmpty(userName, tag);
        }


        public boolean canAppend() {
            return Check.notEmpty(appendTxt) && appendTxt.equals("APPEND");
        }

        /**
         * new user can't send thanks
         * @return
         */
        public boolean canSendThanks() {
            return Check.notEmpty(canSendThanksText);
        }

        public boolean hadThanked() {
            return Check.notEmpty(thankedText) && thankedText.contains("已发送");
        }

        public String getFavoriteLink() {
            return favoriteLink;
        }

        public String getT() {
            if (Check.isEmpty(favoriteLink)) {
                return null;
            }
            return UriUtils.getParamValue(NetConstants.BASE_URL + favoriteLink, "t");
        }

        public boolean hadFavorited() {
            return !Check.isEmpty(favoriteLink) && favoriteLink.contains("unfavorite/");
        }

        //17 人收藏
        public int getFavoriteCount(){
            if(Check.isEmpty(favoriteText)){
                return 0;
            }
            try{
                return Integer.parseInt(favoriteText.trim().substring(0, favoriteText.indexOf(" ")));
            }catch (Exception e){
                e.printStackTrace();
            }
            return 0;
        }

        public String getCommentNum() {
            if (Check.isEmpty(comment)) return "";
            return comment.split(" ")[0];
        }

        public String getTagLink() {
            return tagLink;
        }

        public String getTag() {
            return tag;
        }

        public String getTagId() {
            return tagLink.replace("/go/", "");
        }

        public String getTime() {
            try {
                if (Check.isEmpty(computedTime) && Check.notEmpty(time) && time.contains("·")) {
                    computedTime = time.split("·")[0].trim().substring(6).replaceAll(" ", "").trim();
                    if (computedTime.contains("-") && computedTime.contains("+")) {
                        computedTime = computedTime.substring(0, 10);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return computedTime;
        }

        public int getViewCount() {
            try {
                String count = time.split("·")[1].trim();
                return Integer.parseInt(count.substring(0, count.indexOf(" ")));
            } catch (Exception e) {
                return 0;
            }
        }

        public String getUserName() {
            return userName;
        }

        public String getAvatar() {
            return AvatarUtils.adjustAvatar(avatar);
        }

        public String getTitle() {
            return title;
        }

        public int getTotalPage() {
            return Math.max(Math.max(page, currentPage), 1);
        }

        public boolean hadIgnored() {
            return !Check.isEmpty(ignoreLink) && ignoreLink.contains("unignore/");
        }

        @Override
        public String toString() {
            return "HeaderInfo{" +
                    "avatar='" + avatar + '\'' +
                    ", userName='" + userName + '\'' +
                    ", time='" + time + '\'' +
                    ", tag='" + tag + '\'' +
                    ", tagLink='" + tagLink + '\'' +
                    ", comment='" + comment + '\'' +
                    ", page=" + page +
                    ", currentPage=" + currentPage +
                    ", title='" + title + '\'' +
                    ", favoriteLink='" + favoriteLink + '\'' +
                    ", ignoreLink='" + ignoreLink + '\'' +
                    ", thankedText='" + thankedText + '\'' +
                    ", canSendThanksText='" + canSendThanksText + '\'' +
                    ", computedTime='" + computedTime + '\'' +
                    ", appendTxt='" + appendTxt + '\'' +
                    '}';
        }
    }

    @Stable
    public static class Reply implements Serializable{
        @Pick(value = "div.reply_content", attr = Attrs.INNER_HTML)
        private String replyContent;
        @Pick("strong a.dark[href^=/member]")
        private String userName;
        @Pick(value = "img.avatar", attr = "src")
        private String avatar;
        @Pick("span.fade.small:not(:contains(♥))")
        private String time;
        @Pick("span.small.fade:has(img)")
        private String thanksText;
        @Pick("span.no")
        private int floor;
        @Pick("div.thank_area.thanked")
        private String alreadyThanked;
        @Pick(attr = "id")
        private String replyId;
        private boolean isOwner = false;

        public boolean isOwner() {
            return isOwner;
        }

        public int getFloor() {
            return floor;
        }

        public String getReplyId() {
            if (Check.isEmpty(replyId)) return null;
            try {
                return replyId.substring(replyId.indexOf("_") + 1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        public boolean hadThanked() {
            return Check.notEmpty(alreadyThanked);
        }

        public String getReplyContent() {
            return replyContent;
        }

        public String getUserName() {
            return userName;
        }

        public String getAvatar() {
            return AvatarUtils.adjustAvatar(avatar);
        }

        public String getTime() {
            return time;
        }

        public int getThanksCount() {
            int thanksCount = 0;
            if (Check.isEmpty(thanksText)) {
                return thanksCount;
            }
            try {
                thanksCount = Integer.parseInt(thanksText);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return thanksCount;
        }

        @Override
        public String toString() {
            return "Reply{" +
                    "replyContent='" + replyContent + '\'' +
                    ", userName='" + userName + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", time='" + time + '\'' +
                    ", thanksText='" + thanksText + '\'' +
                    ", floor=" + floor +
                    ", alreadyThanked='" + alreadyThanked + '\'' +
                    ", replyId='" + replyId + '\'' +
                    ", isOwner=" + isOwner +
                    '}';
        }
    }

}
