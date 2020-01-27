package com.bandyer.demo_android_sdk.utils.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.custom_views.CallOptionsDialog;

import static android.content.Context.MODE_PRIVATE;

public class DefaultCallSettingsManager {

    public final static String CALL_OPTIONS_PREFS_NAME = "callOptionsPrefs";

    /**
     * Utility to get default call type from preferences.
     *
     * @param context Activity or App
     * @return CallOptionsDialog.CallOptionsType default call type.
     */
    public static CallOptionsDialog.CallOptionsType getDefaultCallType(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return CallOptionsDialog.CallOptionsType.valueOf(prefs.getString(context.getString(R.string.call_type), context.getString(R.string.call_type_audio_video)));
    }

    /**
     * Utility to set default call type.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    public static void setDefaultCallType(Context context, String callType) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(context.getString(R.string.call_type), callType).commit();
    }

    /**
     * Utility to return if the whiteboard capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isWhiteboardEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.whiteboard_requires_api_19), true);
    }

    /**
     * Utility to set if the whiteboard capability is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    public static void setWhiteboardEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(context.getString(R.string.whiteboard_requires_api_19), enabled).commit();
    }

    /**
     * Utility to return if the file sharing capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isFileSharingEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.file_sharing), true);
    }

    /**
     * Utility to set if the file sharing capability is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    public static void setFileSharingEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(context.getString(R.string.file_sharing), enabled).commit();
    }

    /**
     * Utility to return if the chat capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isChatEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.chat_requires_api_19), true);
    }

    /**
     * Utility to set if the chat capability is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    public static void setChatEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(context.getString(R.string.chat_requires_api_19), enabled).commit();
    }

    /**
     * Utility to return if the screen sharing capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isScreenSharingEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.screen_sharing_requires_api_21), true);
    }

    /**
     * Utility to set if the screen sharing capability is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    public static void setScreenSharingEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(context.getString(R.string.screen_sharing_requires_api_21), enabled).commit();
    }

    /**
     * Utility to return if the call recording option is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isCallRecordingEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.call_recording), false);
    }

    /**
     * Utility to return if the call recording option is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    public static void setCallRecordingEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(context.getString(R.string.call_recording), enabled).commit();
    }

    /**
     * Utility to return if the back camera as default option is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isBackCameraAsDefaultEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.back_camera_as_default_if_available), false);
    }

    /**
     * Utility to return if the back camera as default option is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    public static void setBackCameraAsDefaultEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(context.getString(R.string.back_camera_as_default_if_available), enabled).commit();
    }

    /**
     * Utility to return if the proximity sensor if disabled by default option is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isProximitySensorDisabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.disable_proximity_sensor), false);
    }

    /**
     * Utility to set if the proximity sensor if disabled by default option is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    public static void setProximitySensorDisabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(context.getString(R.string.disable_proximity_sensor), enabled).commit();
    }
}
