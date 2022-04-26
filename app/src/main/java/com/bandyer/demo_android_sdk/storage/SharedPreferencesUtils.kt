package com.bandyer.demo_android_sdk.storage

import android.content.SharedPreferences
import android.os.Parcelable
import com.google.gson.Gson

val gson by lazy {
    Gson()
}

fun SharedPreferences.Editor.putParcelable(key: String, parcelable: Parcelable) {
    putString(key, gson.toJson(parcelable))
}

inline fun <reified T : Parcelable?> SharedPreferences.getParcelable(key: String, default: T): T {
    val jsonString = getString(key, null) ?: return default
    kotlin.runCatching { return gson.fromJson(jsonString, T::class.java)
    }.onFailure { return default }
    return default
}