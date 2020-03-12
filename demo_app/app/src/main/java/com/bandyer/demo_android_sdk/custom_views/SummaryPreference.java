/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.preference.PreferenceViewHolder;

import com.bandyer.demo_android_sdk.R;

/**
 * @author kristiyan
 */
@SuppressLint("StaticFieldLeak")
public interface SummaryPreference {

    @LayoutRes
    int getLayout();

    void setSecondarySummaryText(String value);

    default View onBind(Context context, boolean singleLineTitle, String secondarySummaryText, PreferenceViewHolder holder) {
        View root = holder.findViewById(R.id.root_pref);
        if (root != null) return root;
        root = LayoutInflater.from(context).inflate(getLayout(), null);
        TextView secondarySummaryTextView = root.findViewById(android.R.id.summary);
        secondarySummaryTextView.setText(secondarySummaryText);

        if (singleLineTitle) {
            TextView title = (TextView) holder.findViewById(android.R.id.title);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ((RelativeLayout) title.getParent()).setLayoutParams(llp);
            ((LinearLayout) holder.itemView).setOrientation(LinearLayout.VERTICAL);
        }

        return root;
    }
}
