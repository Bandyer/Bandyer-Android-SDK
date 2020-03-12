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
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bandyer.demo_android_sdk.R;

import static com.bandyer.demo_android_sdk.utils.storage.MediaStorageUtils.getUriFromString;
import static com.bandyer.demo_android_sdk.utils.storage.MediaStorageUtils.saveFileInApp;

/**
 * @author kristiyan
 */
public class ImageTextEditActivity extends BaseActivity {

    private static int PICK_IMAGE = -1;

    public static final String PRESET_URI_PARAM = "uri_param";
    public static final String PRESET_TEXT_PARAM = "text_param";

    private ImageView imageView;
    private EditText editTextView;

    private String imageUrl = "";
    private String title = "";

    public static void showForResult(Fragment fragment, Uri uri, String text, int requestCode) {
        PICK_IMAGE = requestCode;
        fragment.startActivityForResult(buildIntent(fragment.getContext(), uri, text), requestCode);
    }

    public static void showForResult(Activity context, Uri uri, String text, int requestCode) {
        PICK_IMAGE = requestCode;
        context.startActivityForResult(buildIntent(context, uri, text), requestCode);
    }

    private static Intent buildIntent(Context context, Uri uri, String text) {
        Intent intent = new Intent(context, ImageTextEditActivity.class);
        if (uri != null && uri.getLastPathSegment() != null) intent.putExtra(PRESET_URI_PARAM, uri.toString());
        intent.putExtra(PRESET_TEXT_PARAM, text);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image_text);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);

        imageView = findViewById(R.id.image_view);
        editTextView = findViewById(R.id.pref_text_view);

        title = getIntent().getStringExtra(PRESET_TEXT_PARAM);
        imageUrl = getIntent().getStringExtra(PRESET_URI_PARAM);

        if (imageUrl != null) {
            Uri uri = getUriFromString(imageUrl);
            loadImage(imageView, uri);
        }

        editTextView.setText(title);

        findViewById(R.id.chooseButton).setOnClickListener(v -> {
            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            // save the file in application folder to be accessible to the app
            String url = saveFileInApp(this, selectedImage, "custom_logo_" + selectedImage.toString());
            if (url == null) return;
            imageUrl = url;
            loadImage(imageView, getUriFromString(imageUrl));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                title = editTextView.getText().toString();
                Intent resultDataIntent = new Intent();
                if (imageUrl != null) resultDataIntent.putExtra(PRESET_URI_PARAM, imageUrl);
                resultDataIntent.putExtra(PRESET_TEXT_PARAM, title);
                setResult(2, resultDataIntent);
                onBackPressed();
                break;
            case R.id.clear_all:
                imageUrl = "";
                title = "";
                imageView.setImageResource(R.drawable.outline_insert_photo_white_24);
                editTextView.setText(null);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
    }
}
