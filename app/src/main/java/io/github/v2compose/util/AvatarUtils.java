package io.github.v2compose.util;

import io.github.v2compose.network.NetConstants;

/**
 * Created by ghui on 23/06/2017.
 */

public class AvatarUtils {
    public static String adjustAvatar(String avatar) {
        if (Check.isEmpty(avatar)) return null;
        //1.
        if (!avatar.startsWith(NetConstants.HTTPS_SCHEME) && !avatar.startsWith(NetConstants.HTTP_SCHEME)) {
            if (avatar.startsWith("//")) {
                avatar = NetConstants.HTTPS_SCHEME + avatar;
            } else if (avatar.startsWith("/")) {
                avatar = NetConstants.BASE_URL + avatar;
            }
        }

        //2.
        if (avatar.contains("_normal.png")) {
            avatar = avatar.replace("_normal.png", "_large.png");
        } else if (avatar.contains("_mini.png")) {
            avatar = avatar.replace("_mini.png", "_large.png");
        }

        if (avatar.contains("_xxlarge.png")) {
            avatar = avatar.replace("_xxlarge.png", "_large.png");
        }

        //3. del param
//        if (avatar.contains("?")) {
//            avatar = avatar.substring(0, avatar.indexOf("?"));
//        }
        return avatar;
    }
}
