package io.github.v2compose.network;

import androidx.annotation.Nullable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.v2compose.BuildConfig;
import io.github.v2compose.util.Check;
import io.github.v2compose.util.L;
import me.ghui.fruit.Fruit;
import me.ghui.fruit.converter.retrofit.FruitConverterFactory;
import me.ghui.retrofit.converter.GlobalConverterFactory;
import me.ghui.retrofit.converter.annotations.Html;
import me.ghui.retrofit.converter.annotations.Json;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by ghui on 25/03/2017.
 */

public class V2exService {
    public static final String WAP_USER_AGENT = "Mozilla/5.0 (Linux; Android 9.0; V2er Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Mobile Safari/537.36";
    public static final String WEB_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4; V2er) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36";
    public static final String UA_KEY = "user-agent";

    public static final long TIMEOUT_LENGTH = 10;
    private static V2exApi mAPI_SERVICE;
    private static Gson sGson;
    private static Fruit sFruit;
    private static WebkitCookieManagerProxy sCookieJar;
    private static OkHttpClient sHttpClient;


    public static void init() {
        if (mAPI_SERVICE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(httpClient())
                    .addConverterFactory(GlobalConverterFactory
                            .create()
                            .add(FruitConverterFactory.create(fruit()), Html.class)
                            .add(GsonConverterFactory.create(gson()), Json.class))
                    .baseUrl(NetConstants.BASE_URL)
                    .build();
            mAPI_SERVICE = retrofit.create(V2exApi.class);
        }
    }

    public static V2exApi api() {
        return mAPI_SERVICE;
    }

    public static Gson gson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
        }
        return sGson;
    }

    public static Fruit fruit() {
        if (sFruit == null) {
            sFruit = new Fruit();
        }
        return sFruit;
    }

    public static OkHttpClient httpClient() {
        if (sHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_LENGTH, TimeUnit.SECONDS)
                    .cookieJar(cookieJar())
                    .retryOnConnectionFailure(true)
                    .addInterceptor(new ConfigInterceptor());
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(new HttpLoggingInterceptor(L::v)
                        .setLevel(HttpLoggingInterceptor.Level.HEADERS));
            }
            sHttpClient = builder.build();
        }
        return sHttpClient;
    }

    public static WebkitCookieManagerProxy cookieJar() {
        if (sCookieJar == null) {
            sCookieJar = new WebkitCookieManagerProxy();
        }
        return sCookieJar;
    }

    private static class ConfigInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String ua = request.header(UA_KEY);
            if (Check.isEmpty(ua)) {
                request = request.newBuilder()
                        .addHeader("user-agent", WAP_USER_AGENT)
                        .build();
            }
            try {
                if (request.url().host().startsWith(".")) {
                    try {
                        HttpUrl.Builder httpUrlBuilder = request.url().newBuilder()
                                .host(NetConstants.WWW_HOST_NAME)
                                .setEncodedPathSegment(0, "t");
                        List<String> encodedPathSegments = request.url().encodedPathSegments();
                        for (int i = 0; i < request.url().encodedPathSegments().size(); i++) {
                            if (i < encodedPathSegments.size() - 1) {
                                httpUrlBuilder.setEncodedPathSegment(i + 1, encodedPathSegments.get(i));
                            } else {
                                httpUrlBuilder.addEncodedPathSegment(encodedPathSegments.get(i));
                            }
                        }
                        request = request.newBuilder()
                                .url(httpUrlBuilder.build())
                                .build();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return chain.proceed(request);
            } catch (Exception e) {
                e.printStackTrace();
                return new Response.Builder()
                        .protocol(Protocol.HTTP_1_1)
                        .code(404)
                        .message("Exeception when execute chain.proceed request")
                        .body(new ResponseBody() {
                            @Nullable
                            @Override
                            public MediaType contentType() {
                                return null;
                            }

                            @Override
                            public long contentLength() {
                                return 0;
                            }

                            @Override
                            public BufferedSource source() {
                                return null;
                            }
                        })
                        .request(request).build();
            }
        }
    }

}
