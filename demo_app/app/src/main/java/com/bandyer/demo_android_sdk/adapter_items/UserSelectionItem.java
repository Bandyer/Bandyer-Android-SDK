/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.adapter_items;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;

import com.bandyer.demo_android_sdk.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;

/**
 * A simple RecyclerView item used to display the user name with a checkbox as a cell in the list.
 */
public class UserSelectionItem extends AbstractItem<UserSelectionItem, UserSelectionItem.ViewHolder> {

    public final String name;

    public UserSelectionItem(String name) {
        this.name = name;
    }

    @Override
    public long getIdentifier() {
        return name.hashCode();
    }

    @Override
    public int getType() {
        return R.id.user_selection_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.user_selection_item;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends ButterKnifeViewHolder<UserSelectionItem> {

        @BindView(R.id.checkbox)
        CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindView(final @NonNull UserSelectionItem item, @NonNull List<Object> payloads) {
            checkBox.setChecked(item.isSelected());
            checkBox.setText(item.name);
        }

        @Override
        public void unbindView(@NonNull UserSelectionItem item) {
            checkBox.setChecked(false);
            checkBox.setText(null);
        }
    }
}