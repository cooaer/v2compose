package io.github.v2compose.network;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.CookieStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.v2compose.util.L;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by ghui on 10/07/2017.
 */

public class WebkitCookieManager implements CookieJar {

    private static final String TAG = WebkitCookieManager.class.getSimpleName();
    private final android.webkit.CookieManager cookieManager;

    public WebkitCookieManager() {
        this.cookieManager = android.webkit.CookieManager.getInstance();
    }

    public void put(URI uri, Map<String, List<String>> responseHeaders)
            throws IOException {
        // make sure our args are valid
        if ((uri == null) || (responseHeaders == null))
            return;
        // save our url once
        String url = uri.toString();
        // go over the headers
        for (String headerKey : responseHeaders.keySet()) {
            // ignore headers which aren't cookie related
            if ((headerKey == null)
                    || !(headerKey.equalsIgnoreCase("Set-Cookie2") || headerKey
                    .equalsIgnoreCase("Set-Cookie")))
                continue;
            // process each of the headers
            for (String headerValue : responseHeaders.get(headerKey)) {
                cookieManager.setCookie(url, headerValue);
            }
        }
    }

    public Map<String, List<String>> get(URI uri,
                                         Map<String, List<String>> requestHeaders) throws IOException {
        // make sure our args are valid
        if ((uri == null) || (requestHeaders == null))
            throw new IllegalArgumentException("Argument is null");
        // save our url once
        String url = uri.toString();
        // prepare our response
        Map<String, List<String>> res = new HashMap<String, List<String>>();
        // get the cookie
        String cookie = cookieManager.getCookie(url);
        // return it
        if (cookie != null) {
            res.put("Cookie", Arrays.asList(cookie));
        }
        return res;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        HashMap<String, List<String>> generatedResponseHeaders = new HashMap<>();
        ArrayList<String> cookiesList = new ArrayList<>();
        for (Cookie c : cookies) {
            // toString correctly generates a normal cookie string
            cookiesList.add(c.toString());
        }

        generatedResponseHeaders.put("Set-Cookie", cookiesList);
        try {
            put(url.uri(), generatedResponseHeaders);
        } catch (IOException e) {
            L.e(e.toString());
        }
    }

    @NonNull
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        ArrayList<Cookie> cookieArrayList = new ArrayList<>();
        try {
            Map<String, List<String>> cookieList = get(url.uri(), new HashMap<String, List<String>>());
            // Format here looks like: "Cookie":["cookie1=val1;cookie2=val2;"]
            for (List<String> ls : cookieList.values()) {
                for (String s : ls) {
                    String[] cookies = s.split(";");
                    for (String cookie : cookies) {
                        Cookie c = Cookie.parse(url, cookie);
                        cookieArrayList.add(c);
                    }
                }
            }
        } catch (IOException e) {
            L.e(e.toString());
        }
        return cookieArrayList;
    }

    public void clearCookies() {
        cookieManager.removeAllCookies(null);
    }
}
