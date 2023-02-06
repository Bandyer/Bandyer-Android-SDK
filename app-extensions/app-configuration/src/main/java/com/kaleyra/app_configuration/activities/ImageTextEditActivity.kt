/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaleyra.app_configuration.activities

import android.app.Activity
import android.app.AlertDialog
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
import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.utils.MediaStorageUtils
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
        imageUrl = intent.getStringExtra(PRESET_URI_PARAM) ?: ""
        if (!imageUrl.isNullOrBlank()) {
            val uri = MediaStorageUtils.getUriFromString(imageUrl)
            Picasso.get().load(uri).into(imageView)
        } else imageView!!.setImageResource(R.drawable.ic_outline_photo_24)
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
        when (itemId) {
            R.id.save -> {
                saveSettings()
                finish()
            }
            R.id.clear_all -> {
                imageUrl = ""
                title = ""
                imageView!!.setImageResource(R.drawable.ic_outline_photo_24)
                editTextView!!.setText(null)
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSettings() {
        title = editTextView!!.text.toString()
        val resultDataIntent = Intent()
        if (imageUrl != null) resultDataIntent.putExtra(PRESET_URI_PARAM, imageUrl)
        resultDataIntent.putExtra(PRESET_TEXT_PARAM, title)
        setResult(2, resultDataIntent)
    }

    private fun hasChangedSettings(): Boolean =
        editTextView!!.text.toString() != (intent.getStringExtra(PRESET_TEXT_PARAM) ?: "") ||
            imageUrl != (intent.getStringExtra(PRESET_URI_PARAM) ?: "")

    override fun onBackPressed() {
        if (hasChangedSettings()) {
            AlertDialog.Builder(this)
                .setMessage(R.string.pref_settings_save_confirmation_message)
                .setPositiveButton(R.string.pref_settings_save_confirmation_message_confirmation) { dialog, _ ->
                    dialog.dismiss()
                    saveSettings()
                    finish()
                }
                .setNegativeButton(R.string.pref_settings_save_confirmation_message_cancel) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
        } else {
            setResult(Activity.RESULT_CANCELED)
            super.onBackPressed()
        }
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
