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
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.databinding.ConfigurationEdittextBinding
import com.kaleyra.app_configuration.model.ConfigurationFieldChangeListener
import com.kaleyra.app_configuration.model.EditableConfigurationPreference
import com.kaleyra.app_configuration.utils.dp2px

class ConfigurationEditTextPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr), EditableConfigurationPreference<String> {

    private lateinit var binding: ConfigurationEdittextBinding

    override var titleTextView: TextView? = null
    override var subtitleTextView: TextView? = null
    override var summaryTextView: TextView? = null
    override var summaryTextViewHolder: View? = null

    override var configurationFieldChangeListener: ConfigurationFieldChangeListener<String>? = null

    init {
        orientation = VERTICAL

        LayoutInflater.from(context).inflate(R.layout.configuration_edittext, this)
        binding = ConfigurationEdittextBinding.bind(this)

        titleTextView = binding.configurationEdittextTitle
        subtitleTextView = binding.configurationEdittextSubtitle
        summaryTextView = binding.configurationEdittextSummary
        summaryTextViewHolder = binding.configurationEdittextSummaryCardView

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ConfigurationEditTextStyleable,
                defStyleAttr,
                0).apply {

            try {
                val title = getString(R.styleable.ConfigurationEditTextStyleable_title)
                setTitle(title)
                val hintText = getString(R.styleable.ConfigurationEditTextStyleable_hint)
                setSubtitle(getString(R.styleable.ConfigurationEditTextStyleable_subtitle)
                        ?: hintText)
                setSummary(getString(R.styleable.ConfigurationEditTextStyleable_summary))

                val subtitleView = binding.configurationEdittextSubtitle

                setOnClickListener {
                    val editText = EditText(context).apply {
                        hint = hintText
                        if (subtitleView.text != null) setText(subtitleView.text)
                    }
                    val customView = FrameLayout(context).apply {
                        addView(editText)
                        val padding = context.dp2px(16f)
                        setPadding(padding, padding, padding, padding)
                    }
                    AlertDialog.Builder(context).setTitle(title)
                            .setView(customView)
                            .setPositiveButton(context.getString(R.string.settings_dialog_positive_button)) { dialogInterface, _ ->
                                setValue(editText.text.toString())
                                configurationFieldChangeListener?.onConfigurationFieldChanged(editText.text.toString())
                                dialogInterface.dismiss()
                            }
                            .setNegativeButton(context.getString(R.string.settings_dialog_negative_button)) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .show()
                    editText.post { editText.requestFocus() }
                }
            } finally {
                recycle()
            }
        }
    }

    override fun setValue(value: String?) {
        setSubtitle(value)
    }
}
