package io.github.v2compose.network

import android.os.Build
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.v2compose.BuildConfig
import io.github.v2compose.util.Check
import io.github.v2compose.util.L
import me.ghui.fruit.Fruit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpFactory {
    val WAP_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 10; V2compose Build/${Build.MODEL}) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Mobile Safari/537.36"
    const val WEB_USER_AGENT =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4; V2er) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36"
    private const val UA_KEY = "user-agent"
    private const val TIMEOUT_SECONDS: Long = 10

    val gson: Gson by lazy { createGson() }
    val fruit: Fruit by lazy { createFruit() }
    val cookieJar: WebkitCookieManagerProxy by lazy { createCookieJar() }
    val httpClient: OkHttpClient by lazy { createHttpClient() }

    private fun createGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    private fun createFruit(): Fruit {
        return Fruit()
    }

    private fun createHttpClient(): OkHttpClient {
        val builder: OkHttpClient.Builder =
            OkHttpClient.Builder().connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .cookieJar(cookieJar).retryOnConnectionFailure(true)
                .addInterceptor(ConfigInterceptor())
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor { msg: String? -> L.v(msg) }
                    .setLevel(HttpLoggingInterceptor.Level.HEADERS)
            )
        }
        return builder.build()
    }

    private fun createCookieJar(): WebkitCookieManagerProxy {
        return WebkitCookieManagerProxy()
    }

    private class ConfigInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()
            val ua = request.header(UA_KEY)
            if (Check.isEmpty(ua)) {
                request = request.newBuilder().addHeader(UA_KEY, WAP_USER_AGENT).build()
            }
            return chain.proceed(request)
        }
    }


}