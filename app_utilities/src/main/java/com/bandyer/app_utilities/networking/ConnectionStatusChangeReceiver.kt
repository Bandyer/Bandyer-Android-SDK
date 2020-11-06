/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_utilities.networking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * ConnectionStatusChangeReceiver
 * Sends broadcast on connection change.
 */
class ConnectionStatusChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION != intent.action) return
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return
        val hasInternetConnection = isNetworkAvailable(context)
        if (mIsConnected != null && mIsConnected == hasInternetConnection) return
        mIsConnected = hasInternetConnection
        if (isInitialStickyBroadcast) return
        mOnNetworkConnectionChanged!!.onNetworkConnectionChanged(context, hasInternetConnection)
    }

    companion object {
        private var mIsConnected: Boolean? = null
        private var mOnNetworkConnectionChanged: OnNetworkConnectionChanged? = null
        private var connectionStatusReceivedRegistered = false
        private val connectionStatusChangeReceiver = ConnectionStatusChangeReceiver()
        @JvmStatic
        fun register(context: Context, onNetworkConnectionChanged: OnNetworkConnectionChanged?) {
            if (connectionStatusReceivedRegistered) return
            connectionStatusReceivedRegistered = true
            mOnNetworkConnectionChanged = onNetworkConnectionChanged
            context.applicationContext.registerReceiver(connectionStatusChangeReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        }

        @JvmStatic
        fun unRegister(context: Context) {
            if (!connectionStatusReceivedRegistered) return
            connectionStatusReceivedRegistered = false
            mOnNetworkConnectionChanged = null
            try {
                context.applicationContext.unregisterReceiver(connectionStatusChangeReceiver)
            } catch (ignored: Throwable) {
                // ignored
            }
        }

        @JvmStatic
        fun isConnected(context: Context): Boolean {
            return if (mIsConnected == null) isNetworkAvailable(context.applicationContext) else mIsConnected!!
        }

        private fun isNetworkAvailable(context: Context?): Boolean {
            if (context == null) return false
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
            } else {
                try {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo
                    return activeNetworkInfo != null && activeNetworkInfo.isConnected
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return false
        }
    }
}