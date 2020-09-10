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
class SelectedUserItem(@JvmField val userAlias: String,@JvmField val position: Int) : AbstractItem<SelectedUserItem?, SelectedUserItem.ViewHolder>() {

    override fun getIdentifier() = position.toLong()
    override fun getType(): Int = R.id.user_selected_item
    override fun getLayoutRes(): Int = R.layout.selected_user_item_layout
    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
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