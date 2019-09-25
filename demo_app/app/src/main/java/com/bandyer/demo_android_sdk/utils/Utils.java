package com.bandyer.demo_android_sdk.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.bandyer.android_sdk.Environment;


public class Utils {

    public static int dpToPx(Context context, float dp) {
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static Environment getEnvironmentByName(String name) {
        switch (name) {
            case "sandbox": return Environment.Configuration.sandbox();
            case "production": return Environment.Configuration.production();
            default: return Environment.Configuration.sandbox();
        }
    }
}