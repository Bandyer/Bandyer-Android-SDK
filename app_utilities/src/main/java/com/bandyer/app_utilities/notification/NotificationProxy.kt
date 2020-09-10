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

    fun registerDevice(context: Context) {
        if (!isUserLogged(context)) return
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        when (configuration.pushProvider) {
            PushProvider.FCM -> FirebaseCompat.registerDevice(context)
            PushProvider.Pushy -> PushyCompat.registerDevice(context)
        }
    }

    fun unregisterDevice(context: BaseActivity, loggedUser: String?) {
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        when (configuration.pushProvider) {
            PushProvider.FCM -> FirebaseCompat.unregisterDevice(context, loggedUser)
            PushProvider.Pushy -> PushyCompat.unregisterDevice(context, loggedUser)
        }
    }

    @JvmStatic
    fun listen(context: BaseActivity) {
        val configuration = ConfigurationPrefsManager.getConfiguration(context)
        if (configuration.pushProvider != PushProvider.Pushy) return
        PushyCompat.listen(context)
    }
}