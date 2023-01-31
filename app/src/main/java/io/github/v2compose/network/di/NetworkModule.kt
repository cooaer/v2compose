package io.github.v2compose.network.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.v2compose.network.GithubApi
import io.github.v2compose.network.OkHttpFactory
import io.github.v2compose.network.V2exApi
import me.ghui.fruit.Fruit
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import javax.inject.Qualifier

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
    fun provideV2exApi(): V2exApi {
        return V2exApi.instance
    }

    @Provides
    fun provideGithubApi(): GithubApi {
        return GithubApi.instance
    }

    @Provides
    fun provideGson(): Gson = OkHttpFactory.gson

    @Provides
    fun provideFruit(): Fruit = OkHttpFactory.fruit

    @Provides
    fun provideCookieJar(): CookieJar = OkHttpFactory.cookieJar

    @Provides
    @CommonOkHttpClient
    fun provideCommonOkHttpClient(): OkHttpClient = OkHttpFactory.httpClient

    @Provides
    @ImageOkHttpClient
    fun provideImageOkHttpClient(): OkHttpClient = OkHttpFactory.imageHttpClient

}