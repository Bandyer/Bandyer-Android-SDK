/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.receivers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bandyer.android_sdk.call.CallException;
import com.bandyer.android_sdk.intent.call.Call;
import com.bandyer.demo_android_sdk.mock.BiometricNotificationScheduler;


/**
 * A broadcast receiver that receive broadcast when a call starts, ends or ends with error.
 */
public class CallEventBroadcastReceiver extends com.bandyer.android_sdk.call.notification.CallEventBroadcastReceiver {

    public static final String TAG = "CALL EVENT";

    /**
     * A scheduler to show a notification that once opened will request biometric authorization
     */
    public BiometricNotificationScheduler biometricNotificationScheduler = new BiometricNotificationScheduler();

    @Override
    public void onCallCreated(@NonNull Call ongoingCall) {
    }

    @Override
    public void onCallStarted(@NonNull Call ongoingCall) {
        String callId = ongoingCall.getCallInfo().getCallId();
        Log.d(TAG, "Call " + callId + " started.");
        biometricNotificationScheduler.schedule(context);
    }

    @Override
    public void onCallEnded(@NonNull Call ongoingCall) {
        String callId = ongoingCall.getCallInfo().getCallId();
        Log.d(TAG, "Call " + callId + " ended.");
        biometricNotificationScheduler.cancelUserAuthenticationRequest(context);
    }

    @Override
    public void onCallEndedWithError(@NonNull Call ongoingCall, @NonNull CallException callException) {
        String callId = ongoingCall.getCallInfo().getCallId();
        Log.d(TAG, "Call " + callId + " ended with error: " + callException.getMessage());
        biometricNotificationScheduler.cancelUserAuthenticationRequest(context);
    }
}
