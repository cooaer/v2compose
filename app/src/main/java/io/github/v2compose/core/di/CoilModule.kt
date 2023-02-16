package io.github.v2compose.core.di

import android.content.Context
import coil.ImageLoader
import coil.imageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CoilModule {

    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return context.imageLoader
    }

}