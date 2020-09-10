/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.bandyer.app_configuration.R

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
