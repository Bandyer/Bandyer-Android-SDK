package com.bandyer.demo_android_sdk.custom_views;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.bandyer.demo_android_sdk.R;
import com.squareup.picasso.Picasso;

public class SummaryImageEditTextPreference extends Preference implements SummaryPreference {

    private String secondarySummaryText;
    private Uri imageUri;
    private String text;

    public SummaryImageEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SummaryImageEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SummaryImageEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryImageEditTextPreference(Context context) {
        super(context);
    }


    @Override
    public int getLayout() {
        return R.layout.preference_image_edit_text_layout;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View root = onBind(getContext(), true, secondarySummaryText, holder);

        if (holder.findViewById(R.id.root_pref) == null)
            ((LinearLayout) holder.itemView).addView(root);

        AppCompatImageView imageView = holder.itemView.findViewById(R.id.pref_image_view);
        TextView textView = holder.itemView.findViewById(R.id.pref_text_view);

        textView.setText(text);

        Picasso.get().load(imageUri).into(imageView);
    }

    public void setImageUri(Uri uri) {
        imageUri = uri;
        notifyChanged();
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyChanged();
    }

    @Override
    public void setSecondarySummaryText(String secondarySummary) {
        this.secondarySummaryText = secondarySummary;
        notifyChanged();
    }
}
