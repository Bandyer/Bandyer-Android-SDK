/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bandyer.android_sdk.client.BandyerSDKClient.Companion.getInstance
import com.bandyer.android_sdk.intent.call.CallDisplayMode
import com.bandyer.app_utilities.activities.MockUserAuthenticationRequestActivity

/**
 * This broadcast receiver intercept click on mocked biometric user authentication request.
 * When triggered it will be shown a mocked activity with biometric prompt.
 * To prevent unwanted cancellation of biometric prompt no UI can be put above it so the call must
 * disable the picture in picture feature.
 * The picture in picture can be enabled again after the biometric verification has concluded as shown
 * in MockedUserAuthenticationRequestActivity class.
 */
class AuthenticationRequestNotificationClickedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val callModule = getInstance().callModule ?: return
        val ongoingCall = callModule.ongoingCall ?: return
        callModule.setDisplayMode(ongoingCall, CallDisplayMode.BACKGROUND)
        val authenticationIntent = Intent(context, MockUserAuthenticationRequestActivity::class.java)
        authenticationIntent.setPackage(context.packageName)
        authenticationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(authenticationIntent)
    }
}