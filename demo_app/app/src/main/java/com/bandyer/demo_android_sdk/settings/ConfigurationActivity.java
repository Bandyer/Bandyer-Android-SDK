/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.activities.BaseActivity;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
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

        configurationFragment = new ConfigurationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.pref_content, configurationFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (ConfigurationPrefsManager.areCredentialsMockedOrEmpty(this)) showErrorDialog("Settings are not correctly set!");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_all:
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
