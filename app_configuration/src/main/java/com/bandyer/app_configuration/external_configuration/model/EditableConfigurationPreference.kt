/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.model

import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

/**
 * Defines an editable configuration preference that can be observed through configurationFieldChangeListener listener
 * @param T the type of the configuration preference
 * @property configurationFieldChangeListener ConfigurationFieldChangeListener<T>? configuration value change listener
 */
interface EditableConfigurationPreference<T> : ConfigurationPreference {
    var configurationFieldChangeListener: ConfigurationFieldChangeListener<T>?

    /**
     * Sets the preference value
     * @param value T? value to be set
     */
    fun setValue(value: T?)
}

/**
 * Binds the editable preference to the specified configuration property
 * @receiver EditableConfigurationPreference<T> the editable preference used to bind the configuration property
 * @param configuration Configuration to be updated with the preference editing
 * @param kProperty KMutableProperty<*> property to be updated
 * @param onConfigurationFieldChangeListener ConfigurationFieldChangeListener<T>? optional field change listener callback
 */
@Suppress("UNCHECKED_CAST")
fun <F> EditableConfigurationPreference<F>.bindToConfigurationProperty(
        configuration: Configuration,
        kProperty: KMutableProperty<*>,
        onConfigurationFieldChangeListener: ConfigurationFieldChangeListener<F>? = null) {

    val isEnum = kProperty.returnType.javaType.isEnum()

    this.configurationFieldChangeListener = object : ConfigurationFieldChangeListener<F> {
        override fun onConfigurationFieldChanged(value: F) {
            configuration::class.java.getDeclaredField(kProperty.name).apply {
                isAccessible = true
                if (isEnum) set(configuration, getEnumValue(kProperty.returnType, value as String))
                else set(configuration, value)
            }
            onConfigurationFieldChangeListener?.onConfigurationFieldChanged(value)
        }
    }
}

private fun getEnumValue(type: KType, enumValue: String): Any {
    val name = ((type.classifier!!) as KClass<*>).java.name
    val enumClz = Class.forName(name).enumConstants as Array<Enum<*>>
    return enumClz.first { it.name == enumValue }
}

private fun Type.isEnum() = (this as? Class<*>)?.isEnum == true