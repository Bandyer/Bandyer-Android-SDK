/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_utilities.adapter_items

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bandyer.app_utilities.R
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.no_users_selected_item_layout.view.*

/**
 * A simple RecyclerView item used to display no selections on users' list.
 */
class NoUserSelectedItem : AbstractItem<NoUserSelectedItem, NoUserSelectedItem.ViewHolder>() {

    override fun getIdentifier() =  NO_USER_SELECTED_ITEM_IDENTIFIER
    override fun getType(): Int = R.id.no_user_selecte_item
    override fun getLayoutRes(): Int = R.layout.no_users_selected_item_layout
    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.containerView.no_user_selecte_itemView.text = holder.containerView.context.getString(R.string.no_users_selected)
    }

    class ViewHolder(val containerView: View) : RecyclerView.ViewHolder(containerView) {

        init {
            containerView.isFocusable = false
            containerView.isFocusableInTouchMode = false
            containerView.isClickable = false
        }
    }

    companion object {
        @JvmField
        var NO_USER_SELECTED_ITEM_IDENTIFIER = R.id.no_user_selecte_item.toLong()
    }
}