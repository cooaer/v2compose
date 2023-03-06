package io.github.v2compose.network.bean;

import io.github.v2compose.util.Check;
import io.github.v2compose.util.UriUtils;
import io.github.v2compose.util.Utils;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 07/08/2017.
 */

public class DailyInfo extends BaseInfo {
    @Pick(value = "[href^=/member]", attr = "href")
    private String userLink;
    @Pick(value = "img[src*=avatar/]", attr = "src")
    private String avatar;
    @Pick("h1")
    private String title;
    @Pick("div.cell:contains(已连续)")
    private String continuousLoginDaysText;
    @Pick(value = "div.cell input[type=button]", attr = "onclick")
    private String checkInUrl; //location.href = '/mission/daily/redeem?once=84830';

    public boolean hadCheckedIn() {
        return Check.notEmpty(checkInUrl) && checkInUrl.equals("location.href = '/balance';");
    }

    public String getContinuousLoginDaysText() {
        return continuousLoginDaysText;
    }

    public String getContinuousLoginDays() {
        return Utils.extractDigits(continuousLoginDaysText);
    }


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

    public String once() {
        String result = UriUtils.getParamValue(checkInUrl, "once");
        if (Check.notEmpty(result)) {
            result = result.replace("';", "");
        }
        return result;
    }

    @Override
    public boolean isValid() {
        return Check.notEmpty(checkInUrl);
    }

    @Override
    public String toString() {
        return "DailyInfo{" +
                "title='" + title + '\'' +
                ", continuousLoginDay='" + getContinuousLoginDays() + '\'' +
                ", checkinUrl='" + checkInUrl + '\'' +
                ", once='" + once() + '\'' +
                '}';
    }
}
