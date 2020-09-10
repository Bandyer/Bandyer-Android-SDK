/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bandyer.app_configuration.R
import com.bandyer.app_configuration.external_configuration.utils.MediaStorageUtils
import com.squareup.picasso.Picasso

/**
 * @author kristiyan
 */
class ImageTextEditActivity : ScrollAwareToolbarActivity() {
    private var imageView: ImageView? = null
    private var editTextView: EditText? = null
    private var imageUrl: String? = ""
    private var title = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_text_edit_layout)
        imageView = findViewById(R.id.image_view)
        editTextView = findViewById(R.id.pref_text_view)
        title = intent.getStringExtra(PRESET_TEXT_PARAM) ?: ""
        imageUrl = intent.getStringExtra(PRESET_URI_PARAM)
        if (imageUrl != null) {
            val uri = MediaStorageUtils.getUriFromString(imageUrl)
            Picasso.get().load(uri).into(imageView)
        }
        editTextView!!.setText(title)
        findViewById<View>(R.id.chooseButton).setOnClickListener {
            val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImage = data!!.data
            // save the file in application folder to be accessible to the app
            val url = MediaStorageUtils.saveFileInApp(this, selectedImage, "custom_logo_" + selectedImage.toString())
                    ?: return
            imageUrl = url
            Picasso.get().load(MediaStorageUtils.getUriFromString(imageUrl)).into(imageView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.save) {
            title = editTextView!!.text.toString()
            val resultDataIntent = Intent()
            if (imageUrl != null) resultDataIntent.putExtra(PRESET_URI_PARAM, imageUrl)
            resultDataIntent.putExtra(PRESET_TEXT_PARAM, title)
            setResult(2, resultDataIntent)
            onBackPressed()
        } else if (itemId == R.id.clear_all) {
            imageUrl = ""
            title = ""
            imageView!!.setImageResource(R.drawable.ic_outline_photo_24)
            editTextView!!.setText(null)
        } else if (itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    companion object {
        private var PICK_IMAGE = -1
        const val PRESET_URI_PARAM = "uri_param"
        const val PRESET_TEXT_PARAM = "text_param"
        fun showForResult(fragment: Fragment, uri: Uri?, text: String, requestCode: Int) {
            PICK_IMAGE = requestCode
            fragment.startActivityForResult(buildIntent(fragment.context, uri, text), requestCode)
        }

        fun showForResult(context: Activity, uri: Uri?, text: String, requestCode: Int) {
            PICK_IMAGE = requestCode
            context.startActivityForResult(buildIntent(context, uri, text), requestCode)
        }

        private fun buildIntent(context: Context?, uri: Uri?, text: String): Intent {
            val intent = Intent(context, ImageTextEditActivity::class.java)
            if (uri != null && uri.lastPathSegment != null) intent.putExtra(PRESET_URI_PARAM, uri.toString())
            intent.putExtra(PRESET_TEXT_PARAM, text)
            return intent
        }
    }
}