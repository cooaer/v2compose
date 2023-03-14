package io.github.v2compose.network.bean;

import java.io.Serializable;
import java.util.List;

import io.github.v2compose.util.AvatarUtils;
import io.github.v2compose.util.Check;
import io.github.v2compose.util.Utils;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 12/05/2017.
 * https://www.v2ex.com/my/following?p=1
 */

@Pick("div#Wrapper")
public class MyFollowingInfo extends BaseInfo {
    @Pick(value = "input.page_input", attr = "max")
    private int totalPageCount;
    @Pick("div.cell.item")
    private List<Item> items;

    @Override
    public String toString() {
        return "CareInfo{" +
                "totalPageCount=" + totalPageCount +
                ", items=" + items +
                '}';
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public boolean isValid() {
        if (Utils.listSize(items) <= 0) return true;
        return Check.notEmpty(items.get(0).userName);
    }

    public static class Item implements Serializable {
        @Pick(value = "img.avatar", attr = Attrs.SRC)
        private String avatar;
        @Pick("strong a[href^=/member/]")
        private String userName;
        @Pick(value = "span[title]", attr = Attrs.OWN_TEXT)
        private String time;
        @Pick("span.item_title a[href^=/t/]")
        private String title;
        @Pick(value = "span.item_title a[href^=/t/]", attr = Attrs.HREF)
        private String link;
        @Pick("a[class^=count_]")
        private int commentNum;
        @Pick("a.node")
        private String tagTitle;
        @Pick(value = "a.node", attr = Attrs.HREF)
        private String tagLink;

        @Override
        public String toString() {
            return "Item{" +
                    "avatar='" + avatar + '\'' +
                    ", userName='" + userName + '\'' +
                    ", time='" + time + '\'' +
                    ", title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", comentNum=" + commentNum +
                    ", tagTitle='" + tagTitle + '\'' +
                    ", tagLink='" + tagLink + '\'' +
                    '}';
        }

        private String _id;

        public String getId() {
            if (_id != null) return _id;
            if (link == null) return "";
            _id = link.substring("/t/".length(), link.indexOf('#'));
            return _id;
        }

        public String getTime() {
            return time == null ? "" : time;
        }

        private String _avatar;

        public String getAvatar() {
            if (_avatar != null) return _avatar;
            if (avatar == null) return "";
            _avatar = AvatarUtils.adjustAvatar(avatar);
            return _avatar;
        }

        public String getUserName() {
            return userName;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public int getCommentNum() {
            return commentNum;
        }

        private String _tagName;

        public String getTagName() {
            if (_tagName != null) return _tagName;
            _tagName = tagLink.substring("/go/".length());
            return _tagName;
        }

        public String getTagTitle() {
            return tagTitle;
        }

        public String getTagLink() {
            return tagLink;
        }
    }
}
