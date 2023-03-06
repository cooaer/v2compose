package io.github.v2compose.network.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.v2compose.network.GithubService
import io.github.v2compose.network.OkHttpFactory
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.WebkitCookieManager
import me.ghui.fruit.Fruit
import okhttp3.Cache
import okhttp3.OkHttpClient
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CommonOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideV2exApi(
        @CommonOkHttpClient httpClient: OkHttpClient, fruit: Fruit, gson: Gson
    ): V2exService {
        return V2exService.createV2exService(httpClient, fruit, gson)
    }

    @Provides
    fun provideGithubApi(@CommonOkHttpClient httpClient: OkHttpClient, gson: Gson): GithubService {
        return GithubService.createGithubApi(httpClient, gson)
    }

    @Provides
    fun provideGson(): Gson = OkHttpFactory.createGson()

    @Provides
    fun provideFruit(): Fruit = OkHttpFactory.createFruit()

    @Provides
    fun provideCookieManager(): WebkitCookieManager = OkHttpFactory.createCookieManager()

    @Provides
    @CommonOkHttpClient
    fun provideCommonOkHttpClient(cookieJar: WebkitCookieManager, cache: Cache): OkHttpClient =
        OkHttpFactory.createHttpClient(cookieJar, cache)

    @Provides
    @ImageOkHttpClient
    fun provideImageOkHttpClient(cookieJar: WebkitCookieManager): OkHttpClient =
        OkHttpFactory.createImageHttpClient(cookieJar)

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache =
        OkHttpFactory.createCache(context)

}

