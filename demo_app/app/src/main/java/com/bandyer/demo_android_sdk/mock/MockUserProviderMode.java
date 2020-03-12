/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.mock;

/**
 * Mocked user provider mode
 */
public enum MockUserProviderMode {
    /**
     * No mock for user provider, the user will be displayed with its userAlias as display name and default avatar image.
     */
    NONE,
    /**
     * Random diplay name and image will be used to display user.
     */
    RANDOM,
    /**
     * A custom display name and image will be user for ALL users.
     */
    CUSTOM
}
