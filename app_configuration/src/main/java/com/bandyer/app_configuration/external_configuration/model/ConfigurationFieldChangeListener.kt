/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.model

interface ConfigurationFieldChangeListener<T> {
    fun onConfigurationFieldChanged(value: T)
}