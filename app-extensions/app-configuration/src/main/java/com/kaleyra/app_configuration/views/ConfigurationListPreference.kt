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
import androidx.appcompat.app.AlertDialog
import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.model.ConfigurationFieldChangeListener
import com.kaleyra.app_configuration.model.EditableConfigurationPreference
import kotlinx.android.synthetic.main.configuration_list.view.configuration_list_summary
import kotlinx.android.synthetic.main.configuration_list.view.configuration_list_summary_card_view
import kotlinx.android.synthetic.main.configuration_list.view.configuration_list_title
import kotlinx.android.synthetic.main.configuration_list.view.configuration_list_value

class ConfigurationListPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr), EditableConfigurationPreference<String> {

    override var titleTextView: TextView? = null
    override var subtitleTextView: TextView? = null
    override var summaryTextView: TextView? = null
    override var summaryTextViewHolder: View? = null

    private var entries: MutableList<String>
    private var entryValues: MutableList<String>

    override var configurationFieldChangeListener: ConfigurationFieldChangeListener<String>? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.configuration_list, this)

        titleTextView = configuration_list_title
        subtitleTextView = configuration_list_value
        summaryTextView = configuration_list_summary
        summaryTextViewHolder = configuration_list_summary_card_view

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ConfigurationListStyleable,
                defStyleAttr,
                0).apply {

            try {
                entries = getTextArray(R.styleable.ConfigurationListStyleable_entries).map { it.toString() }.toMutableList()
                entryValues = getTextArray(R.styleable.ConfigurationListStyleable_entryValues).map { it.toString() }.toMutableList()
                setTitle(getString(R.styleable.ConfigurationListStyleable_title))
                setSubtitle(getString(R.styleable.ConfigurationListStyleable_defaultValue))
                setSummary(getString(R.styleable.ConfigurationListStyleable_summary))
            } finally {
                recycle()
            }
        }

        setOnClickListener {
            AlertDialog.Builder(context).setTitle(configuration_list_title.text)
                    .setSingleChoiceItems(entries.toTypedArray(), entryValues.indexOf(configuration_list_value.text
                            ?: 0)) { dialogInterface, value ->
                        val newValue = entryValues[value]
                        setValue(newValue)
                        configurationFieldChangeListener?.onConfigurationFieldChanged(newValue)
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(context.getString(R.string.settings_dialog_negative_button)) { dialogInterface, index ->
                        dialogInterface.dismiss()
                    }
                    .show()
        }
    }

    override fun setValue(value: String?) {
        setSubtitle(value)
        value ?: return
        entryValues.firstOrNull { it.equals(value, ignoreCase = true) } ?: kotlin.run {
            entries.add(value)
            entryValues.add(value)
        }
    }
}
