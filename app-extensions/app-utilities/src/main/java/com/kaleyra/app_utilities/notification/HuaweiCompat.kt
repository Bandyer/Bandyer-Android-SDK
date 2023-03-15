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
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.api.Api.ApiOptions.NoOptions
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability
import com.huawei.hms.common.HuaweiApi
import com.huawei.hms.push.HmsMessaging
import com.kaleyra.app_configuration.model.PushProvider
import com.kaleyra.app_utilities.MultiDexApplication.Companion.restApi
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager

object HuaweiCompat {

    @JvmStatic
    fun registerDevice(context: Context) {
        Thread {
            val instanceId = getInstance(context) ?: return@Thread
            val configuration = ConfigurationPrefsManager.getConfiguration(context)
            configuration.hmsAppId ?: return@Thread
            val devicePushToken: String = instanceId.getToken(configuration.hmsAppId, HmsMessaging.DEFAULT_TOKEN_SCOPE)
            restApi.registerDeviceForPushNotification(PushProvider.HMS, devicePushToken)
        }.start()
    }

    fun unregisterDevice(context: Context) {
        Thread {
            val instanceId = getInstance(context) ?: return@Thread
            kotlin.runCatching {
                val configuration = ConfigurationPrefsManager.getConfiguration(context)
                configuration.hmsAppId ?: return@Thread
                val pushToken: String = instanceId.getToken(configuration.hmsAppId, HmsMessaging.DEFAULT_TOKEN_SCOPE)
                instanceId.deleteToken(configuration.hmsAppId, HmsMessaging.DEFAULT_TOKEN_SCOPE)
                restApi.unregisterDeviceForPushNotification(pushToken)
            }
        }.start()
    }

    private fun getInstance(context: Context) = kotlin.runCatching {
        val configuration = ConfigurationPrefsManager.getConfiguration(context)

        HmsInstanceId.getInstance(context.applicationContext).apply {
            val huaweiApiField = this.javaClass.getDeclaredField("c").apply { isAccessible = true }
            val huaweiApi = huaweiApiField.get(this) as HuaweiApi<NoOptions>
            val huaweiAppIDField1 = huaweiApi.javaClass.getDeclaredField("e").apply { isAccessible = true }
            val huaweiAppIDField2 = huaweiApi.javaClass.getDeclaredField("f").apply { isAccessible = true }
            huaweiAppIDField1.set(huaweiApi, configuration.hmsAppId)
            huaweiAppIDField2.set(huaweiApi, configuration.hmsAppId)
        }
    }.getOrNull()

    fun isHmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result = kotlin.runCatching {  HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)}.getOrDefault(ConnectionResult.API_UNAVAILABLE)
            isAvailable = ConnectionResult.SUCCESS == result
        }
        return isAvailable
    }
}