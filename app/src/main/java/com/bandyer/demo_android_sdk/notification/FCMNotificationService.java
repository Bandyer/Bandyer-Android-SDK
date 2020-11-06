/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bandyer.app_utilities.notification.FirebaseCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Sample implementation of a push notification receiver that handles incoming calls.
 * Push notification are not working in this sample and this class is intended to be used as a
 * sample snippet of code to be used when incoming call notification payloads are received through
 * your preferred push notification implementation.
 * The sample is based on Firebase implementation but can be easily applied to other
 * push notification libraries.
 */
public class FCMNotificationService extends FirebaseMessagingService {

    private static final String TAG = FCMNotificationService.class.getSimpleName();

    /**
     * This function represent the push notification receive callback.
     * The incoming call payload must be extracted from the push notification.
     * The payload will be sent to WorkManager instance through PushNotificationPayloadWorker class to
     * ensure execution even if the app is killed by the system.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            String payload = remoteMessage.getData().get("message");
            Log.d(TAG, "payload received: " + payload);

            Data data = new Data.Builder()
                    .putString("payload", payload)
                    .build();

            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(PushNotificationPayloadWorker.class)
                    .setInputData(data)
                    .build();

            WorkManager.getInstance(getApplicationContext()).enqueue(mRequest);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        FirebaseCompat.registerDevice(this);
    }
}