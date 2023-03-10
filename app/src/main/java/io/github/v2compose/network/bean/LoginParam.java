package io.github.v2compose.network.bean;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import io.github.v2compose.util.Check;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;


/**
 * Created by ghui on 01/05/2017.
 */

public class LoginParam extends BaseInfo {
    @Pick(value = "input.sl[type=text]", attr = "name")
    private String nameParam;
    @Pick(value = "input[type=password]", attr = "name")
    private String pswParam;
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick(value = "input[placeholder*=验证码]", attr = "name")
    private String captchaParam;
    @Pick(value = "div.problem", attr = Attrs.INNER_HTML)
    private String problem;

    @NonNull
    @Override
    public String toString() {
        return "LoginParam{" +
                "nameParam='" + nameParam + '\'' +
                ", pswParam='" + pswParam + '\'' +
                ", once='" + once + '\'' +
                ", captureParam='" + captchaParam + '\'' +
                ", problem='" + problem + '\'' +
                '}';
    }

    public String getNameParam() {
        return nameParam;
    }

    public String getPswParam() {
        return pswParam;
    }

    public String getOnce() {
        return once;
    }

    public boolean needCaptcha() {
        return Check.notEmpty(captchaParam);
    }

    public String getProblem() {
        return problem != null ? problem : "";
    }

    public Map<String, String> toMap(String userName, String psw, String captcha) {
        Map<String, String> map = new HashMap<>();
        map.put(nameParam, userName);
        map.put(pswParam, psw);
        map.put(captchaParam, captcha);
        map.put("once", once);
//        map.put("next", "/mission/daily");
        return map;
    }

    @Override
    public boolean isValid() {
        return Check.notEmpty(nameParam, pswParam, once);
    }
}
