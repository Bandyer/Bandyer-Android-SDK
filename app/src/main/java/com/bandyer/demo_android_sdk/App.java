/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bandyer.android_common.logging.BaseLogger;
import com.bandyer.android_sdk.BandyerSDK;
import com.bandyer.android_sdk.Environment;
import com.bandyer.android_sdk.call.model.CallInfo;
import com.bandyer.android_sdk.call.notification.CallNotificationListener;
import com.bandyer.android_sdk.call.notification.CallNotificationStyle;
import com.bandyer.android_sdk.call.notification.CallNotificationType;
import com.bandyer.android_sdk.chat.model.ChatInfo;
import com.bandyer.android_sdk.chat.notification.ChatNotificationListener;
import com.bandyer.android_sdk.chat.notification.ChatNotificationStyle;
import com.bandyer.android_sdk.file_sharing.model.FileInfo;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationListener;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationStyle;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationType;
import com.bandyer.android_sdk.intent.call.CallCapabilities;
import com.bandyer.android_sdk.intent.call.CallOptions;
import com.bandyer.android_sdk.intent.call.IncomingCall;
import com.bandyer.android_sdk.intent.call.IncomingCallOptions;
import com.bandyer.android_sdk.intent.chat.IncomingChat;
import com.bandyer.android_sdk.intent.file.IncomingFile;
import com.bandyer.android_sdk.utils.BandyerSDKLogger;
import com.bandyer.app_configuration.external_configuration.model.Configuration;
import com.bandyer.app_configuration.external_configuration.model.CustomUserDetailsProvider;
import com.bandyer.app_utilities.MultiDexApplication;
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager;
import com.bandyer.app_utilities.utils.Utils;
import com.bandyer.demo_android_sdk.mock.MockedUserProvider;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;

/**
 * This custom implementation of Application class is needed to initialize Bandyer SDK module.
 *
 * @author kristiyan
 */
public class App extends MultiDexApplication {

