package com.bandyer.demo_android_sdk.exceptions;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bandyer.android_sdk.exceptions.BandyerUnhandledExceptionBroadcastReceiver;
import com.bandyer.demo_android_sdk.BuildConfig;
import com.crashlytics.android.Crashlytics;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Broadcast Receiver to be used to receive unexpected exceptions' stacktrace from Bandyer SDK.
 */
public class BandyerExceptionReceiver extends BandyerUnhandledExceptionBroadcastReceiver {

    static final String TAG = "BANDYER SDK EXCEPTION";

    @Override
    public void onException(@NonNull Throwable error) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);

        Log.e(TAG, stringWriter.toString());

        if (!BuildConfig.ENABLE_CRASHLYTICS) return;
        Crashlytics.getInstance().core.logException(error);
    }
}
