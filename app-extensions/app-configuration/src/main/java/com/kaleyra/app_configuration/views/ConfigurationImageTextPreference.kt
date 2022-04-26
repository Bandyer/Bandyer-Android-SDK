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
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.model.ConfigurationPreference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.configuration_image_text.view.configuration_image
import kotlinx.android.synthetic.main.configuration_image_text.view.configuration_image_name
import kotlinx.android.synthetic.main.configuration_image_text.view.configuration_image_text_summary
import kotlinx.android.synthetic.main.configuration_image_text.view.configuration_image_text_summary_card_view
import kotlinx.android.synthetic.main.configuration_image_text.view.configuration_image_text_title

class ConfigurationImageTextPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr), ConfigurationPreference {

    override var titleTextView: TextView? = null
    override var subtitleTextView: TextView? = null
    override var summaryTextView: TextView? = null
    override var summaryTextViewHolder: View? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.configuration_image_text, this)

        titleTextView = configuration_image_text_title
        summaryTextView = configuration_image_text_summary
        summaryTextViewHolder = configuration_image_text_summary_card_view

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ConfigurationImageTextStyleable,
                defStyleAttr,
                0).apply {

            try {
                setTitle(getString(R.styleable.ConfigurationImageTextStyleable_title))
                setSummary(getString(R.styleable.ConfigurationImageTextStyleable_summary))
                getString(R.styleable.ConfigurationImageTextStyleable_imageUri)?.let {
                    setImageUri(Uri.parse(it))
                }
                setImageName(getString(R.styleable.ConfigurationImageTextStyleable_imageName))
            } finally {
                recycle()
            }
        }
    }

    fun setImageUri(imageUri: Uri?) = Picasso.get().load(imageUri).into(configuration_image)

    fun setImageName(imageName: String?) {
        configuration_image_name.text = imageName
    }
}

