/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bandyer.demo_android_sdk.BaseActivity;

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
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("userAlias", userAlias);
        editor.apply();
    }

    /**
     * Utility to return the logged user
     *
     * @param context Activity or App
     * @return userAlias
     */
    public static String getLoggedUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("userAlias", "");
    }

    /**
     * Utility to check whenever the use has been logged or not
     *
     * @param context App or Activity
     * @return true if the user is logged, false otherwise
     */
    public static boolean isUserLogged(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return !prefs.getString("userAlias", "").equals("");
    }

    /**
     * Utility to logout the user from the application
     *
     * @param context BaseActivity
     */
    public static void logout(BaseActivity context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}