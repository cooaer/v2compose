package io.github.v2compose.network.bean;

import androidx.annotation.NonNull;
import androidx.compose.runtime.Stable;

import io.github.v2compose.util.AvatarUtils;
import io.github.v2compose.util.Check;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 01/06/2017.
 * https://www.v2ex.com/member/ghui
 */

@Stable
@Pick("div#Wrapper")
public class UserPageInfo extends BaseInfo {
    @Pick("h1")
    private String userName;
    @Pick(value = "img.avatar", attr = Attrs.SRC)
    private String avatar;
    @Pick("td[valign=top] > span.gray")
    private String desc;
    @Pick("strong.online")
    private String online;
    @Pick(value = "div.fr input", attr = "onclick")
    private String followOnClick;
    @Pick(value = "div.fr input[value*=lock]", attr = "onclick")
    private String blockOnClick;

    public boolean hadFollowed() {
        return Check.notEmpty(followOnClick) && followOnClick.contains("取消");
    }

    public boolean hadBlocked() {
        return Check.notEmpty(blockOnClick) && blockOnClick.contains("unblock");
    }

    //    if (confirm('确认要取消对 diskerjtr 的关注？')) { location.href = '/unfollow/128373?once=15154'; }
    public String getFollowUrl() {
        return getUrl(followOnClick);
    }

    //    if (confirm('确认要解除对 diskerjtr 的屏蔽？')) { location.href = '/unblock/128373?t=1456813618'; }
    public String getBlockUrl() {
        return getUrl(blockOnClick);
    }

    private String getUrl(String onclick) {
        if (Check.notEmpty(onclick)) {
            String reg = "{ location.href = '";
            int start = onclick.indexOf(reg) + reg.length();
            int end = onclick.lastIndexOf("'");
            return onclick.substring(start, end);
        }
        return null;
    }

    public boolean isOnline() {
        return Check.notEmpty(online) && online.equals("ONLINE");
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

    @NonNull
    @Override
    public String toString() {
        return "UserPageInfo{" +
                "userName='" + userName + '\'' +
                ", followOnClick='" + followOnClick + '\'' +
                ", blockOnClick='" + blockOnClick + '\'' +
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
