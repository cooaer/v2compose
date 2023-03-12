package io.github.v2compose.core

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.v2compose.R
import io.github.v2compose.usecase.CheckInUseCase

private const val TAG = "CheckInWorker"
private const val NotificationIdCheckIn: Int = 1001

@HiltWorker
class CheckInWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val checkIn: CheckInUseCase,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val result = checkIn()
        Log.d(TAG, "doWork, result = $result")
        return if (result.success) Result.success() else Result.retry()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NotificationIdCheckIn, createNotification())
    }

    private fun createNotification(): Notification {
        return Notification.Builder(applicationContext, NotificationCenter.ChannelAutoCheckIn)
            .setContentTitle(applicationContext.getString(R.string.auto_checking_in))
            .build()
    }

}