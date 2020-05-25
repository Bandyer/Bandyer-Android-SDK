/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.activities.BaseActivity;
import com.bandyer.demo_android_sdk.utils.activities.QRConfigurationActivity;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.bandyer.demo_android_sdk.utils.storage.DefaultCallSettingsManager;
import com.bandyer.demo_android_sdk.utils.storage.LoginManager;
import com.google.gson.Gson;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class ConfigurationActivity extends BaseActivity {

    public static void show(Context context) {
        Intent intent = new Intent(context, ConfigurationActivity.class);
        context.startActivity(intent);
    }

    public static void showNew(Context context) {
        Intent intent = new Intent(context, ConfigurationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private ConfigurationFragment configurationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
        configure(getIntent());
        configurationFragment = new ConfigurationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.pref_content, configurationFragment).commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        configure(intent);
    }

    private void configure(Intent intent) {
        Uri data = intent.getData();
        if (data == null) return;
        try {
            Gson gson = new Gson();
            Configuration conf = gson.fromJson(uriToJsonObject(data), Configuration.class);

            if (conf != null) {
                // required conf
                ConfigurationPrefsManager.setApiKey(this, conf.apiKey);
                ConfigurationPrefsManager.setAppId(this, conf.appId);
                ConfigurationPrefsManager.setEnvironmentName(this, conf.environment);
                ConfigurationPrefsManager.setFirebaseProjectNumber(this, conf.projectNumber);

                // customization conf
                ConfigurationPrefsManager.setWatermarkText(this, conf.logoName);
                ConfigurationPrefsManager.setWatermarkUri(this, conf.logoUrl);

                ConfigurationPrefsManager.setLeakCanaryEnabled(this, conf.useLeakCanary);
                if (conf.useRandomMockUserDetailsProvider)
                    ConfigurationPrefsManager.setMockedUserDetailsMode(this, getResources().getString(R.string.mock_user_details_config_random));
                ConfigurationPrefsManager.setSimplifiedVersionEnabled(this, conf.useSimplifiedVersion);

                ConfigurationPrefsManager.setMockUserAuthenticationRequest(this,conf.withMockAuthentication);

                DefaultCallSettingsManager.setDefaultCallType(this, conf.defaultCallType);
                DefaultCallSettingsManager.setBackCameraAsDefaultEnabled(this, conf.withBackCameraAsDefault);
                DefaultCallSettingsManager.setCallRecordingEnabled(this, conf.withRecordingEnabled);
                DefaultCallSettingsManager.setChatEnabled(this, conf.withChatCapability);
                DefaultCallSettingsManager.setWhiteboardEnabled(this, conf.withWhiteboardCapability);
                DefaultCallSettingsManager.setScreenSharingEnabled(this, conf.withScreenSharingCapability);
                DefaultCallSettingsManager.setFileSharingEnabled(this, conf.withFileSharingCapability);
                DefaultCallSettingsManager.setProximitySensorDisabled(this, conf.withProximityEnabled);

                // log user
                LoginManager.login(this, conf.userAlias);

                ProcessPhoenix.triggerRebirth(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String uriToJsonObject(Uri uri) {
        return "{\"" + uri.getQuery().trim()
                .concat("\"")
                .replace(" ", "")
                .replace("&", "\",\"")
                .replace("=", "\":\"")
                .replace(":\"true\"", ":true")
                .replace(":\"false\"", ":false")
                .replace("\"null\"", "\"\"")
                + "}";
    }

    @Override
    public void onBackPressed() {
        if (ConfigurationPrefsManager.areCredentialsMockedOrEmpty(this))
            showErrorDialog("Settings are not correctly set!");
        else if (configurationFragment.didChangeSettings()) ProcessPhoenix.triggerRebirth(this);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.configuration_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == QRConfigurationActivity.REQUEST_CONFIGURATION_VIA_QR){
            configure(data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.qr_code:
                QRConfigurationActivity.show(this);
                break;
            case R.id.reset_all:
                ConfigurationPrefsManager.clear(this);
                configurationFragment.updateAllCredentials();
                break;
            case R.id.save:
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
