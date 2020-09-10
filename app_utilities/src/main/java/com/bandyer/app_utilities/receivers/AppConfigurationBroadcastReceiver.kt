package com.bandyer.app_utilities.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bandyer.app_configuration.external_configuration.activities.BaseConfigurationActivity.Companion.CONFIGURATION_ACTION_UPDATE
import com.bandyer.app_configuration.external_configuration.activities.BaseConfigurationActivity.Companion.CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE
import com.bandyer.app_configuration.external_configuration.activities.ConfigurationActivity.Companion.CONFIGURATION_RESULT
import com.bandyer.app_configuration.external_configuration.model.Configuration
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager
import com.bandyer.app_utilities.storage.LoginManager
import com.jakewharton.processphoenix.ProcessPhoenix

class AppConfigurationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != CONFIGURATION_ACTION_UPDATE && intent.action != CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE) return

        val configuration =
                intent.getParcelableExtra<Configuration>(CONFIGURATION_RESULT)
                        ?: return

        ConfigurationPrefsManager.configure(context, configuration)

        when (intent.action) {
            CONFIGURATION_ACTION_UPDATE -> {
                LoginManager.login(context, configuration.userAlias)
                ProcessPhoenix.triggerRebirth(context)
            }
            CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE -> {
                // just save configuration
            }
        }

    }
}
