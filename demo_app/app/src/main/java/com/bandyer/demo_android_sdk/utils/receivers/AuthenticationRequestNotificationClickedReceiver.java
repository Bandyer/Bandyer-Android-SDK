/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bandyer.android_sdk.call.CallModule;
import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.intent.call.Call;
import com.bandyer.android_sdk.intent.call.CallDisplayMode;
import com.bandyer.demo_android_sdk.utils.activities.MockUserAuthenticationRequestActivity;

/**
 * This broadcast receiver intercept click on mocked biometric user authentication request.
 * When triggered it will be shown a mocked activity with biometric prompt.
 * To prevent unwanted cancellation of biometric prompt no UI can be put above it so the call must
 * disable the picture in picture feature.
 * The picture in picture can be enabled again after the biometric verification has concluded as shown
 * in MockedUserAuthenticationRequestActivity class.
 */
public class AuthenticationRequestNotificationClickedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CallModule callModule = BandyerSDKClient.getInstance().getCallModule();
        if (callModule == null) return;

        Call ongoingCall = callModule.getOngoingCall();
        if (ongoingCall == null) return;

        callModule.setDisplayMode(ongoingCall, CallDisplayMode.BACKGROUND);
        Intent authenticationIntent = new Intent(context, MockUserAuthenticationRequestActivity.class);
        authenticationIntent.setPackage(context.getPackageName());
        authenticationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(authenticationIntent);
    }
}
