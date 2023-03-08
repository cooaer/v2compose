package io.github.v2compose.network

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.v2compose.BuildConfig
import io.github.v2compose.Constants
import io.github.v2compose.bean.RedirectEvent
import io.github.v2compose.network.NetConstants.keyUserAgent
import io.github.v2compose.network.NetConstants.wapUserAgent
import io.github.v2compose.network.di.V2ProxySelector
import io.github.v2compose.util.Check
import io.github.v2compose.util.L
import me.ghui.fruit.Fruit
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.TimeUnit

object OkHttpFactory {

    private const val TIMEOUT_SECONDS: Long = 10

//    val gson: Gson by lazy { createGson() }
//    val fruit: Fruit by lazy { createFruit() }
//    val cookieManager: WebkitCookieManager by lazy { createCookieManager() }
//    val httpClient: OkHttpClient by lazy { createHttpClient() }
//    val imageHttpClient: OkHttpClient by lazy { createImageHttpClient(cookieManager) }

    fun createGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    fun createFruit(): Fruit {
        return Fruit()
    }

    fun createHttpClient(
        cookieJar: CookieJar,
        cache: Cache,
        proxySelector: V2ProxySelector
    ): OkHttpClient {
        val builder: OkHttpClient.Builder =
            OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .cache(cache)
                .cookieJar(cookieJar)
                .retryOnConnectionFailure(true)
                .addInterceptor(ConfigInterceptor())
                .addInterceptor(RedirectInterceptor())
                .followRedirects(false)
                .followSslRedirects(false)
                .proxySelector(proxySelector)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor { msg: String? -> L.v(msg) }
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }
        return builder.build()
    }

    fun createImageHttpClient(cookieJar: CookieJar, proxySelector: V2ProxySelector): OkHttpClient {
        val builder: OkHttpClient.Builder =
            OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .retryOnConnectionFailure(true)
                .addInterceptor(ConfigInterceptor())
                .proxySelector(proxySelector)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor { msg: String? -> L.v(msg) }
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }
        return builder.build()
    }

    fun createCookieManager(): WebkitCookieManager {
        return WebkitCookieManager()
    }

    fun createCache(context: Context): Cache {
        val cacheDir = File(context.cacheDir, "http_cache")
        val cacheMaxSize: Long = 100 * 1024 * 1024 //100M
        return Cache(cacheDir, cacheMaxSize)
    }

    private class ConfigInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()
            val ua = request.header(keyUserAgent)
            if (Check.isEmpty(ua)) {
                request = request.newBuilder().addHeader(keyUserAgent, wapUserAgent).build()
            }
            return chain.proceed(request)
        }
    }

    private class RedirectInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val resp = chain.proceed(chain.request())
            if (resp.isRedirect && chain.request().url.host.contains(Constants.host)) {
                resp.header("location")?.let { EventBus.getDefault().post(RedirectEvent(it)) }
            }
            return resp
        }
    }


}