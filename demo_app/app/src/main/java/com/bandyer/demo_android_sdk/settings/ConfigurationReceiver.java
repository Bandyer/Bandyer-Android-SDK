package com.bandyer.demo_android_sdk.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class ConfigurationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle extras = intent.getExtras();
        if (null == extras) return;

        final String referrer = extras.getString("referrer");
        if (null == referrer) return;

        try {
            String[] pairs = referrer.split("%26|%2526|&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("%3D|%253D|=");
                switch (keyValue[0]) {
                    case "utm_content":
                        ConfigurationPrefsManager.setEnvironmentName(context, keyValue[2]);
                        break;
                    case "appId":
                        ConfigurationPrefsManager.setAppId(context, keyValue[1]);
                        break;
                    case "apiKey":
                        ConfigurationPrefsManager.setApiKey(context, keyValue[1]);
                        break;
                    case "fPN":
                        ConfigurationPrefsManager.setFirebaseProjectNumber(context, keyValue[1]);
                        break;
                    case "pushProvider":
                        ConfigurationPrefsManager.setPushProvider(context, keyValue[2]);
                        break;
                }
            }
            ProcessPhoenix.triggerRebirth(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}