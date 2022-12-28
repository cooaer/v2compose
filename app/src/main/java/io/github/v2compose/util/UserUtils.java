package io.github.v2compose.util;

import io.github.v2compose.network.bean.UserInfo;

/**
 * Created by ghui on 30/04/2017.
 */

public class UserUtils {

    //从本地缓存中读取当前登录的用户的信息
    //TODO
    public static UserInfo getUserInfo() {
        return null;
    }

    public static String getUserName() {
        if (!isLogin()) return null;
        return getUserInfo().getUserName();
    }

    public static String getUserID() {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null) return "";
        return userInfo.getId();
    }

    public static boolean isLogin() {
        return getUserInfo() != null;
    }

}
