/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.Utils;

public class SummaryCheckBoxPreference extends CheckBoxPreference implements SummaryPreference {

    private String secondarySummaryText;

    public SummaryCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SummaryCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SummaryCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryCheckBoxPreference(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.preference_checkbox_summary_layout;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View root = onBind(getContext(), false, secondarySummaryText, holder);
        RelativeLayout summaryParent = (RelativeLayout) ((LinearLayout) holder.itemView).getChildAt(1);
        RelativeLayout.LayoutParams secondarySummaryLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        secondarySummaryLayoutParams.addRule(RelativeLayout.BELOW, android.R.id.title);
        root.setLayoutParams(secondarySummaryLayoutParams);

        if (holder.findViewById(R.id.root_pref) == null)
            summaryParent.addView(root, secondarySummaryLayoutParams);

        LinearLayout checkBoxLayout = (LinearLayout) ((LinearLayout) holder.itemView).getChildAt(2);
        checkBoxLayout.setPadding(0, Utils.dpToPx(getContext(), 11), 0, 0);
        checkBoxLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public void setSecondarySummaryText(String secondarySummmary) {
        this.secondarySummaryText = secondarySummmary;
        notifyChanged();
    }
}