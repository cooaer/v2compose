package io.github.v2compose.network.bean;

import androidx.compose.runtime.Stable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.github.v2compose.network.NetConstants;
import io.github.v2compose.util.AvatarUtils;
import io.github.v2compose.util.Check;
import io.github.v2compose.util.Utils;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 10/05/2017.
 */

@Stable
@Pick("div#Wrapper")
public class NotificationInfo extends BaseInfo {
    @Pick("div#Main div.box div.fr.f12 strong")
    private int total;
    @Pick("div#Main div.box div.cell[id^=n_]")
    private List<Reply> replies;

    @Pick("div#Rightbar div.box a[href*=notifications]")
    private String unread;

    public int getTotal() {
        return total;
    }

    public List<Reply> getReplies() {
        return replies != null ? replies : Collections.emptyList();
    }

    public int getUnreadCount() {
        if (Check.isEmpty(unread)) return 0;
        return Integer.parseInt(unread.split(" ")[0]);
    }

    @Override
    public String toString() {
        return "NotificationInfo{" +
                "total=" + total +
                ", replies=" + replies +
                '}';
    }

    @Override
    public boolean isValid() {
        if (Utils.listSize(replies) <= 0) return true;
        return Check.notEmpty(replies.get(0).name);
    }

    @Stable
    public static class Reply implements Serializable {
        @Pick(value = "div.cell[id^=n_]", attr = "id")
        private String idText;
        @Pick("a[href^=/member/] strong")
        private String name;
        @Pick(value = "a[href^=/member/] img", attr = Attrs.SRC)
        private String avatar;
        @Pick(value = "span.fade")
        private String title;
        @Pick(value = "a[href^=/t/]", attr = Attrs.HREF)
        private String link;
        @Pick(value = "div.payload", attr = Attrs.INNER_HTML)
        private String content;
        @Pick("span.snow")
        private String time;

        public String getId() {
            return idText.substring(2);
        }

        public String getLink() {
            return NetConstants.BASE_URL + link;
        }


        public String getTitle() {
            if (Check.notEmpty(title))
                return title.replaceFirst(name, "").trim();
            return title;
        }

        public String getName() {
            return name;
        }

        public String getAvatar() {
            return AvatarUtils.adjustAvatar(avatar);
        }

        public String getContent() {
            return content != null ? content : "";
        }

        public String getTime() {
            return time;
        }

        @Override
        public String toString() {
            return "Reply{" +
                    "idText='" + idText + '\'' +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", content='" + content + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }

}
