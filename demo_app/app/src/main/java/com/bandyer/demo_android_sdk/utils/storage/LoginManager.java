/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.bandyer.demo_android_sdk.notification.NotificationProxy;
import com.bandyer.demo_android_sdk.utils.activities.BaseActivity;
import com.crashlytics.android.Crashlytics;

import static android.content.Context.MODE_PRIVATE;

/**
 * Utility used to remember the logged user, identified by the userAlias
 *
 * @author kristiyan
 */
public class LoginManager {

    private final static String MY_PREFS_NAME = "myPrefs";


    /**
     * Utility to log a user in the application
     *
     * @param context   App or Activity
     * @param userAlias the user identifier to remember
     */
    public static void login(Context context, String userAlias) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("userAlias", userAlias);
        editor.apply();

        // Set userAlias on Crashlytics to identify the user in crash logs.
        try {
            Crashlytics.setUserIdentifier(userAlias);
        } catch (Exception ignored) {
        }

        // Register device for receive push notifications
        // It will not work for you, you should implement your own server for notification send/receive logics
        NotificationProxy.registerDevice(context);
    }

    /**
     * Utility to return the logged user
     *
     * @param context Activity or App
     * @return userAlias
     */
    public static String getLoggedUser(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("userAlias", "");
    }

    /**
     * Utility to check whenever the use has been logged or not
     *
     * @param context App or Activity
     * @return true if the user is logged, false otherwise
     */
    public static boolean isUserLogged(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return !prefs.getString("userAlias", "").equals("");
    }

    /**
     * Utility to logout the user from the application
     *
     * @param context BaseActivity
     */
    public static void logout(BaseActivity context) {

        // unregister device for push notifications
        // It will not work for you, you should implement your own server for notification send/receive logics
        NotificationProxy.unregisterDevice(context, getLoggedUser(context));

        // Remove userAlias on Crashlytics to identify the user in crash logs.
        try {
            Crashlytics.setUserIdentifier(null);
        } catch (Exception ignored) {
        }

        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}