/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import android.content.Context;
import android.widget.Toast;

import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.bandyer.demo_android_sdk.utils.storage.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

/**
 * This class is for Bandyer usage only
 * You should implement your own logic for notification handling
 *
 * @author kristiyan
 */
public class FirebaseCompat {

    public static void unregisterDevice(Context context, String loggedUser) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            Thread post = new Thread(() -> {
                String devicePushToken = instanceIdResult.getToken();
                MockedNetwork.unregisterDeviceForPushNotification(context, loggedUser, devicePushToken);
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            post.start();
        });
    }

    public static void registerDevice(Context context) {
        if (!LoginManager.isUserLogged(context)) return;
        refreshConfiguration(context, () -> {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                String devicePushToken = instanceIdResult.getToken();
                MockedNetwork.registerDeviceForPushNotification(context, LoginManager.getLoggedUser(context), devicePushToken);
            }).addOnFailureListener(error -> {
                Toast.makeText(context, "Wrong configuration for FCM.\nYou will not receive any notifications!", Toast.LENGTH_LONG).show();
            });
        });
    }

    /**
     * This is necessary to change the projectId at runtime
     * In normal use-cases you shall not need to refresh the configuration as it will use the google-services.json file.
     */
    private static void refreshConfiguration(Context context, Runnable onComplete) {
        Thread post = new Thread(() -> {
            try {
                FirebaseApp.getInstance().delete();
                String projectId = ConfigurationPrefsManager.getFirebaseProjectNumber(context);
                FirebaseApp app = FirebaseApp.initializeApp(context, new FirebaseOptions.Builder()
                        .setGcmSenderId(projectId)
                        .setProjectId(projectId)
                        .setApplicationId(context.getPackageName())
                        .build());
                FirebaseInstanceId.getInstance(app).getInstanceId();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            onComplete.run();
        });
        post.start();
    }
}