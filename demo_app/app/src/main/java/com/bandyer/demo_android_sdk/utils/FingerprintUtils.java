/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.bandyer.demo_android_sdk.R;

import java.util.concurrent.Executor;

public class FingerprintUtils {

    private static androidx.biometric.BiometricPrompt biometricPrompt;

    public static void hideUserAuthenticationBiometricPrompt() {
        if (biometricPrompt == null) return;
        biometricPrompt.cancelAuthentication();
        biometricPrompt = null;
    }

    public static void showUserAuthenticationBiometricPrompt(AppCompatActivity appCompatActivity, Executor executor, BiometricPrompt.AuthenticationCallback authenticationCallback) {
        if (biometricPrompt != null) return;
        biometricPrompt = new androidx.biometric.BiometricPrompt(appCompatActivity, executor, authenticationCallback);
        final androidx.biometric.BiometricPrompt.PromptInfo promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle(appCompatActivity.getResources().getString(R.string.mock_user_authentication_dialog_title))
                .setSubtitle(appCompatActivity.getResources().getString(R.string.mock_user_authentication_dialog_subtitle))
                .setDescription(appCompatActivity.getResources().getString(R.string.mock_user_authentication_dialog_description))
                .setConfirmationRequired(false)
                .setDeviceCredentialAllowed(true)
                .build();
        biometricPrompt.authenticate(promptInfo);
        biometricPrompt = null;
    }

    public static boolean canAuthenticateWithBiometricSupport(Context context) {
        BiometricManager biometricManager = androidx.biometric.BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate();
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }
}
