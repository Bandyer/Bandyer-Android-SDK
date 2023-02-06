/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.bandyer.android_sdk.call.model.CallInfo;
import com.bandyer.android_sdk.call.notification.CallNotificationListener;
import com.bandyer.android_sdk.call.notification.CallNotificationStyle;
import com.bandyer.android_sdk.call.notification.CallNotificationType;
import com.bandyer.android_sdk.chat.model.ChatInfo;
import com.bandyer.android_sdk.chat.notification.ChatNotificationListener;
import com.bandyer.android_sdk.chat.notification.ChatNotificationStyle;
import com.bandyer.android_sdk.client.BandyerSDK;
import com.bandyer.android_sdk.client.BandyerSDKConfiguration;
import com.bandyer.android_sdk.client.Completion;
import com.bandyer.android_sdk.intent.call.IncomingCall;
import com.bandyer.android_sdk.utils.provider.UserDetails;
import com.bandyer.android_sdk.utils.provider.UserDetailsProvider;
import com.bandyer.demo_android_sdk.storage.DefaultConfigurationManager;
import com.kaleyra.app_configuration.model.Configuration;
import com.kaleyra.app_configuration.model.UserDetailsProviderMode;
import com.kaleyra.app_configuration.utils.MediaStorageUtils;
import com.kaleyra.app_utilities.MultiDexApplication;
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager;
import com.kaleyra.collaboration_suite_networking.Environment;
import com.kaleyra.collaboration_suite_networking.Region;
import com.kaleyra.collaboration_suite_utils.logging.AndroidPriorityLoggerKt;
import com.kaleyra.collaboration_suite_utils.logging.BaseLogger;
import com.kaleyra.collaboration_suite_utils.logging.PriorityLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This custom implementation of Application class is needed to initialize Bandyer SDK module.
 *
 * @author kristiyan
 */
public class App extends MultiDexApplication {

    @Override
    public void create() {
        Configuration appConfiguration = getAppConfiguration();

        if (appConfiguration.isMockConfiguration()) return;

        // Retrieves Environment.Configuration.sandbox() or Environment.Configuration.production()
        // as set in the configuration settings
        String environmentName = appConfiguration.getEnvironment();
        Environment environment;
        if ("production".equals(environmentName)) environment = Environment.Production.INSTANCE;
        else if ("sandbox".equals(environmentName)) environment = Environment.Sandbox.INSTANCE;
        else environment = Environment.Companion.create(environmentName);

        // Retrieves Region.Eu or Region.In or Region.Us
        // as set in the configuration settings
        Region region;
        String regionName = appConfiguration.getRegion().toLowerCase();
        switch (regionName) {
            case "eu":
                region = Region.Eu.INSTANCE;
                break;
            case "in":
                region = Region.In.INSTANCE;
                break;
            case "us":
                region = Region.Us.INSTANCE;
                break;
            default:
                region = Region.Companion.create(regionName);
                break;
        }

        PriorityLogger logger = null;
        if (BuildConfig.DEBUG)
            logger = AndroidPriorityLoggerKt.androidPrioryLogger(BaseLogger.VERBOSE, -1);

        UserDetailsProviderMode userDetailsProviderMode = appConfiguration.getUserDetailsProviderMode();

        if (userDetailsProviderMode == UserDetailsProviderMode.CUSTOM) {
            UserDetailsProvider customUserDetailsProvider = (userAliases, completion) -> {
                String displayName = appConfiguration.getCustomUserDetailsName();
                String displayImageUrl = appConfiguration.getCustomUserDetailsImageUrl();

                Uri customImage = null;
                if (displayImageUrl != null)
                    customImage = MediaStorageUtils.INSTANCE.getUriFromString(appConfiguration.getCustomUserDetailsImageUrl());

                ArrayList<UserDetails> customDetails = new ArrayList<>();
                for (String alias : userAliases) {
                    UserDetails.Builder userDetailsBuilder = new UserDetails.Builder(alias);
                    if (displayName != null) userDetailsBuilder.withNickName(displayName);
                    if (customImage != null) userDetailsBuilder.withImageUri(customImage);
                    customDetails.add(userDetailsBuilder.build());
                }

                completion.success(customDetails);
            };
            BandyerSDK.getInstance().setUserDetailsProvider(customUserDetailsProvider);
        }

        BandyerSDK.getInstance().setUserDetailsFormatter((userDetails, context) -> {
            if (userDetails.getDisplayName() != null && !userDetails.getDisplayName().isEmpty())
                return userDetails.getDisplayName();
            else if (userDetails.getFirstName() != null && userDetails.getLastName() != null
                    && !userDetails.getFirstName().isEmpty() && !userDetails.getLastName().isEmpty())
                return userDetails.getFirstName() + " " + userDetails.getLastName();
            else if (userDetails.getNickName() != null && !userDetails.getNickName().isEmpty())
                return userDetails.getNickName();
            else if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty())
                return userDetails.getNickName();
            else if (userDetails.getFirstName() != null && !userDetails.getFirstName().isEmpty())
                return userDetails.getFirstName();
            else if (userDetails.getLastName() != null && !userDetails.getLastName().isEmpty())
                return userDetails.getLastName();
            else return userDetails.getUserAlias();
        });

        BandyerSDK.getInstance().configure(
                new BandyerSDKConfiguration.Builder(appConfiguration.getAppId(), environment, region)
                        .tools(builder -> {
                            builder.withCall(configurableCall ->
                                    configurableCall.setCallConfiguration(DefaultConfigurationManager.INSTANCE.getDefaultCallConfiguration()));
                            builder.withChat(configurableChat ->
                                    configurableChat.setChatConfiguration(DefaultConfigurationManager.INSTANCE.getDefaultChatConfiguration()));
                        })
                        .notificationListeners(builder -> {
                            builder.setCallNotificationListener(getCallNotificationListener());
                            builder.setChatNotificationListener(getChatNotificationListener());
                        })
                        .httpStack(Companion.getOkHttpClient())
                        .logger(logger)
                        .build()
        );
    }

    private CallNotificationListener getCallNotificationListener() {
        // custom notification listener
        return new CallNotificationListener() {
            @Override
            public void onIncomingCall(@NonNull IncomingCall incomingCall, boolean isDnd, boolean isScreenLocked) {
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
        // custom notification listener
        return new ChatNotificationListener() {
            @Override
            public void onIncomingChat(@NonNull com.bandyer.android_sdk.intent.chat.IncomingChat chat, boolean isDnd, boolean isScreenLocked) {
                chat.asNotification().show(App.this);
            }

            @Override
            public void onCreateNotification(@NonNull ChatInfo chatInfo, @NonNull ChatNotificationStyle notificationStyle) {
                notificationStyle.setNotificationColor(Color.RED);
            }
        };
    }

    private Configuration getAppConfiguration() {
        return ConfigurationPrefsManager.INSTANCE.getConfiguration(this);
    }
}