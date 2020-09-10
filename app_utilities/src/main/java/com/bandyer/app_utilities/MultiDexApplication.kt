package com.bandyer.app_utilities

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.bandyer.app_utilities.notification.FirebaseCompat
import com.bandyer.app_utilities.notification.NotificationProxy
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager
import com.bandyer.app_utilities.storage.LoginManager
import com.facebook.stetho.Stetho
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.processphoenix.ProcessPhoenix

/**
 *
 * @author kristiyan
 */
abstract class MultiDexApplication : MultiDexApplication() {

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
    
    override fun onCreate() {
        super.onCreate()

        // If triggering restart of application skip
        if (ProcessPhoenix.isPhoenixProcess(this)) return

        // Log crash reports
        initCrashlytics()

        // Set LeakCanary
        LeakCanaryManager.enableLeakCanary(ConfigurationPrefsManager.getConfiguration(this).useLeakCanary)

        // Debug tools
        initStetho()

        // Debug tools
        registerDevicePushToken()

        create()
    }

    abstract fun create();

    /***************************************Stetho**************************************************
     * Using Stetho to debug networking data in a easy way
     *
     * For more information visit:
     * https://github.com/facebook/stetho
     */
    private fun initStetho() {
        Stetho.initializeWithDefaults(this)
    }


    /***************************************Fabric**************************************************
     * Using Crashlytics library to debug potential crashes and handle beta releases.
     * For more information visit:
     * https://fabric.io or https://firebase.google.com/
     */
    protected fun initCrashlytics() {
        if (BuildConfig.DEBUG) return
        val userAlias = LoginManager.getLoggedUser(this)
        if (userAlias.isNotEmpty()) {
            FirebaseCompat.refreshConfiguration(this, Runnable {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
                FirebaseCrashlytics.getInstance().setUserId(userAlias)
            }, false)
        } else {
            FirebaseCompat.refreshConfiguration(this, Runnable {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            }, false)
        }
    }

    /*********************************Firebase Cloud Messaging**************************************
     * Using Firebase Cloud Messaging as push notification sample implementation.
     * Push notification are not working in this sample and the implementation of NotificationService
     * class is intended to be used as a sample snippet of code to be used when incoming call notification or a message notification
     * payload is received through your preferred push notification implementation.
     * For more information visit:
     * https://firebase.google.com/docs/cloud-messaging
     */
    protected fun registerDevicePushToken() {
        NotificationProxy.registerDevice(this)
    }
}