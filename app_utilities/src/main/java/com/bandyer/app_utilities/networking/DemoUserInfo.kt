/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.networking

import com.google.gson.annotations.SerializedName

/**
 * @author kristiyan
 */
class DemoUserInfo {
    @JvmField
    @SerializedName("seed")
    var userAlias: String? = null
}