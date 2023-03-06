package io.github.v2compose.network.bean;

import io.github.v2compose.util.Check;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 16/08/2017.
 */
public class TwoStepLoginInfo extends BaseInfo {

    @Pick(value = "[href^=/member]", attr = "href")
    private String userLink;
    @Pick(value = "img[src*=avatar/]", attr = "src")
    private String avatar;
    @Pick(value = "div.problem", attr = Attrs.INNER_HTML)
    private String problem;
    @Pick("tr:first-child")
    private String title;
    @Pick(value = "input[type=hidden]", attr = "value")
    private String once;

    public String getUserName() {
        if (Check.isEmpty(userLink)) {
            return null;
        }
        return userLink.split("/")[2];
    }

    public String getAvatar() {
        if (Check.isEmpty(avatar)) return null;
        return avatar.replace("normal.png", "large.png");
    }

    public String getProblem() {
        return problem != null ? problem : "";
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public String getOnce() {
        return once;
    }

    @Override
    public boolean isValid() {
        return Check.notEmpty(avatar) && Check.notEmpty(avatar) && Check.notEmpty(once) && Check.notEmpty(title) && title.contains("两步验证");
    }

    @Override
    public String toString() {
        return "TwoStepLoginInfo{" +
                "userLink='" + userLink + '\'' +
                ", avatar='" + avatar + '\'' +
                ", problem='" + problem + '\'' +
                ", title='" + title + '\'' +
                ", once='" + once + '\'' +
                '}';
    }
}
