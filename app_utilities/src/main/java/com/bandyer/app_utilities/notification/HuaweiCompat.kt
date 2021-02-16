/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_utilities.notification

import android.content.Context
import android.util.Log
import com.bandyer.app_configuration.external_configuration.model.PushProvider
import com.bandyer.app_utilities.networking.MockedNetwork
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability
import com.huawei.hms.push.HmsMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object HuaweiCompat {

    @JvmStatic
    fun registerDevice(context: Context, loggedUser: String) {
        Thread {
            val instanceId = getInstance(context) ?: return@Thread
            val configuration = ConfigurationPrefsManager.getConfiguration(context)
            configuration.hmsAppId ?: return@Thread
            val pushToken: String = instanceId.getToken(configuration.hmsAppId, HmsMessaging.DEFAULT_TOKEN_SCOPE)

            MockedNetwork.registerDeviceForPushNotification(loggedUser, PushProvider.HMS, pushToken, configuration.apiKey!!, configuration.appId!!, configuration.environment!!, object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    if (!response.isSuccessful) {
                        Log.e("PushNotification", "Failed to register device for push notifications!")
                        return@onResponse
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Log.e("PushNotification", "Failed to register device for push notifications!")
                }
            })
        }.start()
    }

    fun unregisterDevice(context: Context, loggedUser: String?) {
        loggedUser ?: return
        Thread {
            val instanceId = getInstance(context) ?: return@Thread
            kotlin.runCatching {
                val configuration = ConfigurationPrefsManager.getConfiguration(context)
                configuration.hmsAppId ?: return@Thread
                val pushToken: String = instanceId.getToken(configuration.hmsAppId, HmsMessaging.DEFAULT_TOKEN_SCOPE)
                instanceId.deleteToken(configuration.hmsAppId, HmsMessaging.DEFAULT_TOKEN_SCOPE)
                MockedNetwork.unregisterDeviceForPushNotification(loggedUser, pushToken, configuration.apiKey!!, configuration.appId!!, configuration.environment!!)
            }
        }.start()
    }

    private fun getInstance(context: Context) = kotlin.runCatching {
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        HmsInstanceId.getInstance(context.applicationContext).apply {
            c::class.java.getDeclaredField("mAppID").apply {
                isAccessible = true
                set(c, configuration.hmsAppId)
            }
        }
    }.getOrNull()


    fun isHmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)
            isAvailable = ConnectionResult.SUCCESS == result
        }
        return isAvailable
    }
}