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
package com.kaleyra.app_configuration.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.model.ConfigurationFieldChangeListener
import com.kaleyra.app_configuration.model.EditableConfigurationPreference
import kotlinx.android.synthetic.main.configuration_checkbox.view.configuration_checkbox
import kotlinx.android.synthetic.main.configuration_checkbox.view.configuration_checkbox_summary
import kotlinx.android.synthetic.main.configuration_checkbox.view.configuration_checkbox_summary_card_view
import kotlinx.android.synthetic.main.configuration_checkbox.view.configuration_checkbox_title

class ConfigurationCheckBoxPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), EditableConfigurationPreference<Boolean> {

    override var titleTextView: TextView? = null
    override var subtitleTextView: TextView? = null
    override var summaryTextView: TextView? = null
    override var summaryTextViewHolder: View? = null

    var isChecked = false
        set(value) {
            field = value
            setValue(value)
        }
        get() = configuration_checkbox.isChecked

    override var configurationFieldChangeListener: ConfigurationFieldChangeListener<Boolean>? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.configuration_checkbox, this)

        titleTextView = configuration_checkbox_title
        summaryTextView = configuration_checkbox_summary
        summaryTextViewHolder = configuration_checkbox_summary_card_view

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ConfigurationCheckboxStyleable,
                defStyleAttr,
                0
        ).apply {

            try {
                setTitle(getString(R.styleable.ConfigurationCheckboxStyleable_title))
                setSummary(getString(R.styleable.ConfigurationCheckboxStyleable_summary))
                isChecked = getBoolean(R.styleable.ConfigurationCheckboxStyleable_checked, false)
            } finally {
                recycle()
            }

            configuration_checkbox_title.setOnClickListener { if (configuration_checkbox.isEnabled) configuration_checkbox.performClick() }
            configuration_checkbox.setOnCheckedChangeListener { _, isChecked ->
                configurationFieldChangeListener?.onConfigurationFieldChanged(isChecked)
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        configuration_checkbox.isEnabled = enabled
    }

    override fun setValue(value: Boolean?) {
        configuration_checkbox.isChecked = value ?: false
    }
}
