/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.activities.BaseActivity;

public class DefaultCallSettingsActivity extends BaseActivity {

    private DefaultCallSettingsFragment defaultCallSettingsFragment;

    public static void show(Context context) {
        Intent intent = new Intent(context, DefaultCallSettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_options);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);

        defaultCallSettingsFragment = new DefaultCallSettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.pref_content, defaultCallSettingsFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
