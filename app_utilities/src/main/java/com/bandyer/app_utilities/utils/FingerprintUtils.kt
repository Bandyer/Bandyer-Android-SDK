/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import com.bandyer.app_utilities.R
import java.util.concurrent.Executor

object FingerprintUtils {

    private var biometricPrompt: BiometricPrompt? = null

    fun hideUserAuthenticationBiometricPrompt() {
        if (biometricPrompt == null) return
        biometricPrompt!!.cancelAuthentication()
        biometricPrompt = null
    }

    fun showUserAuthenticationBiometricPrompt(appCompatActivity: AppCompatActivity, executor: Executor?, authenticationCallback: BiometricPrompt.AuthenticationCallback?) {
        if (biometricPrompt != null) return
        biometricPrompt = BiometricPrompt(appCompatActivity, executor!!, authenticationCallback!!)
        val promptInfo = PromptInfo.Builder()
                .setTitle(appCompatActivity.resources.getString(R.string.mock_user_authentication_dialog_title))
                .setSubtitle(appCompatActivity.resources.getString(R.string.mock_user_authentication_dialog_subtitle))
                .setDescription(appCompatActivity.resources.getString(R.string.mock_user_authentication_dialog_description))
                .setConfirmationRequired(false)
                .setDeviceCredentialAllowed(true)
                .build()
        biometricPrompt!!.authenticate(promptInfo)
        biometricPrompt = null
    }

    @JvmStatic
    fun canAuthenticateWithBiometricSupport(context: Context?): Boolean {
        val biometricManager = BiometricManager.from(context!!)
        val canAuthenticate = biometricManager.canAuthenticate()
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }
}