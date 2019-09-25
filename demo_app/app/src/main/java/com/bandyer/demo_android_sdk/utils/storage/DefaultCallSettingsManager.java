package com.bandyer.demo_android_sdk.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.custom_views.CallOptionsDialog;

import static android.content.Context.MODE_PRIVATE;

public class DefaultCallSettingsManager {

    public final static String CALL_OPTIONS_PREFS_NAME = "callOptionsPrefs";

    /**
     * Utility to get default call type from preferences.
     * @param context Activity or App
     * @return CallOptionsDialog.CallOptionsType default call type.
     */
    public static CallOptionsDialog.CallOptionsType getDefaultCallType(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return CallOptionsDialog.CallOptionsType.valueOf(prefs.getString(context.getString(R.string.call_type), context.getString(R.string.call_type_audio_video)));
    }

    /**
     * Utility to return if the whiteboard capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isWiteboardEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.whiteboard_requires_api_19), true);
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
     * Utility to return if the proximity sensor if disabled by default option is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    public static Boolean isProximitySensorDisabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(CALL_OPTIONS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.disable_proximity_sensor), false);
    }
}
