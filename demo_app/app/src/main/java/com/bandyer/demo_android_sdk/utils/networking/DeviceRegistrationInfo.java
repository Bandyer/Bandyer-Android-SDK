/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.networking;

/**
 * @author kristiyan
 */
public class DeviceRegistrationInfo {
    String user_alias;
    String app_id;
    String push_token;
    String platform = "android";

    public DeviceRegistrationInfo(String user_alias, String app_id, String push_token) {
        this.user_alias = user_alias;
        this.app_id = app_id;
        this.push_token = push_token;
    }
}
