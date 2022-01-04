/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_utilities.storage

import android.annotation.SuppressLint
import android.content.Context
import com.bandyer.app_configuration.R
import com.bandyer.app_configuration.external_configuration.model.CallOptionsType

object DefaultCallSettingsManager {

    const val CALL_OPTIONS_PREFS_NAME = "callOptionsPrefs"

    /**
     * Utility to get default call type from preferences.
     *
     * @param context Activity or App
     * @return CallOptionsDialog.CallOptionsType default call type.
     */
    fun getDefaultCallType(context: Context): CallOptionsType {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return CallOptionsType.valueOf(prefs.getString(context.getString(R.string.call_type), CallOptionsType.AUDIO_VIDEO.name)!!)
    }

    /**
     * Utility to set default call type.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setDefaultCallType(context: Context, callType: CallOptionsType) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(context.getString(R.string.call_type), callType.name).commit()
    }

    /**
     * Utility to return if the whiteboard capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isWhiteboardEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(context.getString(R.string.whiteboard_requires_api_19), true)
    }

    /**
     * Utility to set if the whiteboard capability is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setWhiteboardEnabled(context: Context, enabled: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.whiteboard_requires_api_19), enabled).commit()
    }

    /**
     * Utility to return if the file sharing capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isFileSharingEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(context.getString(R.string.file_sharing), true)
    }

    /**
     * Utility to set if the file sharing capability is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setFileSharingEnabled(context: Context, enabled: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.file_sharing), enabled).commit()
    }

    /**
     * Utility to return if the chat capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isChatEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(context.getString(R.string.chat_requires_api_19), true)
    }

    /**
     * Utility to set if the chat capability is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setChatEnabled(context: Context, enabled: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.chat_requires_api_19), enabled).commit()
    }

    /**
     * Utility to return if the screen sharing capability is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isScreenSharingEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(context.getString(R.string.screen_sharing_requires_api_21), true)
    }

    /**
     * Utility to set if the screen sharing capability is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setScreenSharingEnabled(context: Context, enabled: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.screen_sharing_requires_api_21), enabled).commit()
    }

    /**
     * Utility to return if the call recording option is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isCallRecordingEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(context.getString(R.string.call_recording), false)
    }

    /**
     * Utility to return if the call recording option is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setCallRecordingEnabled(context: Context, enabled: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.call_recording), enabled).commit()
    }

    /**
     * Utility to return if the back camera as default option is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isBackCameraAsDefaultEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(context.getString(R.string.back_camera_as_default_if_available), false)
    }

    /**
     * Utility to return if the call feedback displaying is requested on call end.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isCallFeedbackEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(context.getString(R.string.display_feedback_ui), false)
    }

    /**
     * Utility to return if the back camera as default option is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setBackCameraAsDefaultEnabled(context: Context, enabled: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.back_camera_as_default_if_available), enabled).commit()
    }

    /**
     * Utility to return if the proximity sensor if disabled by default option is enabled by default.
     *
     * @param context Activity or App
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isProximitySensorDisabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(context.getString(R.string.disable_proximity_sensor), false)
    }

    /**
     * Utility to set if the proximity sensor if disabled by default option is enabled by default.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setProximitySensorDisabled(context: Context, enabled: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.disable_proximity_sensor), enabled).commit()
    }

    /**
     * Utility to set if the call feedback displaying is requested.
     *
     * @param context Activity or App
     */
    @SuppressLint("ApplySharedPref")
    fun setCallFeedbackEnabled(context: Context, enabled: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.display_feedback_ui), enabled).commit()
    }

    /**
     * Clears all preferences
     *
     * @param context Context
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun clearAll(context: Context) {
        context.getSharedPreferences(CALL_OPTIONS_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }
}