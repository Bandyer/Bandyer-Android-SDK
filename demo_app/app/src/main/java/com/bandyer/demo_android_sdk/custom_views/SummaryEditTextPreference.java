/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.Utils;

public class SummaryEditTextPreference extends EditTextPreference implements SummaryPreference {

    private String secondarySummaryText;

    public SummaryEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SummaryEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SummaryEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryEditTextPreference(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.preference_edittext_summary_layout;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View root = onBind(getContext(),true, secondarySummaryText, holder);
        if (holder.findViewById(R.id.root_pref) == null)
            ((LinearLayout) holder.itemView).addView(root);
    }

    @Override
    public void setSecondarySummaryText(String secondarySummaryText) {
        this.secondarySummaryText = secondarySummaryText;
        notifyChanged();
    }
}
