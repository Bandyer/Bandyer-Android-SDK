/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.storage

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.bandyer.android_sdk.BandyerSDK
import com.bandyer.app_configuration.R
import com.bandyer.app_configuration.external_configuration.model.CustomUserDetailsProvider
import com.bandyer.app_configuration.external_configuration.utils.MediaStorageUtils
import com.bandyer.app_configuration.external_configuration.model.Configuration
import com.bandyer.app_configuration.external_configuration.model.PushProvider
import com.bandyer.app_configuration.external_configuration.model.getMockConfiguration
import com.bandyer.app_utilities.BuildConfig
import com.google.gson.Gson

/**
 * @author kristiyan
 */

object ConfigurationPrefsManager {

    private val gson: Gson by lazy {
        Gson()
    }

    const val CONFIGURATION_PREFS = "ConfigurationPrefs"
    private val legacyPrefsManager = LegacyConfigurationPrefsManager
    private val legacyDefaultCallSettingsManager = DefaultCallSettingsManager

    /**
     * Configure preferences from Configuration object
     *
     * @param context Context
     * @param configuration Configuration to apply
     */
    fun configure(context: Context, configuration: Configuration) {
        val jsonConfiguration = gson.toJson(configuration, Configuration::class.java)
        val editor = context.applicationContext.getSharedPreferences(CONFIGURATION_PREFS, Context.MODE_PRIVATE).edit()
        editor.putString("configuration", jsonConfiguration)
        editor.commit()
        DesignPrefsManager.configure(context, configuration)
    }

    /**
     * Retrieve current app Configuration object
     *
     * @param context Context
     * @return Configuration?
     */
    fun getConfiguration(context: Context): Configuration {
        val editor = context.applicationContext.getSharedPreferences(CONFIGURATION_PREFS, Context.MODE_PRIVATE)
        val jsonConfiguration = editor.getString("configuration", null)
        if (jsonConfiguration != null) return gson.fromJson(jsonConfiguration, Configuration::class.java)
        val legacyConfiguration = getLegacyConfiguration(context)
        if (legacyConfiguration != null) return legacyConfiguration
        return getMockConfiguration(context)
    }

    @SuppressLint("ApplySharedPref")
    private fun getLegacyConfiguration(context: Context): Configuration? {
        if (LegacyConfigurationPrefsManager.areCredentialsMockedOrEmpty(context)) return null
        val migrationConfiguration = Configuration(
                LegacyConfigurationPrefsManager.getEnvironmentName(context),
                LoginManager.getLoggedUser(context),
                LegacyConfigurationPrefsManager.getAppId(context),
                LegacyConfigurationPrefsManager.getApiKey(context),
                LegacyConfigurationPrefsManager.getFirebaseProjectNumber(context),
                PushProvider.valueOf(LegacyConfigurationPrefsManager.getPushProvider(context)),
                LegacyConfigurationPrefsManager.getWatermarkUri(context).toString(),
                LegacyConfigurationPrefsManager.getWatermarkText(context),
                LegacyConfigurationPrefsManager.getCustomUserDetailsDisplayName(context),
                LegacyConfigurationPrefsManager.getCustomUserDetailsImageUri(context).toString(),
                CustomUserDetailsProvider.valueOf(LegacyConfigurationPrefsManager.getMockedUserDetailsMode(context)),
                LegacyConfigurationPrefsManager.isLeakCanaryEnabled(context),
                CustomUserDetailsProvider.valueOf(LegacyConfigurationPrefsManager.getMockedUserDetailsMode(context)) == CustomUserDetailsProvider.RANDOM,
                LegacyConfigurationPrefsManager.isSimplifiedVersionEnabled(context),
                DefaultCallSettingsManager.getDefaultCallType(context),
                DefaultCallSettingsManager.isWhiteboardEnabled(context),
                DefaultCallSettingsManager.isFileSharingEnabled(context),
                DefaultCallSettingsManager.isChatEnabled(context),
                DefaultCallSettingsManager.isScreenSharingEnabled(context),
                DefaultCallSettingsManager.isCallRecordingEnabled(context),
                DefaultCallSettingsManager.isBackCameraAsDefaultEnabled(context),
                DefaultCallSettingsManager.isProximitySensorDisabled(context),
                LegacyConfigurationPrefsManager.isMockUserAuthenticationRequest(context),
                LegacyConfigurationPrefsManager.getFirebaseProjectId(context),
                LegacyConfigurationPrefsManager.getFirebaseProjectMobileAppId(context),
                LegacyConfigurationPrefsManager.getFirebaseApiKey(context))

        LegacyConfigurationPrefsManager.clearAll(context)
        DefaultCallSettingsManager.clearAll(context)

        configure(context, migrationConfiguration)
        return migrationConfiguration
    }

