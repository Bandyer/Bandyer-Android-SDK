/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.adapter_items;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bandyer.demo_android_sdk.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;

/**
 * A simple RecyclerView item used to display the user alias as a cell in the list.
 */
public class UserItem extends AbstractItem<UserItem, UserItem.ViewHolder> {

    public final String userAlias;

    public UserItem(String userAlias) {
        this.userAlias = userAlias;
    }

    @Override
    public int getType() {
        return R.id.user_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_user;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ButterKnifeViewHolder<UserItem> {

        @BindView(R.id.userAlias)
        TextView userAlias;

        ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindView(@NonNull UserItem item, @NonNull List<Object> payloads) {
            userAlias.setText(item.userAlias);
        }

        @Override
        public void unbindView(@NonNull UserItem item) {
            userAlias.setText(null);
        }
    }
}