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

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bandyer.demo_android_sdk.R
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_user_layout.view.*

/**
 * A simple RecyclerView item used to display the user userAlias as a cell in the list.
 */
class UserItem(@JvmField val userAlias: String) : AbstractItem<UserItem.ViewHolder>() {

    override var identifier: Long = userAlias.hashCode().toLong()
    override val type: Int = R.id.user_item_id
    override val layoutRes: Int = R.layout.item_user_layout
    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        holder.containerView.userAlias!!.text = userAlias
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.containerView.userAlias!!.text = null
    }

    class ViewHolder(val containerView: View) : RecyclerView.ViewHolder(containerView)
}
