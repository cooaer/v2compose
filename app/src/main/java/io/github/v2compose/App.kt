package io.github.v2compose

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp
import java.lang.reflect.Field
import java.lang.reflect.Modifier

@HiltAndroidApp
class App : Application(), ImageLoaderFactory {

    companion object {
        const val TAG = "APP"
        lateinit var instance: App
    }

    override fun onCreate() {
        beforeOnCreate()
        super.onCreate()
        instance = this
        init()
    }

    private fun beforeOnCreate() {
        resetScrollableTabRowMinimumTabWidth()
    }

    private fun init() {
        initLogger()
    }

    private fun initLogger() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
            .tag("V2compose.Log") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this).components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
            add(SvgDecoder.Factory())
        }.build()
    }

    private fun resetScrollableTabRowMinimumTabWidth() {
        try {
            val cls = Class.forName("androidx.compose.material3.TabRowKt")
            val field = cls.getDeclaredField("ScrollableTabRowMinimumTabWidth")
            field.isAccessible = true
            val modifiersField = Field::class.java.getDeclaredField("accessFlags")
            modifiersField.isAccessible = true
            modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
            field.set(null, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}