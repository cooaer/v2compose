package io.github.v2compose.network.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.compose.runtime.Stable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.v2compose.util.AvatarUtils;
import io.github.v2compose.util.Check;
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

    public HeaderInfo getHeaderInfo() {
        return headerInfo;
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
         *
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
            return supplements != null ? supplements : Collections.emptyList();
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
        @Pick(value = "div.content div.box:first-child div.inner span:first-child")
        private String favoriteText;
        @Pick(value = "div.box a[href*=favorite/]", attr = Attrs.HREF)
        private String favoriteLink;
        @Pick(value = "div.box a[onclick*=ignore/]", attr = "onclick")
        private String ignoreLink;
        @Pick("div.box div[id=topic_thank]")
        private String thankedText;// 感谢已发送
        @Pick("div.box div.inner div#topic_thank")
        private String canSendThanksText;
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
         *
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

        public boolean hadFavorited() {
            return !Check.isEmpty(favoriteLink) && favoriteLink.contains("unfavorite/");
        }

        private int _favoriteCount = -1;

        //17 人收藏
        public int getFavoriteCount() {
            if (_favoriteCount >= 0) return _favoriteCount;
            if (Check.isEmpty(favoriteText)) {
                return 0;
            }
            try {
                _favoriteCount = Integer.parseInt(favoriteText.trim().substring(0, favoriteText.indexOf(" ")));
                return _favoriteCount;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        private String _commentNum;

        public String getCommentNum() {
            if (_commentNum != null) return _commentNum;
            if (Check.isEmpty(comment)) return "";
            _commentNum = comment.split(" ")[0];
            return _commentNum;
        }

        public String getTagLink() {
            return tagLink;
        }

        public String getTag() {
            return tag;
        }

        private String _tagName;

        public String getTagName() {
            if (_tagName != null) return _tagName;
            _tagName = tagLink.replace("/go/", "");
            return _tagName;
        }

        private String _time;

        public String getTime() {
            if (_time != null) return _time;
            try {
                if (Check.notEmpty(time) && time.contains("·")) {
                    String tempTime = time.split("·")[0].trim().substring(6).replaceAll(" ", "").trim();
                    if (tempTime.contains("-") && tempTime.contains("+")) {
                        tempTime = tempTime.substring(0, 10);
                    }
                    _time = tempTime;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return _time == null ? "" : _time;
        }

        private int _viewCount = -1;

        public int getViewCount() {
            if (_viewCount >= 0) return _viewCount;
            try {
                String count = time.split("·")[1].trim();
                _viewCount = Integer.parseInt(count.substring(0, count.indexOf(" ")));
                return _viewCount;
            } catch (Exception e) {
                return 0;
            }
        }

        public String getUserName() {
            return userName;
        }

        private String _avatar;

        public String getAvatar() {
            if (_avatar != null) return _avatar;
            _avatar = AvatarUtils.adjustAvatar(avatar);
            return _avatar;
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
                    ", appendTxt='" + appendTxt + '\'' +
                    '}';
        }
    }

    @Stable
    public static class Reply implements Serializable {
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

        public int getFloor() {
            return floor;
        }

        private String _replyId;

        public String getReplyId() {
            if (_replyId != null) return _replyId;
            if (Check.isEmpty(replyId)) return null;
            try {
                _replyId = replyId.substring(replyId.indexOf("_") + 1);
                return _replyId;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public boolean hadThanked() {
            return Check.notEmpty(alreadyThanked);
        }

        private String _replyContent;

        public String getReplyContent() {
            if (_replyContent != null) return _replyContent;
            _replyContent = replyContent.trim();
            return _replyContent;
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

        private int _thanksCount = -1;

        public int getThanksCount() {
            if (_thanksCount >= 0) return _thanksCount;
            if (Check.isEmpty(thanksText)) return 0;
            try {
                _thanksCount = Integer.parseInt(thanksText);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return _thanksCount;
        }

        @NonNull
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
                    '}';
        }
    }

}
