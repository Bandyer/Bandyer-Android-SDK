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

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.model.Configuration
import com.kaleyra.app_configuration.model.getMockConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 *
 * @author kristiyan
 */

abstract class BaseConfigurationActivity(override var withToolbar: Boolean = true) : ScrollAwareToolbarActivity(withToolbar) {

    companion object {
        const val CONFIGURATION_ACTION_UPDATE = "com.kaleyra.app_configuration.CONFIGURATION_ACTION_UPDATE"
        const val CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE = "com.kaleyra.app_configuration.CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE"
    }

    protected var currentConfiguration: Configuration? = null
    private var dialog: Dialog? = null

    private val json: Json by lazy { Json { isLenient = true; ignoreUnknownKeys = true; coerceInputValues = true } }

    protected open fun configureFromUri(uri: Uri): Configuration? {
        if (!isValidUriScheme(uri.scheme)) return null
        try {

            val configuration: Configuration = json.decodeFromString(uriToJsonObject(uri))
            configuration.logoName = configuration.logoName?.replace("+", " ")?.trim()
            return configuration.also {
                val hasChanged = currentConfiguration != configuration
                if (!hasChanged) {
                    finish()
                } else sendBroadcastConfigurationResult(CONFIGURATION_ACTION_UPDATE, configuration)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this.applicationContext, resources.getString(R.string.invalid_configuration_url), Toast.LENGTH_LONG).show()
            finish()
            return null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentConfiguration = getInitialConfiguration()
    }

    private fun isValidUriScheme(scheme: String?) = scheme == "https" || scheme == "bandyer"

    protected fun sendBroadcastConfigurationResult(action: String, configuration: Configuration?) {
        val resultIntent = Intent()
        resultIntent.`package` = this.packageName
        resultIntent.putExtra(ConfigurationActivity.CONFIGURATION_RESULT, configuration?.toJson())
        resultIntent.action = action
        sendBroadcast(resultIntent)
    }

    private fun uriToJsonObject(uri: Uri): String {
        return ("{\"" + uri.query!!.trim { it <= ' ' }
            .replace(" ", "")
            .replace("&", "\",\"")
            .replace("=", "\":\"")
            .replace(":\"true\"", ":true")
            .replace(":\"false\"", ":false")
            .replace("\"null\"", "\"\"")
                + "\"}")
    }

    protected fun getInitialConfiguration(): Configuration = intent.getStringExtra(ConfigurationActivity.CURRENT_CONFIGURATION)?.let { Configuration.create(it) }
        ?: getMockConfiguration(this)

    protected fun showErrorDialog(text: String?) {
        if (dialog != null) dialog!!.dismiss()
        if (isFinishing) return
        dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_error))
            .setMessage(text)
            .setPositiveButton(R.string.settings_dialog_positive_button) { dialog, _ -> dialog?.dismiss() }
            .create()
        dialog!!.show()
    }
}