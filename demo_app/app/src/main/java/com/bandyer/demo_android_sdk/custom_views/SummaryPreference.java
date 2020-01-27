package com.bandyer.demo_android_sdk.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.preference.PreferenceViewHolder;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.Utils;

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

        ViewGroup.MarginLayoutParams secondarySummaryLayoutParams = new ViewGroup.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        secondarySummaryLayoutParams.bottomMargin = Utils.dpToPx(context, 16);
        root.setLayoutParams(secondarySummaryLayoutParams);
        return root;
    }
}
