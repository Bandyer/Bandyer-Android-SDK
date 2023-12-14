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
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.model.UserDetailsProviderMode
import com.kaleyra.app_configuration.utils.MediaStorageUtils
import com.kaleyra.app_configuration.utils.hideKeyboard
import com.squareup.picasso.Picasso

class MockUserDetailsSettingsActivity : ScrollAwareToolbarActivity() {

    companion object {
        private var PICK_IMAGE = -1
        const val MOCK_MODE_PARAM = "PRESET_MOCK_MODE_PARAM"
        fun showForResult(activity: Activity, uri: Uri?, text: String, userDetailsProviderMode: UserDetailsProviderMode, mockUserDetailsRequest: Int) {
            PICK_IMAGE = mockUserDetailsRequest
            activity.startActivityForResult(buildIntent(activity, uri, text, userDetailsProviderMode), PICK_IMAGE)
        }

        private fun buildIntent(context: Context?, uri: Uri?, text: String, userDetailsProviderMode: UserDetailsProviderMode): Intent {
            val intent = Intent(context, MockUserDetailsSettingsActivity::class.java)
            intent.putExtra(MOCK_MODE_PARAM, userDetailsProviderMode.name)
            if (uri != null && uri.lastPathSegment != null) intent.putExtra(ImageTextEditActivity.PRESET_URI_PARAM, uri.toString())
            intent.putExtra(ImageTextEditActivity.PRESET_TEXT_PARAM, text)
            return intent
        }
    }

    private var imageView: ImageView? = null
    private var remote: RadioButton? = null
    private var editTextView: EditText? = null
    private var userDetailsSelectionLayout: LinearLayout? = null
    private var currentMockUserDetailsModeMode: UserDetailsProviderMode? = null
    private var imageUrl: String? = ""
    private var displayName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mock_user_details_settings)
        currentMockUserDetailsModeMode = getInitialUserDetailsMode()
        userDetailsSelectionLayout = findViewById(R.id.user_derails_selection_layout)
        remote = findViewById<RadioButton>(R.id.radio_button_user_details_remote)
        val custom = findViewById<RadioButton>(R.id.radio_button_user_details_custom)
        val radioGroup = findViewById<RadioGroup>(R.id.mock_user_details_radio_group)
        val remoteSummary = findViewById<TextView>(R.id.radio_button_user_details_remote_summary)
        val customSummary = findViewById<TextView>(R.id.radio_button_user_details_custom_summary)
        remoteSummary.setOnClickListener { remote!!.performClick() }
        customSummary.setOnClickListener { custom.performClick() }
        imageView = findViewById(R.id.image_view)
        editTextView = findViewById(R.id.pref_text_view)
        radioGroup.setOnCheckedChangeListener { group: RadioGroup, checkedId: Int ->
            when (group.checkedRadioButtonId) {
                remote!!.id -> {
                    hideKeyboard()
                    currentMockUserDetailsModeMode = UserDetailsProviderMode.REMOTE
                    userDetailsSelectionLayout!!.visibility = View.GONE
                    clearUserSelectionDetails()
                }
                custom.id -> {
                    currentMockUserDetailsModeMode = UserDetailsProviderMode.CUSTOM
                    userDetailsSelectionLayout!!.visibility = View.VISIBLE
                    editTextView!!.requestFocus()
                }
            }
        }
        when (currentMockUserDetailsModeMode) {
            UserDetailsProviderMode.REMOTE -> remote!!.isChecked = true
            UserDetailsProviderMode.CUSTOM -> custom.isChecked = true
            else -> Unit
        }
        displayName = intent.getStringExtra(ImageTextEditActivity.PRESET_TEXT_PARAM) ?: ""
        imageUrl = intent.getStringExtra(ImageTextEditActivity.PRESET_URI_PARAM)
        if (imageUrl != null) {
            val uri = MediaStorageUtils.getUriFromString(imageUrl)
            Picasso.get().load(uri).into(imageView)
        }
        editTextView!!.setText(displayName)
        editTextView!!.hint = resources.getString(R.string.mock_user_details_display_name)
        findViewById<View>(R.id.chooseButton).setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getInitialUserDetailsMode(): UserDetailsProviderMode {
        return kotlin.runCatching {
            UserDetailsProviderMode.valueOf(intent.getStringExtra(MOCK_MODE_PARAM) ?: UserDetailsProviderMode.REMOTE.name)
        }.getOrNull() ?: UserDetailsProviderMode.REMOTE
    }

    private fun hasChangedSettings(): Boolean =
        currentMockUserDetailsModeMode != getInitialUserDetailsMode() ||
            imageUrl?.takeIf { it.isNotEmpty() } != (intent.getStringExtra(ImageTextEditActivity.PRESET_URI_PARAM)) ||
            editTextView!!.text.toString() != (intent.getStringExtra(ImageTextEditActivity.PRESET_URI_PARAM) ?: "")

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImage = data!!.data
            // save the file in application folder to be accessible to the app
            val url = MediaStorageUtils.saveFileInApp(this, selectedImage, "custom_user_detail_logo_" + selectedImage.toString())
                ?: return
            imageUrl = url
            Picasso.get().load(MediaStorageUtils.getUriFromString(url)).into(imageView)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.save -> {
                saveSettings()
                finish()
            }
            R.id.clear_all -> {
                clearUserSelectionDetails()
                remote!!.isChecked = true
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSettings() {
        displayName = editTextView!!.text.toString()
        val resultDataIntent = Intent()
        resultDataIntent.putExtra(MOCK_MODE_PARAM, currentMockUserDetailsModeMode)
        if (imageUrl != null) resultDataIntent.putExtra(ImageTextEditActivity.PRESET_URI_PARAM, imageUrl)
        resultDataIntent.putExtra(ImageTextEditActivity.PRESET_TEXT_PARAM, displayName)
        setResult(2, resultDataIntent)
    }

    private fun clearUserSelectionDetails() {
        imageUrl = ""
        displayName = ""
        imageView!!.setImageResource(R.drawable.ic_outline_photo_24)
        editTextView!!.text = null
    }

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
}
