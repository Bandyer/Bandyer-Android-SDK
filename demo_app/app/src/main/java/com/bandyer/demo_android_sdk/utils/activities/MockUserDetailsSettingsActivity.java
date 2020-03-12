/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;

import static com.bandyer.demo_android_sdk.utils.activities.ImageTextEditActivity.PRESET_TEXT_PARAM;
import static com.bandyer.demo_android_sdk.utils.activities.ImageTextEditActivity.PRESET_URI_PARAM;
import static com.bandyer.demo_android_sdk.utils.storage.MediaStorageUtils.getUriFromString;
import static com.bandyer.demo_android_sdk.utils.storage.MediaStorageUtils.saveFileInApp;

public class MockUserDetailsSettingsActivity extends BaseActivity {

    private static int PICK_IMAGE = -1;
    private static final String MOCK_USER_DETAILS_NONE = "NONE";
    private static final String MOCK_USER_DETAILS_RANDOM = "RANDOM";
    private static final String MOCK_USER_DETAILS_CUSTOM = "CUSTOM";

    private ImageView imageView;
    private EditText editTextView;
    private RadioButton none;
    private LinearLayout userDetailsSelectionLayout;

    private String currentMockUserDetailsMode;
    private String imageUrl = "";
    private String displayName = "";

    public static void showForResult(Fragment fragment, Uri uri, String text, int mockUserDetailsRequest) {
        PICK_IMAGE = mockUserDetailsRequest;
        fragment.startActivityForResult(buildIntent(fragment.getContext(), uri, text), PICK_IMAGE);
    }

    private static Intent buildIntent(Context context, Uri uri, String text) {
        Intent intent = new Intent(context, MockUserDetailsSettingsActivity.class);
        if (uri != null && uri.getLastPathSegment() != null)
            intent.putExtra(PRESET_URI_PARAM, uri.toString());
        intent.putExtra(PRESET_TEXT_PARAM, text);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_user_details_settings);

        currentMockUserDetailsMode = ConfigurationPrefsManager.getMockedUserDetailsMode(this);

        userDetailsSelectionLayout = findViewById(R.id.user_derails_selection_layout);

        none = findViewById(R.id.radio_button_user_details_none);
        RadioButton random = findViewById(R.id.radio_button_user_details_random);
        RadioButton custom = findViewById(R.id.radio_button_user_details_custom);
        RadioGroup radioGroup = findViewById(R.id.mock_user_details_radio_group);

        TextView noneSummary = findViewById(R.id.radio_button_user_details_none_summary);
        TextView randomSummary = findViewById(R.id.radio_button_user_details_random_summary);
        TextView customSummary = findViewById(R.id.radio_button_user_details_custom_summary);
        noneSummary.setOnClickListener(v -> none.performClick());
        randomSummary.setOnClickListener(v -> random.performClick());
        customSummary.setOnClickListener(v -> custom.performClick());

        imageView = findViewById(R.id.image_view);
        editTextView = findViewById(R.id.pref_text_view);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == none.getId()) {
                currentMockUserDetailsMode = MOCK_USER_DETAILS_NONE;
                userDetailsSelectionLayout.setVisibility(View.GONE);
                clearUserSelectionDetails();
            } else if (group.getCheckedRadioButtonId() == random.getId()) {
                currentMockUserDetailsMode = MOCK_USER_DETAILS_RANDOM;
                userDetailsSelectionLayout.setVisibility(View.GONE);
                clearUserSelectionDetails();
            } else if (group.getCheckedRadioButtonId() == custom.getId()) {
                currentMockUserDetailsMode = MOCK_USER_DETAILS_CUSTOM;
                userDetailsSelectionLayout.setVisibility(View.VISIBLE);
            }
        });

        switch (ConfigurationPrefsManager.getMockedUserDetailsMode(this)) {
            case MOCK_USER_DETAILS_NONE:
                none.setChecked(true);
                break;
            case MOCK_USER_DETAILS_RANDOM:
                random.setChecked(true);
                break;
            case MOCK_USER_DETAILS_CUSTOM:
                custom.setChecked(true);
                break;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);

        displayName = getIntent().getStringExtra(PRESET_TEXT_PARAM);
        imageUrl = getIntent().getStringExtra(PRESET_URI_PARAM);
        if (imageUrl != null) {
            Uri uri = getUriFromString(imageUrl);
            loadImage(imageView, uri);
        }

        editTextView.setText(displayName);
        editTextView.setHint(getResources().getString(R.string.mock_user_details_display_name));

        findViewById(R.id.chooseButton).setOnClickListener(v -> {
            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            // save the file in application folder to be accessible to the app
            String url = saveFileInApp(this, selectedImage, "custom_user_detail_logo_" + selectedImage.toString());
            if (url == null) return;
            imageUrl = url;
            loadImage(imageView, getUriFromString(url));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                ConfigurationPrefsManager.setMockedUserDetailsMode(this, currentMockUserDetailsMode);
                displayName = editTextView.getText().toString();
                Intent resultDataIntent = new Intent();
                if (imageUrl != null) resultDataIntent.putExtra(PRESET_URI_PARAM, imageUrl);
                resultDataIntent.putExtra(PRESET_TEXT_PARAM, displayName);
                setResult(2, resultDataIntent);
                onBackPressed();
                break;
            case R.id.clear_all:
                clearUserSelectionDetails();
                none.setChecked(true);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearUserSelectionDetails() {
        imageUrl = "";
        displayName = "";
        imageView.setImageResource(R.drawable.outline_insert_photo_white_24);
        editTextView.setText(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
    }
}
