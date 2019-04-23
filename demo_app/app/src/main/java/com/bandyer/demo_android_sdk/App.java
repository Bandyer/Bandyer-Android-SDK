/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.bandyer.android_sdk.BandyerSDK;
import com.bandyer.android_sdk.BuildConfig;
import com.bandyer.android_sdk.Environment;
import com.bandyer.android_sdk.FormatContext;
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
import com.bandyer.android_sdk.intent.call.CallIntentOptions;
import com.bandyer.android_sdk.intent.call.IncomingCall;
import com.bandyer.android_sdk.intent.chat.ChatIntentOptions;
import com.bandyer.android_sdk.intent.chat.IncomingChat;
import com.bandyer.android_sdk.notification.NotificationAction;
import com.bandyer.android_sdk.utils.BandyerSDKLogger;
import com.bandyer.android_sdk.utils.provider.UserDetails;
import com.bandyer.android_sdk.utils.provider.UserDetailsFormatter;
import com.bandyer.demo_android_sdk.mock.MockedUserProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.GsonBuilder;
import com.onesignal.OneSignal;
import com.squareup.leakcanary.LeakCanary;

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

        // Debug tools
        if (LeakCanary.isInAnalyzerProcess(this)) return;
        initLeakCanary();
        initStetho();

        // Push notification sample
        initPushNotification();

        // Bandyer SDK Module initialization
        BandyerSDK.Builder builder = new BandyerSDK.Builder(this, getString(R.string.app_id))
                .withUserContactProvider(new MockedUserProvider())
                .withUserDetailsFormatter(new UserDetailsFormatter() {
                    @NonNull
                    @Override
                    public String format(@NonNull UserDetails userDetails, @NonNull FormatContext context) {
                        return "Operator " + userDetails.getFirstName();
                    }
                })
                .withFileSharingEnabled(getFileSharingNotificationListener())
                .withCallEnabled(getCallNotificationListener())
                .setEnvironment(Environment.Configuration.sandbox());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            builder.withChatEnabled(getChatNotificationListener())
                    .withWhiteboardEnabled();
        }

        if (BuildConfig.DEBUG) {
            builder.setLogger(new BandyerSDKLogger() {
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


    private CallNotificationListener getCallNotificationListener() {
        return new CallNotificationListener() {

            @Override
            public void onIncomingCall(@NonNull IncomingCall incomingCall, boolean isDnd, boolean isScreenLocked) {
                if (!isDnd || isScreenLocked)
                    startActivity(incomingCall.asActivityIntent(App.this));
                else
                    incomingCall.asNotification(App.this).show();
            }

            @Override
            public void onCallActivityStartedFromNotificationAction(@NonNull CallInfo callInfo,
                                                                    @NonNull CallIntentOptions callIntentOptions) {
                callIntentOptions
                        .withChatCapability()
                        .withFileSharingCapability()
                        .withWhiteboardCapability();
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
            public void onChatActivityStartedFromNotificationAction(@NonNull ChatInfo chatInfo, @NonNull ChatIntentOptions chatIntentOptions) {
                chatIntentOptions
                        .withAudioCallCapability(false, true)
                        .withWhiteboardInCallCapability()
                        .withFileSharingInCallCapability()
                        .withAudioVideoCallCapability(false);
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
            public void onCreateNotification(@NonNull FileInfo fileInfo, @NonNull FileSharingNotificationType notificationType, @NonNull FileSharingNotificationStyle notificationStyle) {
                notificationStyle.setNotificationColor(Color.GREEN);
            }

            @Override
            public void onNotificationAction(@NonNull final NotificationAction action) {
                // Here you can execute your own code before executing the default action of the notification
                action.execute();
            }
        };
    }

    /***************************************LeackCanary*********************************************
     * Using LeakCanary library to debug potential leaks.
     * Leaks may lead to your application consuming & retaining memory inefficiently, making the device and the application slower and crash prone
     * For more information visit:
     * https://github.com/square/leakcanary
     **********************************************************************************************/

    private void initLeakCanary() {
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


    /***************************************One Signal**************************************************
     * Using One Signal as push notification sample implementation.
     * Push notification are not working in this sample and the implementation of NotificationService
     * class is intended to be used as a sample snippet of code to be used when incoming call notification
     * payload is received through your preferred push notification implementation.
     * For more information visit:
     * https://onesignal.com/
     **********************************************************************************************/
    private void initPushNotification() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
