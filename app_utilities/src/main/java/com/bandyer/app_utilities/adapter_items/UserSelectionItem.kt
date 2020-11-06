/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.adapter_items

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bandyer.app_utilities.R
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_selection_item_layout.view.*

/**
 * A simple RecyclerView item used to display the user name with a checkbox as a cell in the list.
 */
class UserSelectionItem(@JvmField val name: String) : AbstractItem<UserSelectionItem?, UserSelectionItem.ViewHolder>() {

    override fun getIdentifier(): Long = name.hashCode().toLong()
    override fun getType(): Int = R.id.user_selection_item_id
    override fun getLayoutRes(): Int = R.layout.user_selection_item_layout
    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.containerView.checkbox!!.isChecked = isSelected
        holder.containerView.checkbox!!.text = name
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.containerView.checkbox!!.isChecked = false
        holder.containerView.checkbox.text = null
    }

    class ViewHolder(val containerView: View) : RecyclerView.ViewHolder(containerView)

}