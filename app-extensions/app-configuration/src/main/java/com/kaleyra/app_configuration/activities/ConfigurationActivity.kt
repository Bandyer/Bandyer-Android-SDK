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
package com.kaleyra.app_configuration.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.model.*
import com.kaleyra.app_configuration.utils.MediaStorageUtils
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.activity_configuration.watermark

open class ConfigurationActivity : BaseConfigurationActivity() {

    companion object {

        const val CURRENT_CONFIGURATION = "CURRENT_CONFIGURATION"
        const val CONFIGURATION_RESULT = "CONFIGURATION_RESULT"
        const val BRAND_IMAGE_TEXT_REQUEST = 456
        const val MOCK_USER_DETAILS_REQUEST = 457

        @JvmOverloads
        fun show(context: Context, currentConfiguration: Configuration?, showAsQRReader: Boolean = false, qrConfigurationActivity: Class<*>? = null, withOptions: ((intent: Intent) -> Unit)? = null) {
            if (showAsQRReader || qrConfigurationActivity != null) show(context, currentConfiguration, qrConfigurationActivity
                    ?: QRConfigurationActivity::class.java, withOptions)
            else show(context, currentConfiguration, ConfigurationActivity::class.java, withOptions)
        }

        @JvmOverloads
        fun showNew(context: Context, currentConfiguration: Configuration?, showAsQRReader: Boolean = false, qrConfigurationActivity: Class<*>? = null) {
            val withOptions: (intent: Intent) -> Unit = { intent ->
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            show(context, currentConfiguration, showAsQRReader || qrConfigurationActivity != null, qrConfigurationActivity, withOptions)
        }

        private fun show(context: Context, currentConfiguration: Configuration?, configurationActivity: Class<*>, withOptions: ((intent: Intent) -> Unit)? = null) {
            val intent = Intent(context, configurationActivity)
            intent.putExtra(CURRENT_CONFIGURATION, currentConfiguration?.toJson())
            withOptions?.invoke(intent)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        setDefaultValues(getInitialConfiguration())
        addPreferencesListeners()
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data ?: return
        currentConfiguration = configureFromUri(uri)
        currentConfiguration ?: return
        setDefaultValues(currentConfiguration!!)
    }

    private fun setDefaultValues(configuration: Configuration) {
        this.currentConfiguration = configuration
        environment!!.setValue(configuration.environment)
        region!!.setValue(configuration.region)
        app_id!!.setValue(configuration.appId)
        api_key!!.setValue(configuration.apiKey)
        push_provider!!.setValue(configuration.pushProvider.name)
        onPushProviderChanged(configuration.pushProvider)
        firebase_project_number!!.setValue(configuration.projectNumber)
        firebase_project_id!!.setValue(configuration.firebaseProjectId)
        firebase_api_key!!.setValue(configuration.firebaseApiKey)
        firebase_mobile_app_id!!.setValue(configuration.firebaseMobileAppId)
        hms_app_id!!.setValue(configuration.hmsAppId)
        watermark!!.setImageName(configuration.logoName)
        configuration.logoUrl?.let { textUri ->
            watermark!!.setImageUri(MediaStorageUtils.getUriFromString(textUri))
        }
        mock_user_details!!.setSubtitle(configuration.userDetailsProviderMode.name)
        leak_canary.setValue(configuration.useLeakCanary)
    }

    private fun addPreferencesListeners() {
        environment.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::environment)
        region.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::region)
        app_id!!.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::appId)
        api_key!!.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::apiKey)
        push_provider.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::pushProvider, object :
                ConfigurationFieldChangeListener<String> {
            override fun onConfigurationFieldChanged(value: String) =
                    onPushProviderChanged(PushProvider.valueOf(value))
        })
        firebase_project_number!!.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::projectNumber)
        firebase_project_id!!.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::firebaseProjectId)
        firebase_api_key!!.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::firebaseApiKey)
        firebase_mobile_app_id!!.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::firebaseMobileAppId)
        hms_app_id!!.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::hmsAppId)
        leak_canary!!.bindToConfigurationProperty(currentConfiguration!!, currentConfiguration!!::useLeakCanary)

        watermark!!.setOnClickListener {
            ImageTextEditActivity.showForResult(
                    this,
                    Uri.parse(currentConfiguration?.logoUrl ?: ""),
                    currentConfiguration!!.logoName ?: "",
                    BRAND_IMAGE_TEXT_REQUEST)
        }
        mock_user_details!!.setOnClickListener {
            MockUserDetailsSettingsActivity.showForResult(
                this,
                if (currentConfiguration!!.customUserDetailsImageUrl != null)
                        Uri.parse(currentConfiguration!!.customUserDetailsImageUrl)
                    else null,
                currentConfiguration!!.customUserDetailsName ?: "",
                currentConfiguration!!.userDetailsProviderMode,
                MOCK_USER_DETAILS_REQUEST)
        }
    }

    private fun onPushProviderChanged(pushProvider: PushProvider) {
        fcm_configuration_fields.visibility = when (pushProvider) {
            PushProvider.FCM -> View.VISIBLE
            else-> View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BRAND_IMAGE_TEXT_REQUEST && resultCode == 2 && data!!.extras != null) {
            val url = data.getStringExtra(ImageTextEditActivity.PRESET_URI_PARAM)
            val text = data.getStringExtra(ImageTextEditActivity.PRESET_TEXT_PARAM)
            val uri = MediaStorageUtils.getUriFromString(url)
            watermark!!.setImageName(text)
            watermark!!.setImageUri(uri)
            currentConfiguration!!.logoUrl = url
            currentConfiguration!!.logoName = text
        } else if (requestCode == MOCK_USER_DETAILS_REQUEST && resultCode == 2) {
            val customUserImageUrl = data!!.getStringExtra(ImageTextEditActivity.PRESET_URI_PARAM)
            val customDisplayName = data.getStringExtra(ImageTextEditActivity.PRESET_TEXT_PARAM)
            val mockProviderMode = data.getSerializableExtra(MockUserDetailsSettingsActivity.MOCK_MODE_PARAM) as UserDetailsProviderMode
            currentConfiguration!!.customUserDetailsImageUrl = customUserImageUrl
            currentConfiguration!!.customUserDetailsName = customDisplayName
            currentConfiguration!!.userDetailsProviderMode = mockProviderMode
            mock_user_details!!.setSubtitle(mockProviderMode.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.configuration_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.qr_code -> show(this, currentConfiguration, true)
            R.id.reset_all -> {
                setDefaultValues(getInitialConfiguration())
            }
            R.id.clear_all -> {
                setDefaultValues(getMockConfiguration(this))
            }
            R.id.save -> {
                if (currentConfiguration!!.isMockConfiguration()) {
                    showErrorDialog(resources.getString(R.string.settings_are_not_correctly_set))
                    return true
                }
                sendBroadcastConfigurationResult(CONFIGURATION_ACTION_UPDATE, currentConfiguration)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}