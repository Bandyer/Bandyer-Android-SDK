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
import com.kaleyra.app_configuration.model.PushProvider
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.app_utilities.storage.LoginManager.isUserLogged
import com.kaleyra.app_utilities.activities.BaseActivity

/**
 * This class is a proxy to the different notification services we want to show case
 * At the moment we implemented the FCM ( standard ) which works everywhere except for China.
 * For China we show case the Pushy library.
 */
object NotificationProxy {

    fun registerDevice(context: Context) {
        if (!isUserLogged(context)) return
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        when (configuration.pushProvider) {
            PushProvider.FCM -> {
                if (FirebaseCompat.isGmsAvailable(context)) FirebaseCompat.registerDevice(context)
                else if (HuaweiCompat.isHmsAvailable(context)) HuaweiCompat.registerDevice(context)
            }
            PushProvider.Pushy -> PushyCompat.registerDevice(context)
            PushProvider.HMS -> if (HuaweiCompat.isHmsAvailable(context)) HuaweiCompat.registerDevice(context)
            PushProvider.NONE -> Unit
        }
    }

    fun unregisterDevice(context: Context) {
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        when (configuration.pushProvider) {
            PushProvider.FCM -> {
                if (FirebaseCompat.isGmsAvailable(context)) FirebaseCompat.unregisterDevice(context)
                else if (HuaweiCompat.isHmsAvailable(context)) HuaweiCompat.unregisterDevice(context)
            }
            PushProvider.Pushy -> PushyCompat.unregisterDevice(context)
            PushProvider.HMS -> if (HuaweiCompat.isHmsAvailable(context)) HuaweiCompat.unregisterDevice(context)
            PushProvider.NONE -> Unit
        }
    }

    @JvmStatic
    fun listen(context: BaseActivity) {
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        if (configuration.pushProvider != PushProvider.Pushy) return
        PushyCompat.listen(context)
    }
}