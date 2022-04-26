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

package com.kaleyra.app_utilities

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.processphoenix.ProcessPhoenix
import com.kaleyra.app_utilities.networking.RestApi
import com.kaleyra.app_utilities.notification.FirebaseCompat
import com.kaleyra.app_utilities.notification.NotificationProxy
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.app_utilities.storage.LoginManager
import okhttp3.OkHttpClient

/**
 *
 * @author kristiyan
 */
abstract class MultiDexApplication : MultiDexApplication() {

    companion object {
        private var application: Application? = null
        var okHttpClient = OkHttpClient()
        @JvmStatic
        val restApi by lazy { RestApi(application!!) }
    }

    override fun onCreate() {
        super.onCreate()

        application = this

        // If triggering restart of application skip
        if (ProcessPhoenix.isPhoenixProcess(this)) return

        // Log crash reports
        initCrashlytics()

        // Set LeakCanary
        LeakCanaryManager.enableLeakCanary(ConfigurationPrefsManager.getConfiguration(this).useLeakCanary)

        // Debug tools
        initFlipper()

        // Debug tools
        registerDevicePushToken()

        // Vector assets Resources NotFoundException on Samsung device
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        create()
    }

    abstract fun create();

    /***************************************Flipper**************************************************
     * Using Flipper to debug app in a easy way
     *
     * For more information visit:
     * https://github.com/facebook/flipper
     */
    private fun initFlipper() {
        FlipperManager.enable(this)
    }


    /***************************************Fabric**************************************************
     * Using Crashlytics library to debug potential crashes and handle beta releases.
     * For more information visit:
     * https://fabric.io or https://firebase.google.com/
     */
    protected fun initCrashlytics() {
        if (BuildConfig.DEBUG) return
        val userAlias = LoginManager.getLoggedUser(this)
        val configuration = ConfigurationPrefsManager.getConfiguration(this)
        if (configuration.firebaseApiKey.isNullOrBlank()) return
        if (!FirebaseCompat.isProcessValid(this)) return
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