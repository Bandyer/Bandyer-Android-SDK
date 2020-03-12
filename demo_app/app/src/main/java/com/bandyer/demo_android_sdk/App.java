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
import androidx.multidex.MultiDexApplication;

import com.bandyer.android_common.logging.BaseLogger;
import com.bandyer.android_sdk.BandyerSDK;
import com.bandyer.android_sdk.Environment;
import com.bandyer.android_sdk.call.model.CallInfo;
import com.bandyer.android_sdk.call.notification.CallNotificationListener;
import com.bandyer.android_sdk.call.notification.CallNotificationStyle;
import com.bandyer.android_sdk.call.notification.CallNotificationType;
import com.bandyer.android_sdk.chat.ChatInfo;
import com.bandyer.android_sdk.chat.notification.ChatNotificationListener;
import com.bandyer.android_sdk.chat.notification.ChatNotificationStyle;
import com.bandyer.android_sdk.file_sharing.model.FileInfo;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationListener;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationStyle;
import com.bandyer.android_sdk.file_sharing.notification.FileSharingNotificationType;
import com.bandyer.android_sdk.intent.call.CallCapabilities;
import com.bandyer.android_sdk.intent.call.CallOptions;
import com.bandyer.android_sdk.intent.call.IncomingCall;
import com.bandyer.android_sdk.intent.call.IncomingCallIntentOptions;
import com.bandyer.android_sdk.intent.call.IncomingCallOptions;
import com.bandyer.android_sdk.intent.chat.ChatIntentOptions;
import com.bandyer.android_sdk.intent.chat.IncomingChat;
import com.bandyer.android_sdk.intent.file.IncomingFile;
import com.bandyer.android_sdk.notification.NotificationAction;
import com.bandyer.android_sdk.utils.BandyerSDKLogger;
import com.bandyer.demo_android_sdk.mock.MockedUserProvider;
import com.bandyer.demo_android_sdk.mock.MockUserProviderMode;
import com.bandyer.demo_android_sdk.notification.NotificationProxy;
import com.bandyer.demo_android_sdk.utils.LeakCanaryManager;
import com.bandyer.demo_android_sdk.utils.Utils;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.bandyer.demo_android_sdk.utils.storage.DefaultCallSettingsManager;
import com.bandyer.demo_android_sdk.utils.storage.LoginManager;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.GsonBuilder;
import com.jakewharton.processphoenix.ProcessPhoenix;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

/**
 * This custom implementation of Application class is needed to initialize Bandyer SDK module.
 *
 * @author kristiyan
 */
