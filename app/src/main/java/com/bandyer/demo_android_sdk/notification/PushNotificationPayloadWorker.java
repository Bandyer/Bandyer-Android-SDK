/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bandyer.android_sdk.client.AccessTokenProvider;
import com.bandyer.android_sdk.client.BandyerSDK;
import com.bandyer.android_sdk.client.Session;
import com.bandyer.android_sdk.client.SessionObserver;
import com.kaleyra.app_utilities.MultiDexApplication;
import com.kaleyra.app_utilities.storage.LoginManager;

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

    private final SessionObserver sessionObserver = new SessionObserver() {
        @Override
        public void onSessionAuthenticating(@NonNull Session session) {
            Log.d(TAG, "onSessionAuthenticating for user " + session.getUserId());
        }

        @Override
        public void onSessionAuthenticated(@NonNull Session session) {
            Log.d(TAG, "onSessionAuthenticated for user " + session.getUserId());
        }

        @Override
        public void onSessionRefreshing(@NonNull Session session) {
            Log.d(TAG, "onSessionRefreshing for user " + session.getUserId());
        }

        @Override
        public void onSessionRefreshed(@NonNull Session session) {
            Log.d(TAG, "onSessionRefreshed for user " + session.getUserId());
        }

        @Override
        public void onSessionError(@NonNull Session session, @NonNull Error error) {
            Log.e(TAG, "onSessionError for user " + session.getUserId() + " with error: " + error.getMessage());
        }
    };

    @NonNull
    @Override
    public Result doWork() {

        try {
            String payload = getInputData().getString("payload");
            if (payload == null) return Result.failure();

            Log.d(TAG,"Received payload\n" + payload + "\nready to be processed.");

            String userId = LoginManager.getLoggedUser(getApplicationContext());

            if (userId.isEmpty()) {
                Log.e(TAG,"Unable to handle notification because no user has logged in.");
                return Result.failure();
            }

            startBandyerSDK(userId);

            BandyerSDK.getInstance().handleNotification(payload);
        } catch (Throwable e) {
            e.printStackTrace();
            return Result.failure();
        }

        return Result.success();
    }

    private void startBandyerSDK(String userId) {
        AccessTokenProvider accessTokenProvider = (userId1, completion) -> MultiDexApplication.getRestApi().getAccessToken(userId, accessToken -> {
            completion.success(accessToken);
            return null;
        }, exception -> {
            completion.error(exception);
            return null;
        });

        Session session = new Session(
                userId,
                accessTokenProvider,
                sessionObserver);

        BandyerSDK.getInstance().connect(
                session,
                errorReason -> Log.e(TAG, "Unable to connect BandyerSDK with error: " + errorReason)
        );
    }
}

