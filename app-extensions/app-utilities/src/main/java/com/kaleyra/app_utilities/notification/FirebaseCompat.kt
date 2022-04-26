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
package com.kaleyra.app_utilities.notification

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kaleyra.app_configuration.model.PushProvider
import com.kaleyra.app_utilities.MultiDexApplication.Companion.restApi
import com.kaleyra.app_utilities.networking.ConnectionStatusChangeReceiver.Companion.isConnected
import com.kaleyra.app_utilities.networking.ConnectionStatusChangeReceiver.Companion.register
import com.kaleyra.app_utilities.networking.ConnectionStatusChangeReceiver.Companion.unRegister
import com.kaleyra.app_utilities.networking.OnNetworkConnectionChanged
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging

/**
 * This class is for Bandyer usage only
 * You should implement your own logic for notification handling
 *
 * @author kristiyan
 */
object FirebaseCompat {
    private var deviceRegistered = false
    private val onNetworkConnectionChanged: OnNetworkConnectionChanged = object : OnNetworkConnectionChanged {
        override fun onNetworkConnectionChanged(context: Context, connected: Boolean) {
            if (deviceRegistered || !connected) return
            registerDevice(context)
        }
    }

    fun isProcessValid(context: Context) = FirebaseApp.getApps(context).isNotEmpty()

    fun unregisterDevice(context: Context?) {
        if (!deviceRegistered) return
        unRegister(context!!)
        deviceRegistered = false
        try {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult: InstanceIdResult ->
                val post = Thread {
                    val devicePushToken = instanceIdResult.token
                    restApi.unregisterDeviceForPushNotification(devicePushToken)
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId()
                        FirebaseApp.getInstance().delete()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
                post.start()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun registerDevice(context: Context) {
        if (deviceRegistered) return
        register(context, onNetworkConnectionChanged)
        if (!isConnected(context)) return
        refreshConfiguration(context, {
            try {
                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult: InstanceIdResult ->
                    val devicePushToken = instanceIdResult.token
                    restApi.registerDeviceForPushNotification(PushProvider.FCM, devicePushToken)
                }.addOnFailureListener { error: Exception? -> Toast.makeText(context, "Wrong configuration for FCM.\nYou will not receive any notifications!", Toast.LENGTH_LONG).show() }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        })
    }

    /**
     * This is necessary to change the projectId at runtime
     * In normal use-cases you shall not need to refresh the configuration as it will use the google-services.json file.
     */
    fun refreshConfiguration(context: Context, onComplete: Runnable, notifications: Boolean = true) {
        val post = Thread {
            kotlin.runCatching { FirebaseApp.getInstance().delete() }
            kotlin.runCatching {
                val configuration = ConfigurationPrefsManager.getConfiguration(context)
                val app = FirebaseApp.initializeApp(context, FirebaseOptions.Builder()
                        .setGcmSenderId(configuration.projectNumber)
                        .setProjectId(configuration.firebaseProjectId)
                        .setApplicationId(configuration.firebaseMobileAppId!!)
                        .setApiKey(configuration.firebaseApiKey!!)
                        .build())
                FirebaseInstanceId.getInstance(app).instanceId
                if (notifications) FirebaseMessaging.getInstance().isAutoInitEnabled = true
                onComplete.run()
            }.onFailure { it.printStackTrace() }
        }
        post.start()
    }

    fun isGmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            isAvailable = ConnectionResult.SUCCESS == result
        }
        Log.i("PushNotification", "isGmsAvailable: $isAvailable")
        return isAvailable
    }
}