/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.adapter_items;

import android.view.View;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import butterknife.ButterKnife;

/**
 * @author kristiyan
 */
abstract class ButterKnifeViewHolder<T extends IItem> extends FastAdapter.ViewHolder<T> {

    ButterKnifeViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}