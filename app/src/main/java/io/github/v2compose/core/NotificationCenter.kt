package io.github.v2compose.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationManagerCompat
import io.github.v2compose.R


object NotificationCenter {

    const val ChannelAutoCheckIn = "autoCheckIn"

    fun init(context: Context) {
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            ?: return
        manager.createNotificationChannel(
            ChannelAutoCheckIn,
            context.getString(R.string.notification_channel_auto_check_in),
            NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    fun isAutoCheckInChannelEnabled(context: Context): Boolean {
        return checkNotificationsEnabled(context) &&
                checkNotificationChannelEnabled(context, ChannelAutoCheckIn)
    }

    fun checkNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun checkNotificationChannelEnabled(context: Context, channelID: String): Boolean {
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            ?: return false
        val channel = manager.getNotificationChannel(channelID)
        return channel.importance != NotificationManager.IMPORTANCE_NONE
    }

}

fun NotificationManager.createNotificationChannel(
    channelId: String,
    channelName: String,
    importance: Int
) {
    val autoCheckInChannel = NotificationChannel(channelId, channelName, importance)
    createNotificationChannel(autoCheckInChannel)
}
