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

package com.bandyer.demo_android_sdk.ui.adapter_items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bandyer.demo_android_sdk.R
import com.bandyer.demo_android_sdk.databinding.SelectedUserItemLayoutBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem

/**
 * A simple RecyclerView item used to display the user userAlias as a Chip in the list.
 */
class SelectedUserItem(@JvmField val userAlias: String,@JvmField val position: Int) : AbstractBindingItem<SelectedUserItemLayoutBinding>() {

    override var identifier: Long = userAlias.hashCode().toLong()
    override val type: Int = R.id.user_selected_item

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): SelectedUserItemLayoutBinding {
       return SelectedUserItemLayoutBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: SelectedUserItemLayoutBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.userAlias.text = userAlias
        binding.userAlias.setOnCloseIconClickListener { v: View? -> v!!.performClick() }
    }

    override fun unbindView(binding: SelectedUserItemLayoutBinding) {
        super.unbindView(binding)
        binding.userAlias.text = null
    }

}