/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;

import com.bandyer.demo_android_sdk.BuildConfig;
import com.bandyer.demo_android_sdk.R;

import static android.content.Context.MODE_PRIVATE;
import static com.bandyer.demo_android_sdk.notification.NotificationProxy.FCM_PROVIDER;

/**
 * @author kristiyan
 */
public class ConfigurationPrefsManager {

    public final static String MY_CREDENTIAL_PREFS_NAME = "myCredentialPrefs";

    /**
     * Utility to set apiKey
     *
     * @param context App or Activity
     * @param apiKey  the apiKey
     */
    @SuppressLint("ApplySharedPref")
    public static void setApiKey(Context context, String apiKey) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("apiKey", apiKey);
        editor.commit();
    }

    /**
     * Utility to set appId
     *
     * @param context App or Activity
     * @param appId   the appId
     */
    @SuppressLint("ApplySharedPref")
    public static void setAppId(Context context, String appId) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("appId", appId);
        editor.commit();
    }

    /**
     * Utility to set environment name
     *
     * @param context App or Activity
     * @param envName the environment name
     */
    @SuppressLint("ApplySharedPref")
    public static void setEnvironmentName(Context context, String envName) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("environment", envName);
        editor.commit();
    }

    /**
     * Utility to set push notification provider
     *
     * @param context      App or Activity
     * @param pushProvider the provider name
     */
    @SuppressLint("ApplySharedPref")
    public static void setPushProvider(Context context, String pushProvider) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("pushProvider", pushProvider);
        editor.commit();
    }

    /**
     * Utility to set firebase project number
     *
     * @param context               App or Activity
     * @param firebaseProjectNumber the firebase project number
     */
    @SuppressLint("ApplySharedPref")
    public static void setFirebaseProjectNumber(Context context, String firebaseProjectNumber) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("projectNumber", firebaseProjectNumber);
        editor.commit();
    }

    /**
     * Utility to return the apiKey
     *
     * @param context Activity or App
     * @return apiKey
     */
    public static String getApiKey(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("apiKey", context.getString(R.string.api_key));
    }

    /**
     * Utility to return the appId
     *
     * @param context Activity or App
     * @return appId
     */
    public static String getAppId(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("appId", context.getString(R.string.app_id));
    }

    /**
     * Utility to return the firebase project number
     *
     * @param context Activity or App
     * @return firebase project number
     */
    public static String getFirebaseProjectNumber(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("projectNumber", context.getString(R.string.project_number));
    }

    /**
     * Utility to return the pushProvider
     *
     * @param context Activity or App
     * @return firebase project number
     */
    public static String getPushProvider(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("pushProvider", FCM_PROVIDER);
    }


    /**
     * Utility to check if credentials are mocked
     *
     * @param context Activity or App
     * @return true if apiKey or appId is mocked
     */
    public static Boolean hasMockCredentials(Context context) {
        return ConfigurationPrefsManager.getApiKey(context).equals("ak_xxx") || ConfigurationPrefsManager.getAppId(context).equals("mAppId_xxx");
    }

    /**
     * Utility to retrieve leak canary default setting.
     * @param context Activity or App
     * @return true if leak canary should be enabled by default, false otherwise
     */
    public static Boolean isLeakCanaryEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean("isLeakCanaryEnabled", BuildConfig.USE_LEAK_CANARY);
    }

    /**
     * Utility to set leak canary default setting.
     * @param context Activity or App
     * @param enabled true if leak canary should be enabled by default, false otherwise
     */
    @SuppressLint("ApplySharedPref")
    public static void setLeakCanaryEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("isLeakCanaryEnabled", enabled);
        editor.commit();
    }

    /**
     * Utility to check if credentials are mocked or empty
     *
     * @param context Activity or App
     * @return true if apiKey, appId or projectNumber is mocked or empty
     */
    public static Boolean areCredentialsMockedOrEmpty(Context context) {
        String appId = ConfigurationPrefsManager.getAppId(context);
        String apiKey = ConfigurationPrefsManager.getApiKey(context);
        String firebaseProjectNumber = ConfigurationPrefsManager.getFirebaseProjectNumber(context);
        return apiKey.isEmpty() ||
                apiKey.equals("ak_xxx") ||
                appId.isEmpty() ||
                appId.equals("mAppId_xxx") ||
                firebaseProjectNumber.equals("1035469437089");
    }

    /**
     * Utility to return the environment
     *
     * @param context context
     * @return environment
     */
    public static String getEnvironmentName(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("environment", "sandbox");
    }

    /**
     * Clear all preferences
     *
     * @param context context
     */
    @SuppressLint("ApplySharedPref")
    public static void clear(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    /**
     * Set simplified version for demo app
     * Enables direct call and chat based on default settings
     * @param context context
     * @param enabled true if enabled false otherwise
     */
    public static void setSimplifiedVersionEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("use_simplified_version", enabled);
        editor.commit();
    }

    /**
     * Check if demo app is in currently simplified version
     * @param context context
     * @return true if simplified version is enabled, false otherwise
     */
    public static Boolean isSimplifiedVersionEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean("use_simplified_version", BuildConfig.USE_SIMPLIFIED_VERSION);
    }
}
