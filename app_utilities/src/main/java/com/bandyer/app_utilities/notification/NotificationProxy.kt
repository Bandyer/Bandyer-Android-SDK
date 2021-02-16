/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_utilities.notification

import android.content.Context
import com.bandyer.app_configuration.external_configuration.model.PushProvider
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager
import com.bandyer.app_utilities.storage.LoginManager.isUserLogged
import com.bandyer.app_utilities.activities.BaseActivity

/**
 * This class is a proxy to the different notification services we want to show case
 * At the moment we implemented the FCM ( standard ) which works everywhere except for China.
 * For China we show case the Pushy library.
 */
object NotificationProxy {

    fun registerDevice(context: Context, loggedUser: String) {
        if (!isUserLogged(context)) return
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        when (configuration.pushProvider) {
            PushProvider.FCM -> {
                if (FirebaseCompat.isGmsAvailable(context)) FirebaseCompat.registerDevice(context, loggedUser)
                else if (HuaweiCompat.isHmsAvailable(context)) HuaweiCompat.registerDevice(context, loggedUser)
            }
            PushProvider.Pushy -> PushyCompat.registerDevice(context, loggedUser)
            PushProvider.HMS -> if (HuaweiCompat.isHmsAvailable(context)) HuaweiCompat.registerDevice(context, loggedUser)
            PushProvider.NONE -> Unit
        }
    }

    fun unregisterDevice(context: BaseActivity, loggedUser: String?) {
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        when (configuration.pushProvider) {
            PushProvider.FCM -> {
                if (FirebaseCompat.isGmsAvailable(context)) FirebaseCompat.unregisterDevice(context, loggedUser)
                else if (HuaweiCompat.isHmsAvailable(context)) HuaweiCompat.unregisterDevice(context, loggedUser)
            }
            PushProvider.Pushy -> PushyCompat.unregisterDevice(context, loggedUser)
            PushProvider.HMS -> if (HuaweiCompat.isHmsAvailable(context)) HuaweiCompat.unregisterDevice(context, loggedUser)
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