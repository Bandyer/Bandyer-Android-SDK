/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bandyer.app_configuration.R
import com.bandyer.app_configuration.external_configuration.model.Configuration
import com.bandyer.app_configuration.external_configuration.model.getMockConfiguration
import com.google.gson.Gson

/**
 *
 * @author kristiyan
 */

abstract class BaseConfigurationActivity(override var withToolbar: Boolean = true) : ScrollAwareToolbarActivity(withToolbar) {

    companion object {
        const val CONFIGURATION_ACTION_UPDATE = "com.bandyer.app_configuration.external_configuration.CONFIGURATION_ACTION_UPDATE"
        const val CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE = "com.bandyer.app_configuration.external_configuration.CONFIGURATION_CALL_SETTINGS_ACTION_UPDATE"
    }

    protected var currentConfiguration: Configuration? = null
    private var dialog: Dialog? = null

    protected open fun configureFromUri(uri: Uri): Configuration? {
        if (!isValidUriScheme(uri.scheme)) return null
        try {
            val gson = Gson()
            val configuration = gson.fromJson(uriToJsonObject(uri), Configuration::class.java)
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
        resultIntent.putExtra(ConfigurationActivity.CONFIGURATION_RESULT, configuration)
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

    protected fun getInitialConfiguration(): Configuration = (intent.getParcelableExtra(ConfigurationActivity.CURRENT_CONFIGURATION)
            ?: getMockConfiguration(this)).deepClone()

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