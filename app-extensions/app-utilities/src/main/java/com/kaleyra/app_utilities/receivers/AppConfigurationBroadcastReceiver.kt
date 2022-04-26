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

package com.kaleyra.app_utilities.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kaleyra.app_configuration.activities.BaseConfigurationActivity.Companion.CONFIGURATION_ACTION_UPDATE
import com.kaleyra.app_configuration.activities.BaseConfigurationActivity.Companion.CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE
import com.kaleyra.app_configuration.activities.ConfigurationActivity.Companion.CONFIGURATION_RESULT
import com.kaleyra.app_configuration.model.Configuration
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.app_utilities.storage.LoginManager
import com.jakewharton.processphoenix.ProcessPhoenix

class AppConfigurationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != CONFIGURATION_ACTION_UPDATE && intent.action != CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE) return

        val configuration = Configuration.create(intent.getStringExtra(CONFIGURATION_RESULT) ?: "")

        ConfigurationPrefsManager.configure(context, configuration)

        when (intent.action) {
            CONFIGURATION_ACTION_UPDATE               -> {
                configuration.userId?.let { LoginManager.login(context, it) }
                ProcessPhoenix.triggerRebirth(context)
            }
            CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE -> {
                // just save configuration
            }
        }

    }
}
