/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.storage

import android.annotation.SuppressLint
import android.content.Context
import com.bandyer.app_utilities.activities.BaseActivity
import com.bandyer.app_utilities.notification.NotificationProxy
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Utility used to remember the logged user, identified by the userAlias
 *
 * @author kristiyan
 */
object LoginManager {

    private const val MY_PREFS_NAME = "myPrefs"

    /**
     * Utility to log a user in the application
     *
     * @param context   App or Activity
     * @param userAlias the user identifier to remember
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun login(context: Context, userAlias: String?) {
        val editor = context.applicationContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("userAlias", userAlias)
        editor.commit()

        // Set userAlias on Crashlytics to identify the user in crash logs.
        try {
            FirebaseCrashlytics.getInstance().setUserId(userAlias!!);
        } catch (ignored: Exception) {
        }

        // Register device for receive push notifications
        // It will not work for you, you should implement your own server for notification send/receive logics
        NotificationProxy.registerDevice(context)
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
        return prefs.getString("userAlias", "")!!
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
        return prefs.getString("userAlias", "") != ""
    }

    /**
     * Utility to logout the user from the application
     *
     * @param context BaseActivity
     */
    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun logout(context: BaseActivity) {

        // unregister device for push notifications
        // It will not work for you, you should implement your own server for notification send/receive logics
        NotificationProxy.unregisterDevice(context, getLoggedUser(context))

        // Remove userAlias on Crashlytics to identify the user in crash logs.
        try {
            FirebaseCrashlytics.getInstance().setUserId("")
        } catch (ignored: Exception) {
        }
        val prefs = context.applicationContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }
}