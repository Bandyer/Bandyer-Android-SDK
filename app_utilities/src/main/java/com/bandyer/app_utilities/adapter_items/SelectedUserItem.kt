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
import kotlinx.android.synthetic.main.selected_user_item_layout.view.*

/**
 * A simple RecyclerView item used to display the user userAlias as a Chip in the list.
 */
class SelectedUserItem(@JvmField val userAlias: String,@JvmField val position: Int) : AbstractItem<SelectedUserItem.ViewHolder>() {

    override var identifier: Long = position.toLong()
    override val type: Int = R.id.user_selected_item
    override val layoutRes: Int = R.layout.selected_user_item_layout
    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        holder.containerView.userAlias!!.text = userAlias
        holder.containerView.userAlias!!.setOnCloseIconClickListener { v: View? -> v!!.performClick() }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.containerView.userAlias.text = null
    }

    class ViewHolder(val containerView: View) : RecyclerView.ViewHolder(containerView) {
        init {
            containerView.isFocusable = false
            containerView.isFocusableInTouchMode = false
        }
    }

}