public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // If triggering restart of application skip
        if (ProcessPhoenix.isPhoenixProcess(this)) return;

        // Set LeakCanary
        LeakCanaryManager.enableLeakCanary(ConfigurationPrefsManager.isLeakCanaryEnabled(this));

        // Debug tools
        initStetho();

        // Push notification sample
        initPushNotification();

        // Log crash reports
        initCrashlytics();

        if (ConfigurationPrefsManager.areCredentialsMockedOrEmpty(this)) return;

        String environmentName = ConfigurationPrefsManager.getEnvironmentName(this);

        // Retrieves Environment.Configuration.sandbox() or Environment.Configuration.production()
        // as set in the configuration settings
        Environment env = Utils.getEnvironmentByName(environmentName);

        // Bandyer SDK Module initialization
        BandyerSDK.Builder builder = new BandyerSDK.Builder(this, ConfigurationPrefsManager.getAppId(this))
                .setEnvironment(env)
                .withCallEnabled(getCallNotificationListener())
                .withChatEnabled(getChatNotificationListener())
                .withFileSharingEnabled(getFileSharingNotificationListener());

        // If you desire to personalize the user details shown you should set a provider and formatter
        // otherwise the userAlias and default avatar will be shown in chat or call.

        if (ConfigurationPrefsManager.isMockUserDetailsProviderEnabled(this))
            builder.withUserContactProvider(new MockedUserProvider(this));

        builder.withUserDetailsFormatter((userDetails, context) -> {
            MockUserProviderMode mockedUserProviderMode = MockUserProviderMode.valueOf(ConfigurationPrefsManager.getMockedUserDetailsMode(this));
            switch (mockedUserProviderMode) {
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

        builder.setHttpStackBuilder(new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()))
                .setGsonBuilder(new GsonBuilder().setPrettyPrinting());

        BandyerSDK.init(builder);
    }

    @SuppressLint("NewApi")
    private CallCapabilities getDefaultCallCapabilities() {
        // You may also initialize the callCapabilities without any argument in the constructor
        // You can enable a single capability using the utility methods
        // Example :
        // new CallCapabilities().withChat();
        return new CallCapabilities(DefaultCallSettingsManager.isChatEnabled(this),
                DefaultCallSettingsManager.isFileSharingEnabled(this),
                DefaultCallSettingsManager.isScreenSharingEnabled(this),
                DefaultCallSettingsManager.isWhiteboardEnabled(this));
    }

    private IncomingCallOptions getDefaultIncomingCallOptions() {
        // You may also initialize the callOptions without any argument in the constructor
        // You can enable a single option using the utility methods
        // Example :
        // new IncomingCallOptions().withBackCameraAsDefault();
        return new IncomingCallOptions(
                DefaultCallSettingsManager.isBackCameraAsDefaultEnabled(this),
                DefaultCallSettingsManager.isProximitySensorDisabled(this));
    }

    private CallNotificationListener getCallNotificationListener() {
        return new CallNotificationListener() {

            @Override
            public void onIncomingCall(@NonNull IncomingCall incomingCall, boolean isDnd, boolean isScreenLocked) {
                if (!isDnd || isScreenLocked)
                    incomingCall.show(App.this);
                else {
                    incomingCall.asNotification().show(App.this);
                }
            }

            @Override
            public void onCallActivityStartedFromNotificationAction(@NonNull CallInfo callInfo, @NonNull IncomingCallIntentOptions callIntentOptions) {
                callIntentOptions.withCapabilities(getDefaultCallCapabilities());
                callIntentOptions.withOptions(getDefaultIncomingCallOptions());
            }

            @Override
            public void onCreateNotification(@NonNull CallInfo callInfo,
                                             @NonNull CallNotificationType type,
                                             @NonNull CallNotificationStyle notificationStyle) {
                notificationStyle.setNotificationColor(Color.RED);
            }

            @Override
            public void onNotificationAction(@NonNull final NotificationAction action) {
                // Here you can execute your own code before executing the default action of the notification
                action.execute();
            }
        };
    }

    private ChatNotificationListener getChatNotificationListener() {
        return new ChatNotificationListener() {

            @Override
            public void onIncomingChat(@NonNull IncomingChat chat, boolean isDnd, boolean isScreenLocked) {
                chat.asNotification(App.this).show();
            }

            @Override
            public void onCreateNotification(@NonNull ChatInfo chatInfo, @NonNull ChatNotificationStyle notificationStyle) {

            }

            @Override
            @SuppressLint("NewApi")
            public void onChatActivityStartedFromNotificationAction(@NonNull ChatInfo chatInfo, @NonNull ChatIntentOptions chatIntentOptions) {
                CallCapabilities callCapabilities = getDefaultCallCapabilities();

                // You may also initialize the callOptions without any argument in the constructor
                // You can enable a single option using the utility methods
                // Example :
                // new CallOptions().withBackCameraAsDefault();
                CallOptions callOptions = new CallOptions(
                        DefaultCallSettingsManager.isCallRecordingEnabled(App.this),
                        DefaultCallSettingsManager.isBackCameraAsDefaultEnabled(App.this),
                        DefaultCallSettingsManager.isProximitySensorDisabled(App.this));

                chatIntentOptions
                        .withAudioCallCapability(callCapabilities, callOptions)
                        .withAudioUpgradableCallCapability(callCapabilities, callOptions)
                        .withAudioVideoCallCapability(callCapabilities, callOptions);
            }

            @Override
            public void onNotificationAction(@NonNull final NotificationAction action) {
                // Here you can execute your own code before executing the default action of the notification
                action.execute();
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

            @Override
            public void onNotificationAction(@NonNull final NotificationAction action) {
                // Here you can execute your own code before executing the default action of the notification
                action.execute();
            }
        };
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

    /***************************************Fabric**************************************************
     * Using Crashlytics library to debug potential crashes and handle beta releases.
     * For more information visit:
     * https://fabric.io or https://firebase.google.com/
     **********************************************************************************************/
    private void initCrashlytics() {
        if (!BuildConfig.ENABLE_CRASHLYTICS) return;
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().build()).build());
        String userAlias = LoginManager.getLoggedUser(this);
        if (!userAlias.isEmpty()) Crashlytics.setUserIdentifier(userAlias);
    }

    /*********************************Firebase Cloud Messaging**************************************
     * Using Firebase Cloud Messaging as push notification sample implementation.
     * Push notification are not working in this sample and the implementation of NotificationService
     * class is intended to be used as a sample snippet of code to be used when incoming call notification or a message notification
     * payload is received through your preferred push notification implementation.
     * For more information visit:
     * https://firebase.google.com/docs/cloud-messaging
     **********************************************************************************************/
    private void initPushNotification() {
        NotificationProxy.registerDevice(this);
    }
}
