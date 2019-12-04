/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.DPadNavigationHelper;
import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;

import butterknife.ButterKnife;

/**
 * BaseActivity used to set ButterKnife( library which avoids android-java boilerplate)
 *
 * @author kristiyan
 */
public abstract class BaseActivity extends AppCompatActivity {

    private AlertDialog dialog;

    private DPadNavigationHelper dPadNavigationHelper = new DPadNavigationHelper();

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    protected void showErrorDialog(String text) {
        showErrorDialog(text, null);
    }

    protected void showErrorDialog(String text, DialogInterface.OnClickListener clickListener) {
        if (dialog != null) dialog.dismiss();
        if (isFinishing()) return;

        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_error_title)
                .setMessage(text)
                .setPositiveButton(R.string.dialog_ok, clickListener)
                .create();

        dialog.show();
    }

    protected void showConfirmDialog(@StringRes int title, @StringRes int message, DialogInterface.OnClickListener onConfirm) {
        if (dialog != null) dialog.dismiss();
        if (isFinishing()) return;
        dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel_action, (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton(R.string.button_ok, onConfirm)
                .create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dPadNavigationHelper.bind(this);
    }

    @Override
    protected void onPause() {
        MockedNetwork.cancel();
        if (dialog != null) dialog.dismiss();
        dialog = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dPadNavigationHelper.unbind();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (dPadNavigationHelper.hasConsumedKeyDown(keyCode, event)) return true;
        else return super.onKeyDown(keyCode, event);
    }

    protected void hideKeyboard(Boolean force) {
        InputMethodManager inputManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        int hideMethod = InputMethodManager.HIDE_NOT_ALWAYS;
        if (force) hideMethod = 0;
        if (getCurrentFocus() == null) return;
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), hideMethod);
    }
}