    /**
     * Clears all configuration preferences
     *
     * @param context Context
     */
    fun clearAll(context: Context) {
        context.applicationContext.getSharedPreferences(CONFIGURATION_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        LegacyConfigurationPrefsManager.clearAll(context)
        DefaultCallSettingsManager.clearAll(context)
        DesignPrefsManager.clearAll(context)
    }
}

@Deprecated("LegacyConfigurationPrefsManager will be deprecated in favor of new ConfigurationPrefsManager")
internal object LegacyConfigurationPrefsManager {

    const val MY_CREDENTIAL_PREFS_NAME = "myCredentialPrefs"

    /**
     * Utility to check if credentials are mocked or empty
     *
     * @param context Activity or App
     * @return true if apiKey, appId or projectNumber is mocked or empty
     */
    @JvmStatic
    fun areCredentialsMockedOrEmpty(context: Context): Boolean {
        val appId = getAppId(context)
        val apiKey = getApiKey(context)
        val firebaseProjectNumber = getFirebaseProjectNumber(context)
        return apiKey!!.isEmpty() || apiKey == "ak_xxx" ||
                appId!!.isEmpty() || appId == "mAppId_xxx" || firebaseProjectNumber == "1035469437089"
    }

    /**
     * Utility to set apiKey
     *
     * @param context App or Activity
     * @param apiKey  the apiKey
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setApiKey(context: Context, apiKey: String?) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("apiKey", apiKey)
        editor.commit()
    }

    /**
     * Utility to set appId
     *
     * @param context App or Activity
     * @param appId   the appId
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setAppId(context: Context, appId: String?) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("appId", appId)
        editor.commit()
    }

    /**
     * Utility to set environment name
     *
     * @param context App or Activity
     * @param envName the environment name
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setEnvironmentName(context: Context, envName: String?) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("environment", envName)
        editor.commit()
    }

    /**
     * Utility to set push notification provider
     *
     * @param context      App or Activity
     * @param pushProvider the provider name
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setPushProvider(context: Context, pushProvider: PushProvider?) {
        pushProvider ?: return
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("pushProvider", pushProvider.name)
        editor.commit()
    }

    /**
     * Utility to set firebase project number
     *
     * @param context               App or Activity
     * @param firebaseProjectNumber the firebase project number
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setFirebaseProjectNumber(context: Context, firebaseProjectNumber: String?) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("projectNumber", firebaseProjectNumber)
        editor.commit()
    }

    /**
     * Utility to set firebase project id
     *
     * @param context               App or Activity
     * @param firebaseProjectId the firebase project id
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setFirebaseProjectId(context: Context, firebaseProjectId: String?) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("firebaseProjectId", firebaseProjectId)
        editor.commit()
    }

    /**
     * Utility to set firebase mobile app id
     *
     * @param context               App or Activity
     * @param firebaseMobileAppId the firebase mobile app id
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setFirebaseProjectMobileAppId(context: Context, firebaseMobileAppId: String?) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("firebaseMobileAppId", firebaseMobileAppId)
        editor.commit()
    }

    /**
     * Utility to set firebase api key
     *
     * @param context               App or Activity
     * @param firebaseApiKey the firebase api key
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setFirebaseProjectApiKey(context: Context, firebaseApiKey: String?) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("firebaseApiKey", firebaseApiKey)
        editor.commit()
    }

    /**
     * Utility to return the apiKey
     *
     * @param context Activity or App
     * @return apiKey
     */
    @JvmStatic
    fun getApiKey(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("apiKey", context.getString(R.string.api_key))!!
    }

