package io.github.v2compose.ui.settings.compoables

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.ActivityCompat
import io.github.v2compose.core.NotificationCenter


fun checkAndRequestNotificationPermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    showRationale: () -> Unit,
    onDenied: () -> Unit,
    onGranted: () -> Unit,
) {
    val channelEnabled = NotificationCenter.isAutoCheckInChannelEnabled(context)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        if (channelEnabled) onGranted() else onDenied()
        return
    }

    val permission = Manifest.permission.POST_NOTIFICATIONS
    val permissionResult = ActivityCompat.checkSelfPermission(context, permission)
    if (permissionResult == PackageManager.PERMISSION_GRANTED) {
        if (channelEnabled) onGranted() else showRationale()
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.POST_NOTIFICATIONS,
                )
            ) {
                showRationale()
                return
            }
        }
        launcher.launch(permission)
    }
}