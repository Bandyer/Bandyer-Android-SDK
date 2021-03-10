/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.networking

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bandyer.app_configuration.external_configuration.model.PushProvider
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager
import com.bandyer.app_utilities.networking.APIClient.getClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box,
 * with the least efforts.
 *
 *
 * MockedNetwork
 *
 * @author kristiyan
 */
object MockedNetwork {
    private var getUsers: Call<BandyerUsers?>? = null
    private var registerDevice: Call<Void?>? = null
    private var unregisterDevice: Call<Void?>? = null

    @JvmStatic
    fun getSampleUsers(context: Activity, callback: GetBandyerUsersCallback?) {
        cancelGetUsers()
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        getUsers = getClient(configuration.apiKey!!, configuration.environment)!!.create(APIInterface::class.java).users
        getUsers!!.enqueue(object : Callback<BandyerUsers?> {
            override fun onResponse(call: Call<BandyerUsers?>, response: Response<BandyerUsers?>) {
                getUsers = null
                if (response.body() == null || response.body()!!.user_id_list == null || !response.isSuccessful) {
                    callback?.onError("No users found or credentials are invalid!")
                    return
                }
                callback?.onUsers(response.body()!!.user_id_list)
            }

            override fun onFailure(call: Call<BandyerUsers?>, t: Throwable) {
                if (call.isCanceled) return
                getUsers = null
                callback?.onError(t.message)
            }
        })
    }

    @JvmStatic
    fun registerDeviceForPushNotification(userAlias: String, pushProvider: PushProvider, devicePushToken: String, apiKey: String, appId: String, environment: String, callback: Callback<Void?>?) {
        cancelRegisterUser()
        val info = DeviceRegistrationInfo(userAlias, appId, devicePushToken, pushProvider.name)
        registerDevice = getClient(apiKey, environment)!!.create(APIInterface::class.java).registerDeviceForPushNotifications(info)
        registerDevice!!.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                registerDevice = null
                callback?.onResponse(call, response)
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                if (call.isCanceled) return
                registerDevice = null
                callback?.onFailure(call, t)
            }
        })
    }

    @JvmStatic
    fun unregisterDeviceForPushNotification(userAlias: String, devicePushToken: String, apiKey: String, appId: String, environment: String) {
        cancelUnRegisterUser()
        unregisterDevice = getClient(apiKey, environment)!!.create(APIInterface::class.java).unregisterDeviceForPushNotifications(userAlias, appId, devicePushToken)
        unregisterDevice!!.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (call.isCanceled) return
                if (!response.isSuccessful) {
                    Log.e("PushNotification", "Failed to unregister device for push notifications!")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                if (call.isCanceled) return
                Log.e("PushNotification", "Failed to unregister device for push notifications!")
            }
        })
    }

    private fun cancelGetUsers() {
        getUsers?.cancel()
        getUsers = null
    }

    private fun cancelUnRegisterUser() {
        unregisterDevice?.cancel()
        unregisterDevice = null
    }

    private fun cancelRegisterUser() {
        registerDevice?.cancel()
        registerDevice = null
    }

    fun cancel() {
        cancelGetUsers()
        cancelRegisterUser()
        cancelUnRegisterUser()
    }

    interface GetBandyerUsersCallback {
        fun onUsers(users: List<String?>?)
        fun onError(error: String?)
    }
}