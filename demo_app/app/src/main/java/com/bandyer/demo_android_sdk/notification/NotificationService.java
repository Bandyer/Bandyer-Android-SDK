/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import android.support.annotation.NonNull;

import com.bandyer.android_sdk.BandyerComponent;
import com.bandyer.android_sdk.call.CallModule;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.client.BandyerSDKClientOptions;
import com.bandyer.android_sdk.module.BandyerModule;
import com.bandyer.android_sdk.module.BandyerModuleObserver;
import com.bandyer.android_sdk.module.BandyerModuleStatus;
import com.bandyer.demo_android_sdk.utils.LoginManager;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Sample implementation of a push notification receiver that handles incoming calls.
 * Push notification are not working in this sample and this class is intended to be used as a
 * sample snippet of code to be used when incoming call notification payloads are received through
 * your preferred push notification implementation.
 * The sample is based on One Signal implementation but can be easily applied to other
 * push notification libraries.
 */
public class NotificationService extends NotificationExtenderService {

    /**
     * String representation of the incoming call payload, received as additional data inside push notification.
     */
    private String payload = null;

    /**
     * This instance of BandyerSDKClientObserver is needed to observe when the Bandyer SDK is ready to
     * manage incoming calls.
     * Bandyer SDK client must be initialized if the push has woke up the application from killed state.
     */
    private BandyerModuleObserver bandyerModuleObserver = new BandyerModuleObserver() {


        @Override
        public void onModuleStatusChanged(@NonNull BandyerModule module, @NonNull BandyerModuleStatus moduleStatus) {

        }

        @Override
        public void onModuleFailed(@NonNull BandyerModule module, @NonNull Throwable throwable) {

        }

        @Override
        public void onModulePaused(@NonNull BandyerModule module) {

        }

        @Override
        public void onModuleReady(@NonNull BandyerModule module) {
            if (module instanceof CallModule) {
                if (payload == null) return;
                BandyerSDKClient.getInstance().removeModuleObserver(this);
                BandyerSDKClient.getInstance().handleNotification(NotificationService.this.getApplicationContext(), payload);
                payload = null;
            }
        }
    };

    /**
     * This function represent the push notification receive callback.
     * The incoming call payload must be extracted from the push notification and the Bandyer SDK must
     * be initialized or resumed if in UNINITIALIZED or PAUSED states.
     */
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {

        payload = notification.payload.body;
        /*
         * Your desired alias to log in when application is killed and a push notification of an incoming call is received.
         */
        startBandyerSdkIfNeeded(LoginManager.getLoggedUser(getApplicationContext()));
        return true;
    }

    /**
     * This function checks if the Bandyer SDK needs to be initialized or resumed before handling push notification's
     * incoming call payload.
     * The restart or resume process is observed with bandyerSDKClientObserver instance.
     * Ensure that the observer is added once, if receiving subsequent push notifications, by removing it before.
     *
     * @param userAlias the user alias to be user to perform init.
     */
    private void startBandyerSdkIfNeeded(String userAlias) {

        BandyerSDKClient.getInstance().removeModuleObserver(bandyerModuleObserver);

        switch (BandyerSDKClient.getInstance().getState()) {

            case RUNNING:
                return;

            case PAUSED:
                BandyerSDKClient.getInstance().addModuleObserver(bandyerModuleObserver);
                BandyerSDKClient.getInstance().resume();
                break;

            case UNINITIALIZED:
                BandyerSDKClientOptions sdkClientOptions = new BandyerSDKClientOptions.Builder().build();
                BandyerSDKClient.getInstance().init(userAlias, sdkClientOptions);
                BandyerSDKClient.getInstance().addModuleObserver(bandyerModuleObserver);
                BandyerSDKClient.getInstance().startListening();
                break;

        }
    }
}
