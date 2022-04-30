package com.bandyer.demo_android_sdk.storage

import android.annotation.SuppressLint
import android.content.Context
import com.bandyer.android_sdk.tool_configuration.Configuration
import com.bandyer.android_sdk.tool_configuration.call.CallConfiguration
import com.bandyer.android_sdk.tool_configuration.call.SimpleCallConfiguration
import com.bandyer.android_sdk.tool_configuration.chat.ChatConfiguration
import com.bandyer.android_sdk.tool_configuration.chat.SimpleChatConfiguration
import com.kaleyra.collaboration_suite_utils.ContextRetainer.Companion.context

object DefaultConfigurationManager {
    private const val preferenceKey = "DefaultConfigurationPrefs"
    private const val callConfigurationKey = "CALL_CONFIGURATION"
    private const val chatConfigurationKey = "CHAT_CONFIGURATION"

    private val prefs by lazy { context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE) }

    fun getDefaultCallConfiguration(): CallConfiguration = prefs.getString(callConfigurationKey, null)?.let { Configuration.decode(it) } ?: SimpleCallConfiguration()
    fun getDefaultChatConfiguration(): ChatConfiguration = prefs.getString(chatConfigurationKey, null)?.let { Configuration.decode(it) } ?: SimpleChatConfiguration()

    @SuppressLint("ApplySharedPref")
    fun saveDefaultCallConfiguration(callConfiguration: CallConfiguration) = prefs.edit().apply {
        putString(callConfigurationKey, callConfiguration.encode())
        commit()
    }

    @SuppressLint("ApplySharedPref")
    fun saveDefaultChatConfiguration(chatConfiguration: ChatConfiguration) = prefs.edit().apply {
        putString(chatConfigurationKey, chatConfiguration.encode())
        commit()
    }

    @SuppressLint("ApplySharedPref")
    fun clearAll() = prefs.edit().apply {
        clear()
        commit()
    }
}