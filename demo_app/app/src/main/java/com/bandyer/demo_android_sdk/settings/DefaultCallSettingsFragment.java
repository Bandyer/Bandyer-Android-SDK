/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.storage.DefaultCallSettingsManager;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;

public class DefaultCallSettingsFragment  extends PreferenceFragmentCompat {

    private CheckBoxPreference useSimplifiedVersion;

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
    }

    private void hideBackActionFromToolbar(boolean hide) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(!hide);
    }
}