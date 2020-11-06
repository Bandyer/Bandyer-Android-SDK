/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bandyer.app_configuration.R
import com.bandyer.app_configuration.external_configuration.model.ConfigurationFieldChangeListener
import com.bandyer.app_configuration.external_configuration.model.EditableConfigurationPreference
import com.bandyer.app_configuration.external_configuration.utils.dp2px
import kotlinx.android.synthetic.main.configuration_edittext.view.configuration_edittext_subtitle
import kotlinx.android.synthetic.main.configuration_edittext.view.configuration_edittext_summary
import kotlinx.android.synthetic.main.configuration_edittext.view.configuration_edittext_summary_card_view
import kotlinx.android.synthetic.main.configuration_edittext.view.configuration_edittext_title

class ConfigurationEditTextPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr), EditableConfigurationPreference<String> {

    override var titleTextView: TextView? = null
    override var subtitleTextView: TextView? = null
    override var summaryTextView: TextView? = null
    override var summaryTextViewHolder: View? = null

    override var configurationFieldChangeListener: ConfigurationFieldChangeListener<String>? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.configuration_edittext, this)

        titleTextView = configuration_edittext_title
        subtitleTextView = configuration_edittext_subtitle
        summaryTextView = configuration_edittext_summary
        summaryTextViewHolder = configuration_edittext_summary_card_view

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

                val subtitleView = configuration_edittext_subtitle

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
