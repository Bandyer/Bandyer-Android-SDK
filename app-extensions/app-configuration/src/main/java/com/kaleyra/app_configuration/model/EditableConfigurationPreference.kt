/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaleyra.app_configuration.model

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