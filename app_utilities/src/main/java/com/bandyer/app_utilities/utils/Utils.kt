package com.bandyer.app_utilities.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import com.bandyer.android_sdk.Environment
import com.bandyer.android_sdk.Environment.Configuration.production
import com.bandyer.android_sdk.Environment.Configuration.sandbox
import com.bandyer.android_sdk.EnvironmentImpl

object Utils {
    fun dpToPx(context: Context, dp: Float): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    @JvmStatic
    fun getEnvironmentByName(name: String?): Environment {
        return when (name) {
            "sandbox" -> sandbox()
            "production" -> production()
            else -> EnvironmentImpl("https://$name.bandyer.com")
        }
    }

    @JvmStatic
    fun capitalize(s: String): String {
        return if (s.isEmpty()) s else s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()
    }

    fun isGoogleGlassDevice(): Boolean = Build.DEVICE == "glass_v3"
}