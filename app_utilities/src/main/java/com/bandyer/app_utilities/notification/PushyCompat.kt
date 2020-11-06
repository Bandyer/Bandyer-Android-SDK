/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_utilities.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bandyer.app_utilities.storage.LoginManager.getLoggedUser
import com.bandyer.app_utilities.activities.BaseActivity
import com.bandyer.app_utilities.networking.MockedNetwork.registerDeviceForPushNotification
import com.bandyer.app_utilities.networking.MockedNetwork.unregisterDeviceForPushNotification
import me.pushy.sdk.Pushy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * This class show cases a China compatible notification service.
 * For more info see at https://pushy.me/docs
 */
open class PushyCompat : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
    }

    companion object {
        private val TAG = PushyCompat::class.java.simpleName

        @JvmStatic
        fun registerDevice(context: Context) {
            Pushy.toggleNotifications(true, context)
            if (Pushy.isRegistered(context)) {
                val devicePushToken = Pushy.getDeviceCredentials(context).token
                registerDeviceForPushNotification(context, getLoggedUser(context!!), devicePushToken, callback)
                return
            }
            AsyncTask.execute {
                try {
                    val devicePushToken = Pushy.register(context)
                    registerDeviceForPushNotification(context, getLoggedUser(context!!), devicePushToken, callback)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }

        private val callback: Callback<Void?> = object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (!response.isSuccessful) {
                    Log.e("PushNotification", "Failed to register device for push notifications!")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e("PushNotification", "Failed to register device for push notifications!")
            }
        }

        @JvmStatic
        fun unregisterDevice(context: BaseActivity, loggedUser: String?) {
            if (!Pushy.isRegistered(context)) return
            val devicePushToken = Pushy.getDeviceCredentials(context).token
            unregisterDeviceForPushNotification(context, loggedUser, devicePushToken)
            Pushy.toggleNotifications(false, context)
            // Every unregister will generate a new token, resulting in a new device usage
            // Pushy.unregister(context);
        }

        @JvmStatic
        fun listen(context: BaseActivity) {
            if (!Pushy.isRegistered(context)) return
            Pushy.listen(context)
            if (ContextCompat.checkSelfPermission(context.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) return

            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage and no new device is registered every time
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }
}