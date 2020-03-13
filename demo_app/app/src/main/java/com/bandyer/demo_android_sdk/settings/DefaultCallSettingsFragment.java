/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.FingerprintUtils;
import com.bandyer.demo_android_sdk.utils.storage.DefaultCallSettingsManager;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;

public class DefaultCallSettingsFragment  extends PreferenceFragmentCompat {

    private CheckBoxPreference useSimplifiedVersion;
    private CheckBoxPreference mockUserAuthenticationRequest;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) { }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBackActionFromToolbar(ConfigurationPrefsManager.hasMockCredentials(getActivity()));

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(DefaultCallSettingsManager.CALL_OPTIONS_PREFS_NAME);

        addPreferencesFromResource(R.xml.pref_call_options);

        useSimplifiedVersion = findPreference(getString(R.string.use_simplified_version));
        useSimplifiedVersion.setChecked(ConfigurationPrefsManager.isSimplifiedVersionEnabled(getActivity()));
        useSimplifiedVersion.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean value = ((boolean) newValue);
            ConfigurationPrefsManager.setSimplifiedVersionEnabled(getContext(), value);
            return true;
        });

        mockUserAuthenticationRequest = findPreference(getString(R.string.call_options_mock_user_authentication_request));
        if (FingerprintUtils.canAuthenticateWithBiometricSupport(getActivity())) {
            mockUserAuthenticationRequest.setChecked(ConfigurationPrefsManager.isMockUserAuthenticationRequest(getActivity()));
            mockUserAuthenticationRequest.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean value = ((boolean) newValue);
                ConfigurationPrefsManager.setMockUserAuthenticationRequest(getContext(), value);
                return true;
            });
        } else {
            PreferenceCategory experimental = findPreference("Experimental");
            experimental.removePreference(mockUserAuthenticationRequest);
            if (experimental.getPreferenceCount() == 0)
                getPreferenceScreen().removePreference(experimental);
        }
    }

    private void hideBackActionFromToolbar(boolean hide) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(!hide);
    }
}