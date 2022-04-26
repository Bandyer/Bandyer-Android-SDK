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

import android.content.Context
import com.kaleyra.app_configuration.model.Configuration
import com.kaleyra.app_configuration.model.getMockConfiguration

/**
 * @author kristiyan
 */

object ConfigurationPrefsManager {

    const val CONFIGURATION_PREFS = "ConfigurationPrefs"

    /**
     * Configure preferences from Configuration object
     *
     * @param context Context
     * @param configuration Configuration to apply
     */
    fun configure(context: Context, configuration: Configuration) {
        val editor = context.applicationContext.getSharedPreferences(CONFIGURATION_PREFS, Context.MODE_PRIVATE).edit()
        editor.putString("configuration", configuration.toJson())
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
        if (jsonConfiguration != null) return Configuration.create(jsonConfiguration)
        return getMockConfiguration(context)
    }

    /**
     * Login user
     *
     * @param context
     */
    fun loginUser(context: Context, userId: String) {
        val config = getConfiguration(context)
        config.userId = userId
        configure(context, config)
    }

    /**
     * Logout user
     *
     * @param context
     */
    fun logoutUser(context: Context) {
        val config = getConfiguration(context)
        config.userId = null
        configure(context, config)
    }

    /**
     * Clears all configuration preferences
     *
     * @param context Context
     */
    fun clearAll(context: Context) {
        context.applicationContext.getSharedPreferences(CONFIGURATION_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        DesignPrefsManager.clearAll(context)
    }
}