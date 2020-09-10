package com.bandyer.app_utilities.networking

import android.content.Context

interface OnNetworkConnectionChanged {
    fun onNetworkConnectionChanged(context: Context, connected: Boolean)
}