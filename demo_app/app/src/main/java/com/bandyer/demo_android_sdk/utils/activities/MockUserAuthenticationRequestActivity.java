/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.activities;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import com.bandyer.android_sdk.call.CallModule;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.intent.call.Call;
import com.bandyer.android_sdk.intent.call.CallDisplayMode;
import com.bandyer.demo_android_sdk.utils.FingerprintUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * An activity used to mock user authentication request
 */
public class MockUserAuthenticationRequestActivity extends BaseActivity {

    private Executor executor = Executors.newSingleThreadExecutor();
    private IntentFilter screenLockUnlockIntentFilter = new IntentFilter();
    private BroadcastReceiver screenLockUnlockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strAction = intent.getAction();

            KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (strAction.equals(Intent.ACTION_USER_PRESENT) || strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON))
                if (!myKM.inKeyguardRestrictedInputMode())
                    showBiometricPrompt();
        }
    };

    private boolean hasShownBiometricPrompt = false;
    private int authenticationAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerScreenUnlockReceiver();
    }

    private void registerScreenUnlockReceiver() {
        screenLockUnlockIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenLockUnlockIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
        getApplicationContext().registerReceiver(screenLockUnlockReceiver, screenLockUnlockIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FingerprintUtils.hideUserAuthenticationBiometricPrompt();
        getApplicationContext().unregisterReceiver(screenLockUnlockReceiver);
    }

    protected void onResume() {
        super.onResume();

        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean isPhoneLocked = myKM.isKeyguardLocked();
        if (!isPhoneLocked) showBiometricPrompt();
    }

    protected void onPause() {
        super.onPause();
    }

    private void showBiometricPrompt() {
        if (hasShownBiometricPrompt) return;
        hasShownBiometricPrompt = true;

        FingerprintUtils.showUserAuthenticationBiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                runOnUiThread(() -> {
                    onVerified(true);
                    finish();
                });
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                runOnUiThread(() -> {
                    showToast(errString.toString());
                    finish();
                });
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                runOnUiThread(() -> {
                    if (authenticationAttempts < 3) {
                        authenticationAttempts++;
                        showBiometricPrompt();
                        return;
                    }
                    finish();
                });
            }

            private void onVerified(boolean verified) {
                CallModule callModule = BandyerSDKClient.getInstance().getCallModule();
                if (callModule == null) return;

                Call ongoingCall = callModule.getOngoingCall();
                if (ongoingCall == null) return;

                callModule.setVerified(ongoingCall, verified);
                callModule.setDisplayMode(ongoingCall, CallDisplayMode.FOREGROUND);
            }
        });
    }
}
