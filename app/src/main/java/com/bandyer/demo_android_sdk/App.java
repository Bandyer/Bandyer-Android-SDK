/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bandyer.android_common.logging.BaseLogger;
import com.bandyer.android_sdk.BandyerSDK;
import com.bandyer.android_sdk.Environment;
import com.bandyer.android_sdk.call.model.CallInfo;
import com.bandyer.android_sdk.call.notification.CallNotificationListener;
import com.bandyer.android_sdk.call.notification.CallNotificationStyle;
import com.bandyer.android_sdk.call.notification.CallNotificationType;
import com.bandyer.android_sdk.call.notification.SimpleCallNotificationListener;
import com.bandyer.android_sdk.chat.model.ChatInfo;
import com.bandyer.android_sdk.chat.notification.ChatNotificationListener;
import com.bandyer.android_sdk.chat.notification.ChatNotificationStyle;
import com.bandyer.android_sdk.chat.notification.SimpleChatNotificationListener;
import com.bandyer.android_sdk.file_sharing.model.FileInfo;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationListener;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationStyle;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationType;
import com.bandyer.android_sdk.intent.call.IncomingCall;
import com.bandyer.android_sdk.intent.call.IncomingCallOptions;
import com.bandyer.android_sdk.intent.chat.IncomingChat;
import com.bandyer.android_sdk.intent.file.IncomingFile;
import com.bandyer.android_sdk.tool_configuration.CallCapabilitySet;
import com.bandyer.android_sdk.tool_configuration.CallConfiguration;
import com.bandyer.android_sdk.tool_configuration.ChatConfiguration;
import com.bandyer.android_sdk.tool_configuration.FileShareConfiguration;
import com.bandyer.android_sdk.tool_configuration.IncomingCallConfiguration;
import com.bandyer.android_sdk.tool_configuration.ScreenShareConfiguration;
import com.bandyer.android_sdk.tool_configuration.WhiteboardConfiguration;
import com.bandyer.android_sdk.utils.BandyerSDKLogger;
import com.bandyer.app_configuration.external_configuration.model.Configuration;
import com.bandyer.app_configuration.external_configuration.model.UserDetailsProviderMode;
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
        BandyerSDK.Configuration config = new BandyerSDK.Configuration(this, configuration.getAppId())
                .setEnvironment(env)
                .withCallEnabled(getCallNotificationListener())
                .withChatEnabled(getChatNotificationListener())
                .withFileSharingEnabled(getFileSharingNotificationListener());

        // If you desire to personalize the user details shown you should set a provider and formatter
        // otherwise the userAlias and default avatar will be shown in chat or call.

        if (configuration.getUserDetailsProviderMode() != UserDetailsProviderMode.NONE)
            config.withUserDetailsProvider(new MockedUserProvider(this));

        config.withUserDetailsFormatter((userDetails, context) -> {
            UserDetailsProviderMode userDetailsProviderMode = configuration.getUserDetailsProviderMode();
            switch (userDetailsProviderMode) {
                case SAMPLE:
                case CUSTOM:
                    return userDetails.getNickName() != null && !userDetails.getNickName().isEmpty() ? userDetails.getNickName() : userDetails.getUserAlias();
                default:
                    return userDetails.getUserAlias();
            }
        });

        config.withWhiteboardEnabled();

        config.withScreenSharingEnabled();

        if (BuildConfig.DEBUG) {
            config.setLogger(new BandyerSDKLogger(BaseLogger.ERROR) {

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
        config.setHttpStack(client)
                .setGsonBuilder(new GsonBuilder().setPrettyPrinting());

        BandyerSDK.init(config);
    }

    private CallCapabilitySet getDefaultCallCapabilitySet() {
        Configuration appConfiguration = getConfiguration();

        ChatConfiguration chatConfiguration = appConfiguration.getWithChatCapability() ? new ChatConfiguration(getDefaultChatCallCapabilities()) : null;
        FileShareConfiguration fileShareConfiguration = appConfiguration.getWithFileSharingCapability() ? new FileShareConfiguration() : null;
        ScreenShareConfiguration screenShareConfiguration = appConfiguration.getWithScreenSharingCapability() ? new ScreenShareConfiguration() : null;
        WhiteboardConfiguration whiteboardConfiguration = appConfiguration.getWithWhiteboardCapability() ? new WhiteboardConfiguration() : null;

        return new CallConfiguration.CapabilitySet(chatConfiguration, fileShareConfiguration, screenShareConfiguration, whiteboardConfiguration);
    }

    private ChatConfiguration.CapabilitySet getDefaultChatCallCapabilities() {
        Configuration appConfiguration = getConfiguration();

        FileShareConfiguration fileShareConfiguration = appConfiguration.getWithFileSharingCapability() ? new FileShareConfiguration() : null;
        ScreenShareConfiguration screenShareConfiguration = appConfiguration.getWithScreenSharingCapability() ? new ScreenShareConfiguration() : null;
        WhiteboardConfiguration whiteboardConfiguration = appConfiguration.getWithWhiteboardCapability() ? new WhiteboardConfiguration() : null;

        ChatConfiguration.CapabilitySet.CallConfiguration audioOnlyCallCapabilitySet = new ChatConfiguration.CapabilitySet.CallConfiguration(
                new ChatConfiguration.CapabilitySet.CallConfiguration.CapabilitySet(
                        fileShareConfiguration,
                        screenShareConfiguration,
                        whiteboardConfiguration),
                getDefaultIncomingCallOptionSet());

        ChatConfiguration.CapabilitySet.CallConfiguration audioUpgradableCallCapabilitySet = new ChatConfiguration.CapabilitySet.CallConfiguration(
                new ChatConfiguration.CapabilitySet.CallConfiguration.CapabilitySet(
                        fileShareConfiguration,
                        screenShareConfiguration,
                        whiteboardConfiguration),
                getDefaultIncomingCallOptionSet());

        ChatConfiguration.CapabilitySet.CallConfiguration audioVideoCallCapabilitySet = new ChatConfiguration.CapabilitySet.CallConfiguration(
                new ChatConfiguration.CapabilitySet.CallConfiguration.CapabilitySet(
                        fileShareConfiguration,
                        screenShareConfiguration,
                        whiteboardConfiguration),
                getDefaultIncomingCallOptionSet());

        return new ChatConfiguration.CapabilitySet(audioOnlyCallCapabilitySet, audioUpgradableCallCapabilitySet, audioVideoCallCapabilitySet);
    }

    private IncomingCallOptions getDefaultIncomingCallOptionSet() {
        // You may also initialize the callOptions without any argument in the constructor
        // You can enable a single option using the utility methods
        // Example :
        // new IncomingCallOptions().withBackCameraAsDefault();
        Configuration configuration = getConfiguration();
        return new IncomingCallOptions(
                configuration.getWithBackCameraAsDefault(),
                configuration.getWithProximitySensorDisabled(),
                false,
                configuration.getWithCallRating());
    }

    private CallNotificationListener getCallNotificationListener() {
        if (getConfiguration().getUseSimplifiedVersion())
            return new SimpleCallNotificationListener(this);

        // custom notification listener
        return new CallNotificationListener() {
            @Override
            public void onIncomingCall(@NonNull IncomingCall incomingCall, boolean isDnd, boolean isScreenLocked) {
                incomingCall.withConfiguration(new IncomingCallConfiguration(getDefaultCallCapabilitySet(), getDefaultIncomingCallOptionSet()));

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
        if (getConfiguration().getUseSimplifiedVersion())
            return new SimpleChatNotificationListener(this);

        // custom notification listener
        return new ChatNotificationListener() {
            @Override
            public void onIncomingChat(@NonNull IncomingChat chat, boolean isDnd, boolean isScreenLocked) {
                ChatConfiguration chatConfiguration = new ChatConfiguration(getDefaultChatCallCapabilities());
                chat.withConfiguration(chatConfiguration);
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