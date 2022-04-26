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
package com.bandyer.demo_android_sdk.mock

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import com.kaleyra.app_utilities.R
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