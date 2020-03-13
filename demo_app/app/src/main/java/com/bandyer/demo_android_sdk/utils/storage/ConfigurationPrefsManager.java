/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.bandyer.android_sdk.BandyerSDK;
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
     *
     * @param context Activity or App
     * @return true if leak canary should be enabled by default, false otherwise
     */
    public static Boolean isLeakCanaryEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean("isLeakCanaryEnabled", BuildConfig.USE_LEAK_CANARY);
    }

    /**
     * Utility to set leak canary default setting.
     *
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
     * Utility to retrieve mock user details provider default setting.
     *
     * @param context Activity or App
     * @return true if mock user details provider should be enabled by default, false otherwise
     */
    public static Boolean isMockUserDetailsProviderEnabled(Context context) {
        String mockedUserDetailsProviderMode = getMockedUserDetailsMode(context);
        return mockedUserDetailsProviderMode.equals(context.getResources().getString(R.string.mock_user_details_config_random)) ||
                mockedUserDetailsProviderMode.equals(context.getResources().getString(R.string.mock_user_details_config_custom));
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
        context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).edit().clear().commit();
    }

    /**
     * Set simplified version for demo app
     * Enables direct call and chat based on default settings
     *
     * @param context context
     * @param enabled true if enabled false otherwise
     */
    @SuppressLint("ApplySharedPref")
    public static void setSimplifiedVersionEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("use_simplified_version", enabled);
        editor.commit();
    }

    /**
     * Check if demo app is in currently simplified version
     *
     * @param context context
     * @return true if simplified version is enabled, false otherwise
     */
    public static Boolean isSimplifiedVersionEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean("use_simplified_version", BuildConfig.USE_SIMPLIFIED_VERSION);
    }

    /**
     * Set mock user authentication request.
     * Let the demo app ask for mocked biometric authentication that will be forwarded to other call participants.
     * @param context context
     * @param enabled true if enabled false otherwise
     */
    public static void setMockUserAuthenticationRequest(Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("mock_user_authentication_request", enabled);
        editor.commit();
    }

    /**
     * Check if demo app has enabled mocked user authentication request.
     * @param context context
     * @return true if enabled, false otherwise
     */
    public static Boolean isMockUserAuthenticationRequest(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean("mock_user_authentication_request", false);
    }

    /**
     * Utility to get watermarkUri if set, otherwise will return the default logo asset
     *
     * @param context context
     * @return uri
     */
    public static Uri getWatermarkUri(Context context) {
        String defaultUrl = "android.resource://" + context.getPackageName() + "/drawable/logo";
        String url = context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).getString("call_watermark_image_uri", defaultUrl);
        return MediaStorageUtils.getUriFromString(url);
    }

    /**
     * Utility to get watermark text if set, otherwise will return Bandyer
     *
     * @param context context
     * @return text representing the watermark
     */
    public static String getWatermarkText(Context context) {
        return context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).getString("call_watermark_text", "Bandyer");
    }

    /**
     * Utility to set the watermark uri for the logo
     *
     * @param context context
     * @param url new url to be used as watermark logo
     */
    @SuppressLint("ApplySharedPref")
    public static void setWatermarkUri(Context context, String url) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).edit().putString("call_watermark_image_uri", url).commit();
    }

    /**
     * Utility to set the watermark text to represent the branding
     *
     * @param context context
     * @param text new text to be used as watermark title
     */
    @SuppressLint("ApplySharedPref")
    public static void setWatermarkText(Context context, String text) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).edit().putString("call_watermark_text", text).commit();
    }

    /**
     * Utility to get custom user details image uri if set
     *
     * @param context context
     * @return uri
     */
    public static Uri getCustomUserDetailsImageUri(Context context) {
        String url = context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).getString("custom_user_details_image_uri", "");
        return MediaStorageUtils.getUriFromString(url);
    }

    /**
     * Utility to get custom user details display name text if set, otherwise will return Bandyer
     *
     * @param context context
     * @return text representing the custom user details display name
     */
    public static String getCustomUserDetailsDisplayName(Context context) {
        return context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).getString("custom_user_details_display_name", "");
    }

    /**
     * Utility to set the custom user details image uri
     *
     * @param context context
     * @param uri new uri to be used as custom user details logo
     */
    @SuppressLint("ApplySharedPref")
    public static void setCustomUserDetailsImageUri(Context context, String uri) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).edit().putString("custom_user_details_image_uri", uri).commit();
    }

    /**
     * Utility to set the custom user details display name text
     *
     * @param context context
     * @param text new text to be used as custom user details display name
     */
    @SuppressLint("ApplySharedPref")
    public static void setCustomUserDetailsDisplayName(Context context, String text) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).edit().putString("custom_user_details_display_name", text).commit();
    }

    /**
     * Utility to get mock user details mode
     *
     * @param context context
     * @return text representing the mock user details mode, NONE, RANDOM or CUSTOM
     */
    public static String getMockedUserDetailsMode(Context context) {
        return context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).getString("mock_user_details_mode", "NONE");
    }

    /**
     * Utility to set mock user details mode
     *
     * @param context context
     * @param mode new mock user details mode
     */
    @SuppressLint("ApplySharedPref")
    public static void setMockedUserDetailsMode(Context context, String mode) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, MODE_PRIVATE).edit().putString("mock_user_details_mode", mode).commit();
    }
}
