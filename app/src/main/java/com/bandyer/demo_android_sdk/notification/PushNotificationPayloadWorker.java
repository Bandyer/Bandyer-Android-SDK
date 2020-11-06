/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bandyer.android_sdk.client.BandyerSDKClient;

/**
 * Sample implementation of a worker object used to manage the push notification payload.
 * Using worker interface ensures that the payload parsing and process will be executed even if
 * the application is killed by the system.
 */
public class PushNotificationPayloadWorker extends Worker {

    private static final String TAG = PushNotificationPayloadWorker.class.getSimpleName();

    public PushNotificationPayloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            String payload = getInputData().getString("payload");
            if (payload == null) return Result.failure();
            Log.d(TAG,"Received payload\n" + payload + "\nready to be processed.");
            BandyerSDKClient.getInstance().handleNotification(getApplicationContext(), payload);
        } catch (Throwable e) {
            e.printStackTrace();
            return Result.failure();
        }

        return Result.success();
    }
}

