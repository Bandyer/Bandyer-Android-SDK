package com.bandyer.demo_android_sdk.storage

import android.content.Context
import com.bandyer.android_sdk.tool_configuration.call.CallConfiguration
import com.bandyer.android_sdk.tool_configuration.call.CustomCallConfiguration
import com.bandyer.android_sdk.tool_configuration.call.SimpleCallConfiguration
import com.bandyer.android_sdk.tool_configuration.chat.ChatConfiguration
import com.bandyer.android_sdk.tool_configuration.chat.CustomChatConfiguration
import com.bandyer.android_sdk.tool_configuration.chat.SimpleChatConfiguration
import com.kaleyra.collaboration_suite_utils.ContextRetainer.Companion.context

object DefaultConfigurationManager {
    private const val preferenceKey = "ConfigurationPrefs"
    private const val callConfigurationKey = "CALL_CONFIGURATION"
    private const val chatConfigurationKey = "CHAT_CONFIGURATION"

    fun getDefaultCallConfiguration(): CustomCallConfiguration {
        val prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)
        return prefs.getParcelable<CustomCallConfiguration>(callConfigurationKey, SimpleCallConfiguration())
    }

    fun getDefaultChatConfiguration(): CustomChatConfiguration {
        val prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)
        return prefs.getParcelable<CustomChatConfiguration>(chatConfigurationKey,  SimpleChatConfiguration())
    }

    fun saveDefaultCallConfiguration(customCallConfiguration: CustomCallConfiguration) {
        val editor = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).edit()
        editor.putParcelable(callConfigurationKey, customCallConfiguration)
        editor.apply()
    }

    fun saveDefaultChatConfiguration(defaultChatConfiguration: ChatConfiguration) {
        val editor = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).edit()
        editor.putParcelable(chatConfigurationKey, defaultChatConfiguration)
        editor.apply()
    }

    fun clearAll() {
        val editor = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).edit()
        editor.putParcelable(callConfigurationKey, SimpleCallConfiguration())
        editor.putParcelable(chatConfigurationKey, SimpleChatConfiguration())
        editor.apply()
    }
}