/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import android.content.Context;

import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.bandyer.demo_android_sdk.utils.storage.LoginManager;
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
        // Get firebase token using the google-services.json
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String devicePushToken = instanceIdResult.getToken();
            MockedNetwork.unregisterDeviceForPushNotification(context, loggedUser, devicePushToken);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // if google-services.json has an INVALID configuration
        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(e -> {
            Thread post = new Thread(() -> {
                String devicePushToken;
                try {
                    devicePushToken = FirebaseInstanceId.getInstance().getToken(ConfigurationPrefsManager.getFirebaseProjectNumber(context), "FCM");
                    FirebaseInstanceId.getInstance().deleteToken(ConfigurationPrefsManager.getFirebaseProjectNumber(context), "FCM");
                    MockedNetwork.unregisterDeviceForPushNotification(context, loggedUser, devicePushToken);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
            post.start();
        });
    }

    public static void registerDevice(Context context) {
        if (!LoginManager.isUserLogged(context)) return;

        // Get firebase token using the google-services.json
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String devicePushToken = instanceIdResult.getToken();
            MockedNetwork.registerDeviceForPushNotification(context, LoginManager.getLoggedUser(context), devicePushToken);
        });

        // If google-services.json has an INVALID configuration
        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(e -> {
            Thread post = new Thread(() -> {
                if (!LoginManager.isUserLogged(context)) return;
                String devicePushToken;
                try {
                    devicePushToken = FirebaseInstanceId.getInstance().getToken(ConfigurationPrefsManager.getFirebaseProjectNumber(context), "FCM");
                    MockedNetwork.registerDeviceForPushNotification(context, LoginManager.getLoggedUser(context), devicePushToken);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
            post.start();
        });
    }
}