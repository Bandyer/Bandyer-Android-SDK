/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_utilities.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.bandyer.app_configuration.external_configuration.activities.BaseConfigurationActivity
import com.bandyer.app_configuration.external_configuration.activities.ConfigurationActivity
import com.bandyer.app_configuration.external_configuration.activities.ScrollAwareToolbarActivity
import com.bandyer.app_configuration.external_configuration.model.Configuration
import com.bandyer.app_configuration.external_configuration.model.ConfigurationFieldChangeListener
import com.bandyer.app_configuration.external_configuration.model.bindToConfigurationProperty
import com.bandyer.app_utilities.R
import com.bandyer.app_utilities.utils.FingerprintUtils
import com.bandyer.app_utilities.utils.Utils
import kotlinx.android.synthetic.main.activity_default_call_settings.*

@Suppress("UNCHECKED_CAST")
class DefaultCallSettingsActivity : ScrollAwareToolbarActivity() {

    companion object {

        const val CURRENT_CONFIGURATION = "CURRENT_CONFIGURATION"

        @JvmStatic
        fun show(context: Context, currentConfiguration: Configuration) {
            val intent = Intent(context, DefaultCallSettingsActivity::class.java)
            intent.putExtra(CURRENT_CONFIGURATION, currentConfiguration)
            context.startActivity(intent)
        }
    }

    private lateinit var configuration: Configuration

    private val simplifiedVersionChecker = object : ConfigurationFieldChangeListener<Boolean> {
        override fun onConfigurationFieldChanged(value: Boolean) {
            val isSimplifiedVersion = isSimplifiedVersion()
            use_simplified_version.setValue(isSimplifiedVersion)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_call_settings)
        configuration = intent.getParcelableExtra<Configuration>(CURRENT_CONFIGURATION) as Configuration
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setDefaultValues()
        addPreferencesListeners()
    }

    private fun setDefaultValues() {
        use_simplified_version!!.isChecked = configuration.useSimplifiedVersion
        skip_customization!!.isChecked = configuration.skipCustomization
        default_call_type!!.setValue(configuration.defaultCallType.name)
        whiteboard!!.isChecked = configuration.withWhiteboardCapability
        file_sharing!!.isChecked = configuration.withFileSharingCapability
        chat!!.isChecked = configuration.withChatCapability
        screen_sharing!!.isChecked = configuration.withScreenSharingCapability
        call_recording!!.isChecked = configuration.withRecordingEnabled
        back_camera_as_default!!.isChecked = configuration.withBackCameraAsDefault
        call_rating!!.isChecked = configuration.withCallRating
        disable_proximity_sensor!!.apply {
            this.isChecked = if (Utils.isGoogleGlassDevice()) true else configuration.withProximitySensorDisabled
            this.isEnabled = !Utils.isGoogleGlassDevice()
        }
        if (FingerprintUtils.canAuthenticateWithBiometricSupport(this))
            mock_user_authentication_request!!.isChecked = configuration.withMockAuthentication
        else
            experimental_configurations.visibility = View.GONE
    }

    private fun addPreferencesListeners() {
        use_simplified_version.bindToConfigurationProperty(configuration, configuration::useSimplifiedVersion, object : ConfigurationFieldChangeListener<Boolean> {
            override fun onConfigurationFieldChanged(value: Boolean) {
                if (!value || (value && isSimplifiedVersion())) return
                whiteboard.setValue(true)
                file_sharing.setValue(true)
                chat.setValue(true)
                screen_sharing.setValue(true)
                call_recording.setValue(false)
                back_camera_as_default.setValue(false)
                disable_proximity_sensor.setValue(false)
                call_rating.setValue(false)
            }
        })
        skip_customization.bindToConfigurationProperty(configuration, configuration::skipCustomization)
        whiteboard.bindToConfigurationProperty(configuration, configuration::withWhiteboardCapability, simplifiedVersionChecker)
        file_sharing.bindToConfigurationProperty(configuration, configuration::withFileSharingCapability, simplifiedVersionChecker)
        chat.bindToConfigurationProperty(configuration, configuration::withChatCapability, simplifiedVersionChecker)
        screen_sharing.bindToConfigurationProperty(configuration, configuration::withScreenSharingCapability, simplifiedVersionChecker)
        call_recording.bindToConfigurationProperty(configuration, configuration::withRecordingEnabled, simplifiedVersionChecker)
        back_camera_as_default.bindToConfigurationProperty(configuration, configuration::withBackCameraAsDefault, simplifiedVersionChecker)
        disable_proximity_sensor.bindToConfigurationProperty(configuration, configuration::withProximitySensorDisabled, simplifiedVersionChecker)
        mock_user_authentication_request.bindToConfigurationProperty(configuration, configuration::withMockAuthentication, simplifiedVersionChecker)
        call_rating.bindToConfigurationProperty(configuration, configuration::withCallRating, simplifiedVersionChecker)
        default_call_type.bindToConfigurationProperty(configuration, configuration::defaultCallType)
    }

    private fun isSimplifiedVersion(): Boolean = whiteboard.isChecked &&
            file_sharing.isChecked &&
            chat.isChecked &&
            screen_sharing.isChecked &&
            !call_recording.isChecked &&
            !back_camera_as_default.isChecked &&
            !disable_proximity_sensor.isChecked &&
            !call_rating.isChecked

    override fun onBackPressed() {
        super.onBackPressed()
        sendBroadcastConfigurationResult(configuration)
    }

    private fun sendBroadcastConfigurationResult(configuration: Configuration?) {
        val resultIntent = Intent()
        resultIntent.`package` = this.packageName
        resultIntent.putExtra(ConfigurationActivity.CONFIGURATION_RESULT, configuration)
        resultIntent.action = BaseConfigurationActivity.CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE
        sendBroadcast(resultIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}