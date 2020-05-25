package com.bandyer.demo_android_sdk.utils.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @author kristiyan
 */
public class QRConfigurationActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    public static final String CONFIGURATION_URL = "configuration_url";
    public static final int  REQUEST_CONFIGURATION_VIA_QR = 155;

    private ZXingScannerView mScannerView;

    public static void show(AppCompatActivity context) {
        Intent intent = new Intent(context, QRConfigurationActivity.class);
        context.startActivityForResult(intent, REQUEST_CONFIGURATION_VIA_QR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent =  new Intent();
        intent.setData(Uri.parse(rawResult.getText()));
        setResult(RESULT_OK, intent);
        finish();
    }
}