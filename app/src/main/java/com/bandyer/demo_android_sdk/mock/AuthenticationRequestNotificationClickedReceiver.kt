/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bandyer.demo_android_sdk.mock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bandyer.android_sdk.client.BandyerSDK.Companion.getInstance
import com.bandyer.android_sdk.intent.call.CallDisplayMode
import com.bandyer.demo_android_sdk.mock.MockUserAuthenticationRequestActivity

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
        val authenticationIntent = Intent(context, com.bandyer.demo_android_sdk.mock.MockUserAuthenticationRequestActivity::class.java)
        authenticationIntent.setPackage(context.packageName)
        authenticationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(authenticationIntent)
    }
}