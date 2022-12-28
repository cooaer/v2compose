package io.github.v2compose.util;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by ghui on 01/04/2017.
 */

public class Utils {
    public static int listSize(List list) {
        return list == null ? 0 : list.size();
    }

    public static String extractDigits(String src) {
        if (TextUtils.isEmpty(src)) return "";
        return src.replaceAll("\\D+","");
    }

}

