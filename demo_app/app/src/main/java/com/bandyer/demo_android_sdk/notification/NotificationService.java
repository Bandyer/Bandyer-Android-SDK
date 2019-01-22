/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;

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
     * This function represent the push notification receive callback.
     * The incoming call payload must be extracted from the push notification and the Bandyer SDK must
     * be initialized or resumed if in UNINITIALIZED or PAUSED states.
     */
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        try {
            String payload = notification.payload.additionalData.getString("payload");
            if (payload != null)
                BandyerSDKClient.getInstance().handleNotification(NotificationService.this.getApplicationContext(), payload);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
