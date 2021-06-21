/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bandyer.android_sdk.call.CallModule;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.app_utilities.activities.MockUserAuthenticationRequestActivity;
import com.bandyer.app_utilities.receivers.AuthenticationRequestNotificationClickedReceiver;
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager;
import com.bandyer.app_utilities.utils.FingerprintUtils;
import com.bandyer.demo_android_sdk.R;

public class BiometricNotificationScheduler {

    public static final String notificationUserAuthenticationChannelId = "Authentication";

    private int userAuthenticationRequestScheduleMillis = 10000;
    private int userAuthenticationRequestNotificationId = 238;
    private int userAuthenticationRequestNotificationRequestCode = 239;

    private Handler handler = new Handler(Looper.getMainLooper());

    public void schedule(Context context) {
        if (!ConfigurationPrefsManager.INSTANCE.getConfiguration(context).getWithMockAuthentication())
            return;
        scheduleUserAuthenticationRequest(context);
    }

    public void cancelUserAuthenticationRequest(Context context) {
        NotificationManager notificationManager = ((NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.cancel(userAuthenticationRequestNotificationId);
        handler.removeCallbacksAndMessages(null);
    }

    private void scheduleUserAuthenticationRequest(Context context) {
        handler.postDelayed(() -> {
            if (!FingerprintUtils.canAuthenticateWithBiometricSupport(context)) return;
            CallModule callModule = BandyerSDKClient.getInstance().getCallModule();
            boolean callStillExists = callModule != null && callModule.isInCall();
            if (!callStillExists) return;
            mockUserAuthenticationRequestWithNotification(context);
        }, userAuthenticationRequestScheduleMillis);
    }

    private void mockUserAuthenticationRequestWithNotification(Context context) {
        Intent intent = new Intent(context, MockUserAuthenticationRequestActivity.class);
        intent.setPackage(context.getPackageName());

        Intent broadcastIntent = new Intent(context, AuthenticationRequestNotificationClickedReceiver.class);
        broadcastIntent.setPackage(context.getPackageName());

        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(
                context,
                userAuthenticationRequestNotificationRequestCode,
                broadcastIntent,
                getUpdateFlag()
        );

        NotificationManager notificationManager = ((NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    notificationUserAuthenticationChannelId,
                    notificationUserAuthenticationChannelId,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setBypassDnd(true);
            notificationChannel.setSound(null, null);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, notificationUserAuthenticationChannelId);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setColor(ContextCompat.getColor(context, com.bandyer.app_utilities.R.color.colorPrimary))
                .setSmallIcon(com.bandyer.app_utilities.R.drawable.ic_fingerprint_white_24dp)
                .setContentTitle(context.getString(R.string.biometric_notification_title))
                .setContentText(context.getString(R.string.biometric_notification_description))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(broadcastPendingIntent);

        notificationManager.notify(userAuthenticationRequestNotificationId, b.build());
    }

    private int getUpdateFlag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        else return PendingIntent.FLAG_UPDATE_CURRENT;
    }
}
