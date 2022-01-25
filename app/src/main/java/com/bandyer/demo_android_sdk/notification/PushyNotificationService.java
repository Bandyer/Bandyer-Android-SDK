/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bandyer.app_utilities.notification.PushyCompat;


/**
 * @author kristiyan
 */
public class PushyNotificationService extends PushyCompat {

    private static final String TAG = PushyNotificationService.class.getSimpleName();

    public PushyNotificationService() {
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        String payload = intent.getStringExtra("payload");
        Log.d(TAG, "Pushy payload received: " + payload);

        Data data = new Data.Builder()
                .putString("payload", payload)
                .build();

        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(PushNotificationPayloadWorker.class)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(mRequest);
    }
}
