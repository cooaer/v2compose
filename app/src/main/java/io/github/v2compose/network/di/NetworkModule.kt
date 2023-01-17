package io.github.v2compose.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.v2compose.network.V2exApi
import io.github.v2compose.network.OkHttpFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideV2exApi(): V2exApi {
        return V2exApi.instance
    }

}