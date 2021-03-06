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
