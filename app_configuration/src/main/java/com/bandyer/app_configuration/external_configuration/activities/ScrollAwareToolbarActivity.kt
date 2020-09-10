/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bandyer.app_configuration.R
import kotlinx.android.synthetic.main.actionbar_layout.scroll
import kotlinx.android.synthetic.main.actionbar_layout.toolbar

/**
 *
 * @author kristiyan
 */
abstract class ScrollAwareToolbarActivity(open var withToolbar: Boolean = true) : AppCompatActivity() {

    override fun setContentView(layoutResID: Int) = setContentView(LayoutInflater.from(this).inflate(layoutResID, null), null)
    override fun setContentView(view: View?) = setContentView(view, null)
    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (!withToolbar) {
            params?.let { super.setContentView(view, it) } ?: super.setContentView(view)
            return
        }
        super.setContentView(R.layout.actionbar_layout)
        params?.let { scroll.addView(view, it) } ?: scroll.addView(view)
        scroll!!.isNestedScrollingEnabled = true
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}