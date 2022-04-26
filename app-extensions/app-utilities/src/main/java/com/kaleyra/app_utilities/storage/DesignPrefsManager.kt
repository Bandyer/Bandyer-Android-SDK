/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kaleyra.app_utilities.storage

import android.annotation.SuppressLint
import android.content.Context
import com.kaleyra.app_configuration.model.Configuration

object DesignPrefsManager {

    private const val DESIGN_PREFS = "KaleyraCollaborationSuiteUIPrefs"

    /**
     * Configure design preferences from Configuration object
     *
     * @param context Context
     * @param configuration Configuration
     */
    fun configure(context: Context, configuration: Configuration?) {
        configuration?.apply {
            setWatermarkText(context, logoName)
            setWatermarkUri(context, logoUrl)
            setCustomUserDetailsDisplayName(context, customUserDetailsName)
            setCustomUserDetailsImageUri(context, customUserDetailsImageUrl)
            setMockedUserDetailsMode(context, userDetailsProviderMode.name)
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
        context.getSharedPreferences(DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("call_watermark_image_uri", url).commit()
    }

    /**
     * Utility to set the watermark text to represent the branding
     *
     * @param context context
     * @param text new text to be used as watermark title
     */
    @SuppressLint("ApplySharedPref")
    private fun setWatermarkText(context: Context, text: String?) {
        context.getSharedPreferences(DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("call_watermark_text", text).commit()
    }

    /**
     * Utility to set the custom user details image uri
     *
     * @param context context
     * @param uri new uri to be used as custom user details logo
     */
    @SuppressLint("ApplySharedPref")
    private fun setCustomUserDetailsImageUri(context: Context, uri: String?) {
        context.getSharedPreferences(DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("custom_user_details_image_uri", uri).commit()
    }

    /**
     * Utility to set the custom user details display name text
     *
     * @param context context
     * @param text new text to be used as custom user details display name
     */
    @SuppressLint("ApplySharedPref")
    private fun setCustomUserDetailsDisplayName(context: Context, text: String?) {
        context.getSharedPreferences(DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("custom_user_details_display_name", text).commit()
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
        context.getSharedPreferences(DESIGN_PREFS, Context.MODE_PRIVATE).edit().putString("mock_user_details_mode", mode).commit()
    }

    /**
     * Clears all design preferences
     *
     * @param context Context
     */
    @SuppressLint("ApplySharedPref")
    fun clearAll(context: Context) {
        context.getSharedPreferences(DESIGN_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
    }
}