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
import android.view.ViewGroup
import com.bandyer.demo_android_sdk.R
import com.bandyer.demo_android_sdk.databinding.UserSelectionItemLayoutBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem

/**
 * A simple RecyclerView item used to display the user name with a checkbox as a cell in the list.
 */
class UserSelectionItem(@JvmField val name: String) : AbstractBindingItem<UserSelectionItemLayoutBinding>() {

    override var identifier: Long = name.hashCode().toLong()
    override val type: Int = R.id.user_selection_item_id

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): UserSelectionItemLayoutBinding {
        return UserSelectionItemLayoutBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: UserSelectionItemLayoutBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.checkbox.isChecked = isSelected
        binding.checkbox.text = name
    }

    override fun unbindView(binding: UserSelectionItemLayoutBinding) {
        super.unbindView(binding)
        binding.checkbox.isChecked = false
        binding.checkbox.text = null
    }
}