    /**
     * Utility to return the appId
     *
     * @param context Activity or App
     * @return appId
     */
    @JvmStatic
    fun getAppId(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("appId", context.getString(R.string.app_id))!!
    }

    /**
     * Utility to return the firebase project number
     *
     * @param context Activity or App
     * @return firebase project number
     */
    @JvmStatic
    fun getFirebaseProjectNumber(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("projectNumber", context.getString(R.string.project_number))!!
    }

    /**
     * Utility to return the firebase project id
     *
     * @param context Activity or App
     * @return firebase project id
     */
    @JvmStatic
    fun getFirebaseProjectId(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("firebaseProjectId", context.getString(R.string.firebase_project_id))!!
    }

    /**
     * Utility to return the firebase mobile app id
     *
     * @param context Activity or App
     * @return firebase mobile app id
     */
    @JvmStatic
    fun getFirebaseProjectMobileAppId(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("firebaseMobileAppId", context.getString(R.string.firebase_mobile_app_id))!!
    }

    /**
     * Utility to return the firebase api key
     *
     * @param context Activity or App
     * @return firebase api key
     */
    @JvmStatic
    fun getFirebaseApiKey(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("firebaseApiKey", context.getString(R.string.firebase_api_key))!!
    }

    /**
     * Utility to return the pushProvider
     *
     * @param context Activity or App
     * @return firebase project number
     */
    @JvmStatic
    fun getPushProvider(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("pushProvider", context.getString(R.string.push_provider))!!
    }

    /**
     * Utility to check if credentials are mocked
     *
     * @param context Activity or App
     * @return true if apiKey or appId is mocked
     */
    @JvmStatic
    fun hasMockCredentials(context: Context): Boolean {
        return getApiKey(context) == "ak_xxx" || getAppId(context) == "mAppId_xxx"
    }

    /**
     * Utility to retrieve leak canary default setting.
     *
     * @param context Activity or App
     * @return true if leak canary should be enabled by default, false otherwise
     */
    fun isLeakCanaryEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("isLeakCanaryEnabled", BuildConfig.USE_LEAK_CANARY)
    }

