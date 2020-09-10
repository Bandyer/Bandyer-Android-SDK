/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bandyer.app_configuration.external_configuration.activities.ScrollAwareToolbarActivity
import com.bandyer.app_utilities.R
import com.bandyer.app_utilities.networking.MockedNetwork
import com.bandyer.app_utilities.utils.DPadNavigationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * BaseActivity used to set ButterKnife( library which avoids android-java boilerplate)
 *
 * @author kristiyan
 */
abstract class BaseActivity : AppCompatActivity() {
    private var dialog: AlertDialog? = null
    private val dPadNavigationHelper = DPadNavigationHelper()

    @JvmOverloads
    protected fun showErrorDialog(text: String?, clickListener: DialogInterface.OnClickListener? = null) {
        if (dialog != null) dialog!!.dismiss()
        if (isFinishing) return
        dialog = MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_error_title)
                .setMessage(text)
                .setPositiveButton(R.string.button_ok, clickListener)
                .create()
        dialog!!.show()
    }

    @JvmOverloads
    protected fun showConfirmDialog(@StringRes title: Int, @StringRes message: Int, onConfirm: DialogInterface.OnClickListener? = null) {
        if (dialog != null) dialog!!.dismiss()
        if (isFinishing) return
        dialog = MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel_action) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .setPositiveButton(R.string.button_ok, onConfirm)
                .create()
        dialog!!.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dPadNavigationHelper.bind(this)
    }

    override fun onPause() {
        MockedNetwork.cancel()
        if (dialog != null) dialog!!.dismiss()
        dialog = null
        super.onPause()
    }

    override fun onDestroy() {
        dPadNavigationHelper.unbind()
        super.onDestroy()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent) = if (dPadNavigationHelper.hasConsumedKeyUp(keyCode, event)) true else super.onKeyUp(keyCode, event)
    override fun onKeyDown(keyCode: Int, event: KeyEvent) = if (dPadNavigationHelper.hasConsumedKeyDown(keyCode, event)) true else super.onKeyDown(keyCode, event)
    override fun dispatchTouchEvent(ev: MotionEvent?) = if (dPadNavigationHelper.hasConsumedTouchEvent(ev)) true else super.dispatchTouchEvent(ev)

    protected fun hideKeyboard(force: Boolean) {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var hideMethod = InputMethodManager.HIDE_NOT_ALWAYS
        if (force) hideMethod = 0
        if (currentFocus == null) return
        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, hideMethod)
    }
}