/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.model

/**
 * Mocked user provider mode
 */
enum class UserDetailsProviderMode {
    /**
     * No mock for user provider, the user will be displayed with its userAlias as display name and default avatar image.
     */
    NONE,

    /**
     * Sample display name and image will be used to display user.
     */
    SAMPLE,

    /**
     * A custom display name and image will be user for ALL users.
     */
    CUSTOM
}