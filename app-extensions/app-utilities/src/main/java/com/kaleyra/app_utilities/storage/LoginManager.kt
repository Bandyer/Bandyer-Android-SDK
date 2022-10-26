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
package com.kaleyra.app_utilities.storage

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kaleyra.app_utilities.notification.NotificationProxy

/**
 * Utility used to remember the logged user, identified by the userAlias
 *
 * @author kristiyan
 */
object LoginManager {

    const val MY_PREFS_NAME = "myPrefs"

    /**
     * Utility to log a user in the application
     *
     * @param context   App or Activity
     * @param userId the user identifier to remember
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun login(context: Context, userId: String) {
        val editor = context.applicationContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("userId", userId)
        editor.commit()

        // Set userAlias on Crashlytics to identify the user in crash logs.
        try {
            FirebaseCrashlytics.getInstance().setUserId(userId)
        } catch (ignored: Throwable) {
        }

        // Register device for receive push notifications
        // It will not work for you, you should implement your own server for notification send/receive logics
        NotificationProxy.registerDevice(context)

        ConfigurationPrefsManager.loginUser(context, userId)
    }

    /**
     * Utility to return the logged user
     *
     * @param context Activity or App
     * @return userAlias
     */
    @JvmStatic
    fun getLoggedUser(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("userId", "")!!
    }

    /**
     * Utility to check whenever the use has been logged or not
     *
     * @param context App or Activity
     * @return true if the user is logged, false otherwise
     */
    @JvmStatic
    fun isUserLogged(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("userId", "") != ""
    }

    /**
     * Utility to logout the user from the application
     *
     * @param context BaseActivity
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun logout(context: Context) {

        // unregister device for push notifications
        // It will not work for you, you should implement your own server for notification send/receive logics
        NotificationProxy.unregisterDevice(context)

        // Remove userAlias on Crashlytics to identify the user in crash logs.
        try {
            FirebaseCrashlytics.getInstance().setUserId("")
        } catch (ignored: Throwable) {
        }
        val prefs = context.applicationContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        ConfigurationPrefsManager.logoutUser(context)
    }
}