    @Override
    public void create() {

        Configuration configuration = getConfiguration();

        if (configuration.isMockConfiguration()) return;

        String environmentName = configuration.getEnvironment();

        // Retrieves Environment.Configuration.sandbox() or Environment.Configuration.production()
        // as set in the configuration settings
        Environment env = Utils.getEnvironmentByName(environmentName);

        // Bandyer SDK Module initialization
        BandyerSDK.Builder builder = new BandyerSDK.Builder(this, configuration.getAppId())
                .setEnvironment(env)
                .withCallEnabled(getCallNotificationListener())
                .withChatEnabled(getChatNotificationListener())
                .withFileSharingEnabled(getFileSharingNotificationListener());

        // If you desire to personalize the user details shown you should set a provider and formatter
        // otherwise the userAlias and default avatar will be shown in chat or call.

        if (configuration.getCustomUserDetailsProvider() != CustomUserDetailsProvider.NONE)
            builder.withUserDetailsProvider(new MockedUserProvider(this));

        builder.withUserDetailsFormatter((userDetails, context) -> {
            CustomUserDetailsProvider customUserDetailsProvider = configuration.getCustomUserDetailsProvider();
            switch (customUserDetailsProvider) {
                case RANDOM:
                    return userDetails.getFirstName() + " " + userDetails.getLastName();
                case CUSTOM:
                    return userDetails.getNickName();
                default:
                    return userDetails.getUserAlias();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            builder.withWhiteboardEnabled();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.withScreenSharingEnabled();

        if (BuildConfig.DEBUG) {
            builder.setLogger(new BandyerSDKLogger(BaseLogger.ERROR) {

                @Override
                public void verbose(@NonNull String tag, @NonNull String message) {
                    Log.v(tag, message);
                }

                @Override
                public void debug(@NonNull String tag, @NonNull String message) {
                    Log.d(tag, message);
                }

                @Override
                public void info(@NonNull String tag, @NonNull String message) {
                    Log.i(tag, message);
                }

                @Override
                public void warn(@NonNull String tag, @NonNull String message) {
                    Log.w(tag, message);
                }

                @Override
                public void error(@NonNull String tag, @NonNull String message) {
                    Log.e(tag, message);
                }
            });
        }

        OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
        builder.setHttpStack(client)
                .setGsonBuilder(new GsonBuilder().setPrettyPrinting());

        BandyerSDK.init(builder);
    }

    @SuppressLint("NewApi")
    private CallCapabilities getDefaultCallCapabilities() {
        // You may also initialize the callCapabilities without any argument in the constructor
        // You can enable a single capability using the utility methods
        // Example :
        // new CallCapabilities().withChat();
        Configuration configuration = getConfiguration();
        return new CallCapabilities(configuration.getWithChatCapability(),
                configuration.getWithFileSharingCapability(),
                configuration.getWithScreenSharingCapability(),
                configuration.getWithWhiteboardCapability());
    }

    private IncomingCallOptions getDefaultIncomingCallOptions() {
        // You may also initialize the callOptions without any argument in the constructor
        // You can enable a single option using the utility methods
        // Example :
        // new IncomingCallOptions().withBackCameraAsDefault();
        Configuration configuration = getConfiguration();
        return new IncomingCallOptions(
                configuration.getWithBackCameraAsDefault(),
                configuration.getWithProximitySensorDisabled());
    }

    private CallNotificationListener getCallNotificationListener() {
        return new CallNotificationListener() {
            @Override
            public void onIncomingCall(@NonNull IncomingCall incomingCall, boolean isDnd, boolean isScreenLocked) {
                incomingCall.withCapabilities(getDefaultCallCapabilities());
                incomingCall.withOptions(getDefaultIncomingCallOptions());

                if (!isDnd || isScreenLocked)
                    incomingCall
                            .show(App.this);
                else {
                    incomingCall
                            .asNotification()
                            .show(App.this);
                }
            }

            @Override
            public void onCreateNotification(@NonNull CallInfo callInfo,
                                             @NonNull CallNotificationType type,
                                             @NonNull CallNotificationStyle notificationStyle) {
                notificationStyle.setNotificationColor(Color.RED);
            }
        };
    }

    private ChatNotificationListener getChatNotificationListener() {
        return new ChatNotificationListener() {
            @Override
            public void onIncomingChat(@NonNull IncomingChat chat, boolean isDnd, boolean isScreenLocked) {
                CallCapabilities callCapabilities = getDefaultCallCapabilities();
                // You may also initialize the callOptions without any argument in the constructor
                // You can enable a single option using the utility methods
                // Example :
                // new CallOptions().withBackCameraAsDefault();
                Configuration configuration = getConfiguration();
                CallOptions callOptions = new CallOptions(
                        configuration.getWithRecordingEnabled(),
                        configuration.getWithBackCameraAsDefault(),
                        !configuration.getWithProximitySensorDisabled());

                chat.withAudioCallCapability(callCapabilities, callOptions)
                        .withAudioUpgradableCallCapability(callCapabilities, callOptions)
                        .withAudioVideoCallCapability(callCapabilities, callOptions);

                chat.asNotification().show(App.this);
            }

            @Override
            public void onCreateNotification(@NonNull ChatInfo chatInfo, @NonNull ChatNotificationStyle notificationStyle) {
            }
        };
    }


    private FileSharingNotificationListener getFileSharingNotificationListener() {
        return new FileSharingNotificationListener() {

            @Override
            public void onIncomingFile(@NonNull IncomingFile file, boolean isDnd, boolean isScreenLocked) {
                file.asNotification(App.this).show();
            }

            @Override
            public void onCreateNotification(@NonNull FileInfo fileInfo,
                                             @NonNull FileSharingNotificationType notificationType,
                                             @NonNull FileSharingNotificationStyle notificationStyle) {
                notificationStyle.setNotificationColor(Color.GREEN);
            }
        };
    }

    private Configuration getConfiguration() {
        return ConfigurationPrefsManager.INSTANCE.getConfiguration(this);
    }
}