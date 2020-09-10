package com.bandyer.app_utilities.storage

import android.annotation.SuppressLint
import android.content.Context
import com.bandyer.android_sdk.BandyerSDK
import com.bandyer.app_configuration.external_configuration.model.Configuration

object DesignPrefsManager {

    /**
     * Configure design preferences from Configuration object
     *
     * @param context Context
     * @param configuration Configuration
     */
    fun configure(context: Context, configuration: Configuration) {
        configuration.apply {
            setWatermarkText(context, logoName)
            setWatermarkUri(context, logoUrl)
            setCustomUserDetailsDisplayName(context, customUserDetailsName)
            setCustomUserDetailsImageUri(context, customUserDetailsImageUrl)
            setMockedUserDetailsMode(context, customUserDetailsProvider?.name)
        }
    }

    /**
     * Utility to set the watermark uri for the logo
     *
     * @param context context
     * @param url new url to be used as watermark logo
     */
    @SuppressLint("ApplySharedPref")
    private fun setWatermarkUri(context: Context, url: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("call_watermark_image_uri", url).commit()
    }

    /**
     * Utility to set the watermark text to represent the branding
     *
     * @param context context
     * @param text new text to be used as watermark title
     */
    @SuppressLint("ApplySharedPref")
    private fun setWatermarkText(context: Context, text: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("call_watermark_text", text).commit()
    }

    /**
     * Utility to set the custom user details image uri
     *
     * @param context context
     * @param uri new uri to be used as custom user details logo
     */
    @SuppressLint("ApplySharedPref")
    private fun setCustomUserDetailsImageUri(context: Context, uri: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("custom_user_details_image_uri", uri).commit()
    }

    /**
     * Utility to set the custom user details display name text
     *
     * @param context context
     * @param text new text to be used as custom user details display name
     */
    @SuppressLint("ApplySharedPref")
    private fun setCustomUserDetailsDisplayName(context: Context, text: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("custom_user_details_display_name", text).commit()
    }

    /**
     * Utility to set mock user details mode
     *
     * @param context context
     * @param mode new mock user details mode
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    private fun setMockedUserDetailsMode(context: Context, mode: String?) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("mock_user_details_mode", mode).commit()
    }

    /**
     * Clears all design preferences
     *
     * @param context Context
     */
    @SuppressLint("ApplySharedPref")
    fun clearAll(context: Context) {
        context.getSharedPreferences(BandyerSDK.DESIGN_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
    }
}