package io.github.v2compose.network.bean;

import io.github.v2compose.util.AvatarUtils;
import io.github.v2compose.util.Check;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

@Pick("div#Wrapper")
public class HomePageInfo extends BaseInfo {
    @Pick("h1")
    private String userName;
    @Pick(value = "img.avatar", attr = Attrs.SRC)
    private String avatar;
    @Pick("td[valign=top] > span.gray")
    private String desc;
    @Pick("strong.online")
    private String online;

    @Pick("a[href=/my/nodes] span.bigger")
    private int nodes;

    @Pick("a[href=/my/topics] span.bigger")
    private int topics;

    @Pick("a[href=/my/following] span.bigger")
    private int following;

    private String getUrl(String onclick) {
        if (Check.notEmpty(onclick)) {
            String reg = "{ location.href = '";
            int start = onclick.indexOf(reg) + reg.length();
            int end = onclick.lastIndexOf("'");
            return onclick.substring(start, end);
        }
        return null;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return AvatarUtils.adjustAvatar(avatar);
    }

    public String getDesc() {
        return desc;
    }

    public boolean isOnline() {
        return Check.notEmpty(online) && online.equals("ONLINE");
    }

    public int getNodes() {
        return nodes;
    }

    public int getTopics() {
        return topics;
    }

    public int getFollowing() {
        return following;
    }

    @Override
    public String toString() {
        return "UserPageInfo{" +
                "userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", desc='" + desc + '\'' +
                ", online='" + online + '\'' +
                '}';
    }

    @Override
    public boolean isValid() {
        return Check.notEmpty(userName);
    }
}
