/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * BaseActivity used to set ButterKnife( library which avoids android-java boilerplate)
 *
 * @author kristiyan
 */
public abstract class BaseActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private Toast toast;

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
        if (dialog != null)
            dialog.dismiss();

        if (isFinishing())
            return;

        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_error_title)
                .setMessage(text)
                .setPositiveButton(R.string.dialog_ok, null)
                .create();

        dialog.show();
    }

    protected void showToast(String text) {
        if (toast != null)
            toast.cancel();
        if (isFinishing())
            return;
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();

    }

    @Override
    protected void onStop() {
        if (dialog != null)
            dialog.dismiss();
        if (toast != null)
            toast.cancel();
        super.onStop();
    }
}
