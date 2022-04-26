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
import com.kaleyra.app_configuration.model.ConfigurationPreference
import kotlinx.android.synthetic.main.configuration_edittext.view.configuration_edittext_subtitle
import kotlinx.android.synthetic.main.configuration_edittext.view.configuration_edittext_summary
import kotlinx.android.synthetic.main.configuration_edittext.view.configuration_edittext_summary_card_view
import kotlinx.android.synthetic.main.configuration_edittext.view.configuration_edittext_title

class ConfigurationTextViewPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr), ConfigurationPreference {

    override var titleTextView: TextView? = null
    override var subtitleTextView: TextView? = null
    override var summaryTextView: TextView? = null
    override var summaryTextViewHolder: View? = null

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
                setTitle(getString(R.styleable.ConfigurationEditTextStyleable_title))
                setSubtitle(getString(R.styleable.ConfigurationEditTextStyleable_subtitle)
                        ?: getString(R.styleable.ConfigurationEditTextStyleable_hint))
                setSummary(getString(R.styleable.ConfigurationEditTextStyleable_summary))
            } finally {
                recycle()
            }
        }
    }
}