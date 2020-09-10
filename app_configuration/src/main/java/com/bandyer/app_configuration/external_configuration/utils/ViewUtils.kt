/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.utils

import android.content.Context
import android.util.DisplayMetrics
import java.util.*

private val dipsMap = HashMap<Float, Int>()

/**
 * Convert dp value in pixels
 * @param dp value
 * @return value in pixels
 */
fun Context.dp2px(dp: Float): Int {
    dipsMap[dp]?.let { return it }

    val metrics = resources.displayMetrics
    val value = (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    dipsMap[dp] = value

    return value
}