    /**
     * Utility to set leak canary default setting.
     *
     * @param context Activity or App
     * @param enabled true if leak canary should be enabled by default, false otherwise
     */
    @SuppressLint("ApplySharedPref")
    fun setLeakCanaryEnabled(context: Context, enabled: Boolean) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean("isLeakCanaryEnabled", enabled)
        editor.commit()
    }

    /**
     * Utility to retrieve mock user details provider default setting.
     *
     * @param context Activity or App
     * @return true if mock user details provider should be enabled by default, false otherwise
     */
    @JvmStatic
    fun isMockUserDetailsProviderEnabled(context: Context): Boolean {
        val mockedUserDetailsProviderMode = getMockedUserDetailsMode(context)
        return mockedUserDetailsProviderMode == context.resources.getString(R.string.mock_user_details_config_random) || mockedUserDetailsProviderMode == context.resources.getString(R.string.mock_user_details_config_custom)
    }

    /**
     * Utility to return the environment
     *
     * @param context context
     * @return environment
     */
    @JvmStatic
    fun getEnvironmentName(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("environment", "sandbox")!!
    }

    /**
     * Clear all preferences
     *
     * @param context context
     */
    @SuppressLint("ApplySharedPref")
    fun clear(context: Context) {
        context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
    }

    /**
     * Set simplified version for demo app
     * Enables direct call and chat based on default settings
     *
     * @param context context
     * @param enabled true if enabled false otherwise
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setSimplifiedVersionEnabled(context: Context, enabled: Boolean) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean("use_simplified_version", enabled)
        editor.commit()
    }

    /**
     * Check if demo app is in currently simplified version
     *
     * @param context context
     * @return true if simplified version is enabled, false otherwise
     */
    @JvmStatic
    fun isSimplifiedVersionEnabled(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("use_simplified_version", BuildConfig.USE_SIMPLIFIED_VERSION)
    }

    /**
     * Set mock user authentication request.
     * Let the demo app ask for mocked biometric authentication that will be forwarded to other call participants.
     * @param context context
     * @param enabled true if enabled false otherwise
     */
    @JvmStatic
    fun setMockUserAuthenticationRequest(context: Context, enabled: Boolean) {
        val editor = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean("mock_user_authentication_request", enabled)
        editor.commit()
    }

    /**
     * Check if demo app has enabled mocked user authentication request.
     * @param context context
     * @return true if enabled, false otherwise
     */
    @JvmStatic
    fun isMockUserAuthenticationRequest(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("mock_user_authentication_request", false)
    }

    /**
     * Utility to get watermarkUri if set, otherwise will return the default logo asset
     *
     * @param context context
     * @return uri
     */
    fun getWatermarkUri(context: Context): Uri {
        val defaultUrl = "android.resource://" + context.packageName + "/drawable/logo"
        val url = context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).getString("call_watermark_image_uri", defaultUrl)
        return MediaStorageUtils.getUriFromString(url)!!
    }

    /**
     * Utility to get watermark text if set, otherwise will return Bandyer
     *
     * @param context context
     * @return text representing the watermark
     */
    fun getWatermarkText(context: Context): String {
        return context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).getString("call_watermark_text", "Bandyer")!!
    }

    /**
     * Utility to set the watermark uri for the logo
     *
     * @param context context
     * @param url new url to be used as watermark logo
     */
    @SuppressLint("ApplySharedPref")
    fun setWatermarkUri(context: Context, url: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("call_watermark_image_uri", url).commit()
    }

    /**
     * Utility to set the watermark text to represent the branding
     *
     * @param context context
     * @param text new text to be used as watermark title
     */
    @SuppressLint("ApplySharedPref")
    fun setWatermarkText(context: Context, text: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("call_watermark_text", text).commit()
    }

    /**
     * Utility to get custom user details image uri if set
     *
     * @param context context
     * @return uri
     */
    @JvmStatic
    fun getCustomUserDetailsImageUri(context: Context): Uri {
        val url = context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).getString("custom_user_details_image_uri", "")
        return MediaStorageUtils.getUriFromString(url)!!
    }

    /**
     * Utility to get custom user details display name text if set, otherwise will return Bandyer
     *
     * @param context context
     * @return text representing the custom user details display name
     */
    @JvmStatic
    fun getCustomUserDetailsDisplayName(context: Context): String {
        return context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).getString("custom_user_details_display_name", "")!!
    }

    /**
     * Utility to set the custom user details image uri
     *
     * @param context context
     * @param uri new uri to be used as custom user details logo
     */
    @SuppressLint("ApplySharedPref")
    fun setCustomUserDetailsImageUri(context: Context, uri: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("custom_user_details_image_uri", uri).commit()
    }

    /**
     * Utility to set the custom user details display name text
     *
     * @param context context
     * @param text new text to be used as custom user details display name
     */
    @SuppressLint("ApplySharedPref")
    fun setCustomUserDetailsDisplayName(context: Context, text: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("custom_user_details_display_name", text).commit()
    }

    /**
     * Utility to get mock user details mode
     *
     * @param context context
     * @return text representing the mock user details mode, NONE, RANDOM or CUSTOM
     */
    @JvmStatic
    fun getMockedUserDetailsMode(context: Context): String {
        return context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).getString("mock_user_details_mode", "NONE")!!
    }

    /**
     * Utility to set mock user details mode
     *
     * @param context context
     * @param mode new mock user details mode
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setMockedUserDetailsMode(context: Context, mode: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("mock_user_details_mode", mode).commit()
    }

    /**
     * Clears all preferences
     *
     * @param context Context
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun clearAll(context: Context) {
        context.getSharedPreferences(MY_CREDENTIAL_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }
}