/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.networking

/**
 * @author kristiyan
 */
class DeviceRegistrationInfo(var user_alias: String, var app_id: String, var push_token: String, var push_provider: String) {
    var platform = "android"

}