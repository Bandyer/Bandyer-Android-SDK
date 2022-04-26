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
import android.widget.LinearLayout
import android.widget.TextView
import com.kaleyra.app_configuration.R

class ConfigurationCategoryPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = LinearLayout.VERTICAL

        val title = LayoutInflater.from(context).inflate(R.layout.configuration_category_title, null) as TextView
        addView(title)
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ConfigurationCategoryStyleable,
                defStyleAttr,
                0).apply {

            try {
                title.text = getString(R.styleable.ConfigurationCategoryStyleable_title)
            } finally {
                recycle()
            }
        }
    }
}
