/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.app.Application;
import android.util.Log;

import com.bandyer.android_sdk.BandyerSDK;
import com.bandyer.android_sdk.BuildConfig;
import com.bandyer.android_sdk.networking.Environment;
import com.bandyer.android_sdk.utils.BandyerSDKLogger;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.GsonBuilder;
import com.squareup.leakcanary.LeakCanary;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;

/**
 * @author kristiyan
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Debug tools
        initLeakCanary();
        initStetho();

        // Bandyer SDK Module initialization
        BandyerSDK.Builder builder = new BandyerSDK.Builder(getApplicationContext(), getString(R.string.app_id), getString(R.string.api_key))
                .withCallEnabled()
                .setEnvironment(Environment.SANDBOX);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            builder.withChatEnabled();
        }

        if (BuildConfig.DEBUG) {
            builder.setLogger(new BandyerSDKLogger() {
                @Override
                public void verbose(@NotNull String tag, @NotNull String message) {
                    Log.v(tag, message);
                }

                @Override
                public void debug(@NotNull String tag, @NotNull String message) {
                    Log.d(tag, message);
                }

                @Override
                public void info(@NotNull String tag, @NotNull String message) {
                    Log.i(tag, message);
                }

                @Override
                public void warn(@NotNull String tag, @NotNull String message) {
                    Log.w(tag, message);
                }

                @Override
                public void error(@NotNull String tag, @NotNull String message) {
                    Log.e(tag, message);
                }
            });
        }

        builder.setHttpStackBuilder(new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()))
                .setGsonBuilder(new GsonBuilder().setPrettyPrinting());

        BandyerSDK.init(builder);

    }

    /***************************************LeackCanary*********************************************
     * Using LeakCanary library to debug potential leaks.
     * Leaks may lead to your application consuming & retaining memory inefficiently, making the device and the application slower and crash prone
     * For more information visit:
     * https://github.com/square/leakcanary
     **********************************************************************************************/

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this))
            return;
        LeakCanary.install(this);
    }


    /***************************************Stetho**************************************************
     * Using Stetho to debug networking data in a easy way
     *
     * For more information visit:
     * https://github.com/facebook/stetho
     **********************************************************************************************/

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }

}
