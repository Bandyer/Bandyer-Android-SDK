/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_utilities.networking

import android.content.Context

interface OnNetworkConnectionChanged {
    fun onNetworkConnectionChanged(context: Context, connected: Boolean)
}