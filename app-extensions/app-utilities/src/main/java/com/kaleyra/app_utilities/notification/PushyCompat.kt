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

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kaleyra.app_configuration.model.PushProvider
import com.kaleyra.app_utilities.MultiDexApplication.Companion.restApi
import com.kaleyra.app_utilities.activities.BaseActivity
import me.pushy.sdk.Pushy

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
                restApi.registerDeviceForPushNotification(PushProvider.Pushy, devicePushToken)
                return
            }
            AsyncTask.execute {
                try {
                    val devicePushToken = Pushy.register(context)
                    restApi.registerDeviceForPushNotification(PushProvider.Pushy, devicePushToken)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }

        @JvmStatic
        fun unregisterDevice(context: Context) {
            if (!Pushy.isRegistered(context)) return
            val devicePushToken = Pushy.getDeviceCredentials(context).token
            restApi.unregisterDeviceForPushNotification(devicePushToken)
            Pushy.toggleNotifications(false, context)
            Pushy.unregister(context)
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