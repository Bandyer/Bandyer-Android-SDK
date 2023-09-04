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
import com.bandyer.demo_android_sdk.databinding.NoUsersSelectedItemLayoutBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem

/**
 * A simple RecyclerView item used to display no selections on users' list.
 */
class NoUserSelectedItem : AbstractBindingItem<NoUsersSelectedItemLayoutBinding>() {

    override var identifier: Long = NO_USER_SELECTED_ITEM_IDENTIFIER
    override val type: Int = R.id.no_user_selecte_item

    override fun bindView(binding: NoUsersSelectedItemLayoutBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.noUserSelecteItemView.text = binding.noUserSelecteItemView.context.getString(R.string.no_users_selected)
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): NoUsersSelectedItemLayoutBinding {
        return NoUsersSelectedItemLayoutBinding.inflate(inflater, parent, false)
    }

    companion object {
        @JvmField
        var NO_USER_SELECTED_ITEM_IDENTIFIER = R.id.no_user_selecte_item.toLong()
    }
}