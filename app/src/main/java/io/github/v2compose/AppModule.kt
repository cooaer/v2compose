package io.github.v2compose

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.v2compose.network.di.ImageOkHttpClient
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        @ImageOkHttpClient httpClient: OkHttpClient,
        diskCache: DiskCache,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(httpClient)
            .diskCache(diskCache)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(SvgDecoder.Factory())
            }.build()
    }

    @Provides
    @Singleton
    fun provideDiskCache(@ApplicationContext context: Context): DiskCache {
        val dir = File(context.cacheDir, "image_cache")
        return DiskCache.Builder().directory(dir).maxSizePercent(0.02).build()
    }

}