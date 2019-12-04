package com.bandyer.demo_android_sdk.exceptions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bandyer.demo_android_sdk.BuildConfig;
import com.crashlytics.android.Crashlytics;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Broadcast Receiver to be used to receive unexpected exceptions' stacktrace from Bandyer SDK.
 */
public class BandyerUnhandledExceptionBroadcastReceiver extends BroadcastReceiver {

    static final String TAG = "BANDYER SDK EXCEPTION";
    static final String BANDYER_EXCEPTION_PAYLOAD = "throwable_payload";

    public BandyerUnhandledExceptionBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Throwable error = (Throwable) intent.getSerializableExtra(BANDYER_EXCEPTION_PAYLOAD);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);

        Log.e(TAG, stringWriter.toString());

        if (!BuildConfig.ENABLE_CRASHLYTICS) return;
        Crashlytics.getInstance().core.logException(error);
    }
}
