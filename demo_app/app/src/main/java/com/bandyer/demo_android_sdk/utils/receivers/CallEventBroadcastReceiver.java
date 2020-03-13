/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bandyer.android_sdk.call.CallException;
import com.bandyer.android_sdk.call.CallModule;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.intent.call.Call;
import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.FingerprintUtils;
import com.bandyer.demo_android_sdk.utils.activities.MockUserAuthenticationRequestActivity;
import com.bandyer.demo_android_sdk.utils.storage.DefaultCallSettingsManager;


/**
 * A broadcast receiver that receive broadcast when a call starts, ends or ends with error.
 */
public class CallEventBroadcastReceiver extends com.bandyer.android_sdk.call.notification.CallEventBroadcastReceiver {

    public static final String TAG = "CALL EVENT";
    public static final String notificationUserAuthenticationChannelId = "Authentication";

    private int userAuthenticationRequestScheduleMillis = 10000;
    private int userAuthenticationRequestNotificationId = 238;
    private int userAuthenticationRequestNotificationRequestCode = 239;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCallCreated(@NonNull Call ongoingCall) {
    }

    @Override
    public void onCallStarted(@NonNull Call ongoingCall) {
        String callId = ongoingCall.getCallInfo().getCallId();
        Log.d(TAG, "Call " + callId + " started.");
        if (DefaultCallSettingsManager.isMockedUserAuthenticationEnabled(getContext()))
            scheduleUserAuthenticationRequest(getContext());
    }

    @Override
    public void onCallEnded(@NonNull Call ongoingCall) {
        String callId = ongoingCall.getCallInfo().getCallId();
        Log.d(TAG, "Call " + callId + " ended.");
        cancelUserAuthenticationRequest(context);
    }

    @Override
    public void onCallEndedWithError(@NonNull Call ongoingCall, @NonNull CallException callException) {
        String callId = ongoingCall.getCallInfo().getCallId();
        Log.d(TAG, "Call " + callId + " ended with error: " + callException.getMessage());
        cancelUserAuthenticationRequest(context);
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

    private void cancelUserAuthenticationRequest(Context context) {
        NotificationManager notificationManager = ((NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.cancel(userAuthenticationRequestNotificationId);
        handler.removeCallbacksAndMessages(null);
    }

    private void mockUserAuthenticationRequestWithNotification(Context context) {
        Intent intent = new Intent(context, MockUserAuthenticationRequestActivity.class);
        intent.setPackage(context.getPackageName());
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                userAuthenticationRequestNotificationRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIntent = new Intent(context, AuthenticationRequestNotificationClickedReceiver.class);
        broadcastIntent.setPackage(context.getPackageName());

        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(
                context,
                userAuthenticationRequestNotificationRequestCode,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
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
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_fingerprint_white_24dp)
                .setContentTitle("User authentication request.")
                .setContentText("Please click on notification to verify your identity.")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(broadcastPendingIntent)
                .setContentInfo("Info");

        notificationManager.notify(userAuthenticationRequestNotificationId, b.build());
    }
}
