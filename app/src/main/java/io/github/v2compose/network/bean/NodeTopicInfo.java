package io.github.v2compose.network.bean;

import android.text.TextUtils;

import androidx.compose.runtime.Stable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.github.v2compose.network.NetConstants;
import io.github.v2compose.util.Check;
import io.github.v2compose.util.UriUtils;
import io.github.v2compose.util.Utils;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 27/05/2017.
 * https://www.v2ex.com/go/python
 */

@Stable
@Pick("div#Wrapper")
public class NodeTopicInfo extends BaseInfo {

    @Pick("span.topic-count strong")
    private int total;
    @Pick(value = "a[href*=favorite/] ", attr = Attrs.HREF)
    private String favoriteLink;
    @Pick("div.box div.cell:has(table)")
    private List<Item> items;

    public int getTotal() {
        return total;
    }

    public List<Item> getItems() {
        return items != null ? items : Collections.emptyList();
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getFavoriteLink() {
        return NetConstants.BASE_URL + favoriteLink;
    }

    public boolean hasStared() {
        return Check.notEmpty(favoriteLink) && favoriteLink.contains("/unfavorite/node/");
    }

    public void updateStarStatus(boolean isStared) {
        if (isStared) {
            favoriteLink = favoriteLink.replace("/favorite/", "/unfavorite/");
        } else {
            favoriteLink = favoriteLink.replace("/unfavorite/", "/favorite/");
        }
    }

    public String getOnce() {
        if (Check.notEmpty(favoriteLink)) {
            return UriUtils.getParamValue(favoriteLink, "once");
        }
        return null;
    }

    @Override
    public String toString() {
        return "NodeTopicInfo{" +
                "favoriteLink=" + favoriteLink +
                ",total=" + total +
                ", items=" + items +
                '}';
    }

    @Override
    public boolean isValid() {
        if (Utils.listSize(items) <= 0) return true;
        return Check.notEmpty(items.get(0).userName);
    }

    @Stable
    public static class Item implements Serializable {
        @Pick(value = "img.avatar", attr = Attrs.SRC)
        private String avatar;
        @Pick("span.item_title")
        private String title;
        @Pick("span.small.fade strong")
        private String userName;
        @Pick(value = "span.small.fade", attr = Attrs.OWN_TEXT)
        private String clickedAndContentLength;
        @Pick("a[class^=count_]")
        private int commentNum;
        @Pick(value = "span.item_title a", attr = Attrs.HREF)
        private String topicLink;

        private int clickNum = -1;

        public String getTopicId() {
            // topicLink example : /t/908177#reply33
            int end = topicLink.indexOf('#');
            return topicLink.substring(3, end);
        }

        public String getTopicLink() {
            return topicLink;
        }

        public String getAvatar() {
            if (!TextUtils.isEmpty(avatar) && avatar.startsWith("http")) {
                return avatar;
            }
            return NetConstants.HTTPS_SCHEME + avatar;
        }

        public String getTitle() {
            return title;
        }

        public String getUserName() {
            return userName;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public int getClickNum() {
            if (clickNum > 0) {
                return clickNum;
            }
            //  •  719 个字符  •  109 次点击
            if (Check.isEmpty(clickedAndContentLength)) {
                clickNum = 0;
            } else {
                int count;
                try {
                    String result = clickedAndContentLength.substring(clickedAndContentLength.lastIndexOf("•") + 1);
                    result = result.replaceAll("[^0-9]", "");
                    count = Integer.parseInt(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    count = 0;
                }
                clickNum = count;
            }
            return clickNum;
        }

        public int getContentLength() {
            if (Check.isEmpty(clickedAndContentLength)) return 0;
            else {
                clickedAndContentLength = clickedAndContentLength.trim();
                String result = clickedAndContentLength.substring(0, clickedAndContentLength.lastIndexOf("•")).trim();
                result = result.split(" ")[1].trim();
                return Integer.parseInt(result);
            }
        }
    }
}
