package com.kaleyra.app_utilities.notification

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.kaleyra.app_configuration.utils.hideKeyboard
import com.kaleyra.app_utilities.R

fun AppCompatActivity.requestPushNotificationPermissionApi33() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return

    registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        val hideKeyboard = Handler(Looper.getMainLooper())
        hideKeyboard.postDelayed({ hideKeyboard() }, 4000)
        if (result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.notification_permission)
                    .setMessage(R.string.notification_permission_message)
                    .setPositiveButton(R.string.button_ok) { _, _ -> showTestNotification() }
                    .setNegativeButton(R.string.cancel_action, null)
                    .show()
            } else {
                showTestNotification()
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.notification_permission)
                .setMessage(R.string.notification_permission_app_settings)
                .setPositiveButton(R.string.button_ok) { _, _ ->
                    kotlin.runCatching {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
                .setNegativeButton(R.string.cancel_action, null)
                .show()
        }
    }.launch(Manifest.permission.POST_NOTIFICATIONS)
}

fun AppCompatActivity.showTestNotification() = kotlin.runCatching {
    val notificationId = "notification_channel_enabled"
    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(notificationId, "Notifications enabled channel", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    val stringId = applicationInfo.labelRes
    val appName = if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
    val applicationIcon = applicationContext.packageManager.getApplicationIcon(packageName).toBitmap()
    val message = getString(R.string.notifications_enabled)

    val builder = NotificationCompat.Builder(this, notificationId)
        .setContentTitle(appName)
        .setContentText(message)
        .setTimeoutAfter(2500)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setLargeIcon(applicationIcon)

    notificationManager.notify(12345, builder.build())
}
