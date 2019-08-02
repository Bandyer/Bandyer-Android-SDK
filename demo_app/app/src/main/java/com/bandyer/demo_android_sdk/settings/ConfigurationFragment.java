/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */


package com.bandyer.demo_android_sdk.settings;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.custom_views.SummaryEditTextPreference;
import com.bandyer.demo_android_sdk.custom_views.SummaryListPreference;
import com.bandyer.demo_android_sdk.custom_views.SummaryPreference;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.jakewharton.processphoenix.ProcessPhoenix;

import static com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager.MY_CREDENTIAL_PREFS_NAME;

public class ConfigurationFragment extends PreferenceFragmentCompat {

    private boolean credentialsChanged = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    private SummaryEditTextPreference apiKey;
    private SummaryEditTextPreference appId;
    private SummaryEditTextPreference projectNumber;
    private SummaryListPreference environment;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBackActionFromToolbar(ConfigurationPrefsManager.hasMockCredentials(getActivity()));

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(MY_CREDENTIAL_PREFS_NAME);

        addPreferencesFromResource(R.xml.pref_configuration);

        environment = findPreference(getString(R.string.pref_environment));
        appId = findPreference(getString(R.string.pref_app_id));
        apiKey = findPreference(getString(R.string.pref_api_key));
        projectNumber = findPreference(getString(R.string.pref_project_number));

        setupPreferenceView(environment,
                0,
                R.string.summary_environment,
                ConfigurationPrefsManager.getEnvironmentName(getActivity()));

        setupPreferenceView(apiKey,
                R.string.pref_hint_api_key,
                R.string.summary_api_key,
                ConfigurationPrefsManager.getApiKey(getActivity()));

        setupPreferenceView(appId,
                R.string.pref_hint_app_id,
                R.string.summary_app_id,
                ConfigurationPrefsManager.getAppId(getActivity()));

        setupPreferenceView(projectNumber,
                R.string.pref_hint_project_number,
                R.string.summary_project_number,
                ConfigurationPrefsManager.getFirebaseProjectNumber(getActivity()));
    }

    private void setupPreferenceView(Preference preferenceView,
                                     @StringRes int hint,
                                     @StringRes int description,
                                     String defaultValue) {

        if (preferenceView instanceof EditTextPreference) {
            ((EditTextPreference) preferenceView).setOnBindEditTextListener(editText -> editText.setHint(hint));
        }
        if (preferenceView instanceof SummaryPreference)
            ((SummaryPreference) preferenceView).setSecondarySummmary(getResources().getString(description));
        if (preferenceView instanceof ListPreference)
            ((ListPreference) preferenceView).setValue(ConfigurationPrefsManager.getEnvironmentName(getActivity()));

        preferenceView.setDefaultValue(defaultValue);

        preferenceView.setOnPreferenceClickListener(preference -> false);

        setSummary(preferenceView, defaultValue);
        preferenceView.setOnPreferenceChangeListener((preference, newValue) -> {
            if (newValue.toString().isEmpty()) return false;
            setCredentialsChanged(!preference.getSummary().equals(newValue.toString()));
            setSummary(preference, newValue.toString());
            return true;
        });
    }

    void updateAllCredentials() {
        if (getActivity() == null) return;

        boolean hasApiKeyChanged = false;
        boolean hasAppIdChanged = false;
        boolean hasFirebaseProjectNumberChanged = false;
        boolean hasEnvironmentChanged = false;

        if (apiKey != null) {
            String defaultValue = ConfigurationPrefsManager.getApiKey(getActivity());
            hasApiKeyChanged = !defaultValue.equals(apiKey.getSummary().toString());
            setSummary(apiKey, defaultValue);
            apiKey.setText(defaultValue);
        }

        if (appId != null) {
            String defaultValue = ConfigurationPrefsManager.getAppId(getActivity());
            hasAppIdChanged = !defaultValue.equals(appId.getSummary().toString());
            setSummary(appId, defaultValue);
            appId.setText(defaultValue);
        }

        if (projectNumber != null) {
            String defaultValue = ConfigurationPrefsManager.getFirebaseProjectNumber(getActivity());
            hasFirebaseProjectNumberChanged = !defaultValue.equals(projectNumber.getSummary().toString());
            setSummary(projectNumber, defaultValue);
            projectNumber.setText(defaultValue);
        }

        if (environment != null) {
            String defaultValue = ConfigurationPrefsManager.getEnvironmentName(getActivity());
            hasEnvironmentChanged = !defaultValue.equals(environment.getSummary().toString());
            setSummary(environment, defaultValue);
            environment.setValue(defaultValue);
        }

        setCredentialsChanged(hasApiKeyChanged || hasAppIdChanged || hasFirebaseProjectNumberChanged || hasEnvironmentChanged);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void hideBackActionFromToolbar(boolean hide) {
        if (getActivity() == null) return;
        if (((AppCompatActivity) getActivity()).getSupportActionBar() == null) return;
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(!hide);
    }

    private void setCredentialsChanged(boolean changed) {
        credentialsChanged = credentialsChanged || changed;
    }

    private void setSummary(Preference preference, String string) {
        Spannable summary = new SpannableString(string);
        summary.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.colorPrimary)), 0, summary.length(), 0);
        preference.setSummary(summary);
    }

    boolean onActivityBackPressed() {
        if (ConfigurationPrefsManager.areCredentialsMockedOrEmpty(getActivity())) return false;
        if (credentialsChanged) ProcessPhoenix.triggerRebirth(getActivity());
        return true;
    }
}
