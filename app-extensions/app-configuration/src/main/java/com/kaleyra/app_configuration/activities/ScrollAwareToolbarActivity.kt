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
package com.kaleyra.app_configuration.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.kaleyra.app_configuration.databinding.ActionbarLayoutBinding

/**
 *
 * @author kristiyan
 */
abstract class ScrollAwareToolbarActivity(open var withToolbar: Boolean = true) : AppCompatActivity() {

    private lateinit var binding: ActionbarLayoutBinding

    override fun setContentView(layoutResID: Int) = setContentView(LayoutInflater.from(this).inflate(layoutResID, null), null)

    override fun setContentView(view: View?) = setContentView(view, null)
    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (!withToolbar) {
            params?.let { super.setContentView(view, it) } ?: super.setContentView(view)
            return
        }
        binding = ActionbarLayoutBinding.inflate(layoutInflater)
        super.setContentView(binding.root)
        params?.let {binding.scroll.addView(view, it) } ?: binding.scroll.addView(view!!)
        binding.scroll.isNestedScrollingEnabled = true
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}