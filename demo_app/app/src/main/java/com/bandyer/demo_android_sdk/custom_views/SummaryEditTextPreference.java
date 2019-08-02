package com.bandyer.demo_android_sdk.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.Utils;

public class SummaryEditTextPreference extends EditTextPreference implements SummaryPreference {

    private TextView secondarySummaryTextView;
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
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView title = (TextView) holder.findViewById(android.R.id.title);
        if (secondarySummaryTextView != null) return;
        View secondarySummary = LayoutInflater.from(getContext()).inflate(R.layout.preference_summary_layout, null);
        secondarySummaryTextView = secondarySummary.findViewById(android.R.id.summary);
        secondarySummaryTextView.setText(secondarySummaryText);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((RelativeLayout) title.getParent()).setLayoutParams(llp);
        ((LinearLayout) holder.itemView).setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams secondarySummaryLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        secondarySummaryLayoutParams.bottomMargin = Utils.dpToPx(getContext(), 16);
        ((LinearLayout) holder.itemView).addView(secondarySummary, secondarySummaryLayoutParams);
    }

    @Override
    public void setSecondarySummmary(String secondarySummmary) {
        this.secondarySummaryText = secondarySummmary;
        notifyChanged();
    }
}
