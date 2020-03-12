/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.custom_views;

import android.content.Context;
import android.util.AttributeSet;

public class SummaryListIntentPreference extends SummaryListPreference  {

    private String secondarySummaryText;

    public SummaryListIntentPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SummaryListIntentPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SummaryListIntentPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryListIntentPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {}
}
