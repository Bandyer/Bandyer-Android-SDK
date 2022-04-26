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

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import com.bandyer.android_sdk.client.BandyerSDK.Companion.getInstance
import com.bandyer.android_sdk.intent.call.CallDisplayMode
import com.kaleyra.app_configuration.activities.ScrollAwareToolbarActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * An activity used to mock user authentication request
 */
class MockUserAuthenticationRequestActivity : ScrollAwareToolbarActivity() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val screenLockUnlockIntentFilter = IntentFilter()
    private val screenLockUnlockReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val strAction = intent.action
            val myKM = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (strAction == Intent.ACTION_USER_PRESENT || strAction == Intent.ACTION_SCREEN_OFF || strAction == Intent.ACTION_SCREEN_ON) if (!myKM.inKeyguardRestrictedInputMode()) showBiometricPrompt()
        }
    }
    private var hasShownBiometricPrompt = false
    private var authenticationAttempts = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerScreenUnlockReceiver()
    }

    private fun registerScreenUnlockReceiver() {
        screenLockUnlockIntentFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenLockUnlockIntentFilter.addAction(Intent.ACTION_USER_PRESENT)
        applicationContext.registerReceiver(screenLockUnlockReceiver, screenLockUnlockIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        FingerprintUtils.hideUserAuthenticationBiometricPrompt()
        applicationContext.unregisterReceiver(screenLockUnlockReceiver)
    }

    override fun onResume() {
        super.onResume()
        val myKM = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val isPhoneLocked = myKM.isKeyguardLocked
        if (!isPhoneLocked) showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        if (hasShownBiometricPrompt) return
        hasShownBiometricPrompt = true
        FingerprintUtils.showUserAuthenticationBiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                runOnUiThread {
                    onVerified(true)
                    finish()
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                runOnUiThread {
                    Toast.makeText(this@MockUserAuthenticationRequestActivity, errString.toString(), Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                runOnUiThread {
                    if (authenticationAttempts < 3) {
                        authenticationAttempts++
                        showBiometricPrompt()
                        return@runOnUiThread
                    }
                    finish()
                }
            }

            private fun onVerified(verified: Boolean) {
                val callModule = getInstance().callModule ?: return
                val ongoingCall = callModule.ongoingCall ?: return
                callModule.setVerified(ongoingCall, verified)
                callModule.setDisplayMode(ongoingCall, CallDisplayMode.FOREGROUND)
            }
        })
    }
}