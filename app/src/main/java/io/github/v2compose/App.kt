package io.github.v2compose

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp
import io.github.v2compose.core.NotificationCenter
import io.github.v2compose.core.analytics.IAnalytics
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), ImageLoaderFactory, Configuration.Provider {
    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var analytics: IAnalytics

    companion object {
        private const val TAG = "APP"
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
        if (BuildConfig.DEBUG) analytics.stopTracking()
        NotificationCenter.init(this)
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

    override fun newImageLoader(): ImageLoader = imageLoader

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setWorkerFactory(workerFactory).build()

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