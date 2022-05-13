/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bandyer.demo_android_sdk.ui.custom_views

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bandyer.android_sdk.call.model.CallType
import com.bandyer.android_sdk.intent.call.CallOptions
import com.bandyer.android_sdk.intent.call.CallRecordingType
import com.bandyer.android_sdk.tool_configuration.call.CallConfiguration
import com.bandyer.android_sdk.tool_configuration.call.CustomCallConfiguration
import com.bandyer.android_sdk.tool_configuration.chat.ChatConfiguration
import com.bandyer.android_sdk.tool_configuration.chat.CustomChatConfiguration
import com.bandyer.android_sdk.tool_configuration.file_share.CustomFileShareConfiguration
import com.bandyer.android_sdk.tool_configuration.screen_share.CustomScreenShareConfiguration
import com.bandyer.android_sdk.tool_configuration.whiteboard.CustomWhiteboardConfiguration
import com.bandyer.demo_android_sdk.R
import com.bandyer.demo_android_sdk.storage.DefaultConfigurationManager
import com.bandyer.demo_android_sdk.ui.custom_views.CallOptionsDialogView.CallOptionsView
import com.kaleyra.app_configuration.model.CallOptionsType
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager

class CustomConfigurationDialog : DialogFragment() {

    private var callConfiguration: CallConfiguration? = null
    private var callOptionsType: CallOptionsType? = null
    private var chatConfiguration: ChatConfiguration? = null
    private val appConfiguration by lazy {
        ConfigurationPrefsManager.getConfiguration(dialog!!.context)
    }

    private var callOptionsDialogType = CallOptionsDialogType.CALL
    private val lifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun disconnectListener() {
            removeLifecycleObserver()
            dismiss()
        }
    }

    enum class CallOptionsDialogType {
        CALL, CHAT
    }

    enum class Callype {
        AUDIO_ONLY, AUDIO_UPGRADABLE, AUDIO_VIDEO
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callOptionsDialogType = CallOptionsDialogType.valueOf(requireArguments().getString("type")!!)
        when (callOptionsDialogType) {
            CallOptionsDialogType.CALL -> {
                callConfiguration = DefaultConfigurationManager.getDefaultCallConfiguration()
                callOptionsType = CallOptionsType.valueOf(requireArguments().getString("call_type", "AUDIO_VIDEO"))
            }
            CallOptionsDialogType.CHAT ->
                chatConfiguration = DefaultConfigurationManager.getDefaultChatConfiguration()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (Build.VERSION.SDK_INT <= 23) {
            dialog!!.setTitle(title)
        }
        addLifecycleObserver()
        return setup()
    }

    private fun addLifecycleObserver() {
        if (activity != null && activity is AppCompatActivity) requireActivity().lifecycle.addObserver(lifecycleObserver)
    }

    private fun removeLifecycleObserver() {
        if (activity != null && activity is AppCompatActivity) requireActivity().lifecycle.removeObserver(lifecycleObserver)
    }

    override fun onResume() {
        super.onResume()
        val params = dialog!!.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params
    }

    private fun setup(): View {
        val callOptionsDialogView = CallOptionsDialogView(requireContext(), callOptionsDialogType, callOptionsType, callConfiguration, chatConfiguration)
        val textViewTitle = callOptionsDialogView.findViewById<TextView>(R.id.title)
        if (Build.VERSION.SDK_INT >= 23) textViewTitle.text = title else textViewTitle.visibility = View.GONE

        val actionButton = callOptionsDialogView.findViewById<Button>(R.id.action)
        actionButton.setOnClickListener {
            dismiss()
            setFragmentResult(
                "customize_configuration", bundleOf(
                    when (callOptionsDialogType) {
                        CallOptionsDialogType.CALL -> "call_configuration" to getCallConfiguration(callOptionsDialogView)
                        CallOptionsDialogType.CHAT -> "chat_configuration" to getChatConfiguration(callOptionsDialogView)
                    },
                    "call_type" to when (callOptionsDialogType) {
                        CallOptionsDialogType.CALL -> when {
                            callOptionsDialogView.isAudioOnlyCallChecked       -> Callype.AUDIO_ONLY.name
                            callOptionsDialogView.isAudioUpgradableCallChecked -> CallType.AUDIO_UPGRADABLE.name
                            callOptionsDialogView.isAudioVideoCallChecked      -> CallType.AUDIO_VIDEO.name
                            else                                               -> null
                        }
                        CallOptionsDialogType.CHAT -> null
                    },
                    "app_configuration" to appConfiguration.apply {
                        if (callOptionsDialogType == CallOptionsDialogType.CALL) {
                            this.defaultCallType = when {
                                callOptionsDialogView.isAudioOnlyCallChecked       -> CallOptionsType.AUDIO_ONLY
                                callOptionsDialogView.isAudioUpgradableCallChecked -> CallOptionsType.AUDIO_UPGRADABLE
                                callOptionsDialogView.isAudioVideoCallChecked      -> CallOptionsType.AUDIO_VIDEO
                                else                                               -> this.defaultCallType
                            }
                        }
                    }
                )
            )
        }

        callOptionsDialogView.findViewById<View>(R.id.cancel_action).setOnClickListener {
            dismiss()
            setFragmentResult("customize_configuration", bundleOf())
        }

        val mockBiometricCheckbox = callOptionsDialogView.findViewById<CheckBox>(R.id.mock_biometric_authentication_request)
        mockBiometricCheckbox.isChecked = appConfiguration.withMockAuthentication
        mockBiometricCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            appConfiguration.withMockAuthentication = isChecked
        }

        return callOptionsDialogView
    }

    private val title: String
        get() = if (callOptionsDialogType == CallOptionsDialogType.CHAT) "Chat configuration" else "Call configuration"

    override fun dismiss() {
        removeLifecycleObserver()
        if (dialog != null) dialog!!.dismiss()
    }

    private fun getCallConfiguration(callOptionsDialogView: CallOptionsDialogView) = CustomCallConfiguration(
        when {
            callOptionsDialogView.isAudioOnlyCallChecked       -> getCallCapabilities(callOptionsDialogView.audioOnlyCallOptionsView!!)
            callOptionsDialogView.isAudioUpgradableCallChecked -> getCallCapabilities(callOptionsDialogView.audioUpgradableCallOptionsView!!)
            callOptionsDialogView.isAudioVideoCallChecked      -> getCallCapabilities(callOptionsDialogView.audioVideoCallOptionsView!!)
            else                                               -> CustomCallConfiguration.CustomCapabilitySet()
        }, when {
            callOptionsDialogView.isAudioOnlyCallChecked       -> getOptions(callOptionsDialogView.audioOnlyCallOptionsView!!)
            callOptionsDialogView.isAudioUpgradableCallChecked -> getOptions(callOptionsDialogView.audioUpgradableCallOptionsView!!)
            callOptionsDialogView.isAudioVideoCallChecked      -> getOptions(callOptionsDialogView.audioVideoCallOptionsView!!)
            else                                               -> CallOptions()
        }
    )

    private fun getChatConfiguration(callOptionsDialogView: CallOptionsDialogView): ChatConfiguration = CustomChatConfiguration(
        CustomChatConfiguration.CustomCapabilitySet(
            if (callOptionsDialogView.isAudioOnlyCallChecked) getChatCallCapabilities(callOptionsDialogView.audioOnlyCallOptionsView!!) else null,
            if (callOptionsDialogView.isAudioUpgradableCallChecked) getChatCallCapabilities(callOptionsDialogView.audioUpgradableCallOptionsView!!) else null,
            if (callOptionsDialogView.isAudioVideoCallChecked) getChatCallCapabilities(callOptionsDialogView.audioVideoCallOptionsView!!) else null
        )
    )

    private fun getCallCapabilities(optionView: CallOptionsView): CustomCallConfiguration.CustomCapabilitySet = CustomCallConfiguration.CustomCapabilitySet(
        if (optionView.isChatChecked) CustomChatConfiguration(
            CustomChatConfiguration.CustomCapabilitySet(
                audioCallConfiguration = getChatCallCapabilities(optionView),
                audioVideoCallConfiguration = getChatCallCapabilities(optionView),
                audioUpgradableCallConfiguration = getChatCallCapabilities(optionView)
            )
        ) else null,
        if (optionView.isFileShareChecked) CustomFileShareConfiguration() else null,
        if (optionView.isScreenShareChecked) CustomScreenShareConfiguration() else null,
        if (optionView.isWhiteboardChecked) CustomWhiteboardConfiguration() else null
    )

    private fun getChatCallCapabilities(optionView: CallOptionsView): CustomChatConfiguration.CustomCapabilitySet.CustomCallConfiguration =
        CustomChatConfiguration.CustomCapabilitySet.CustomCallConfiguration(
            CustomChatConfiguration.CustomCapabilitySet.CustomCallConfiguration.CustomCapabilitySet(
                if (optionView.isFileShareChecked) CustomFileShareConfiguration() else null,
                if (optionView.isScreenShareChecked) CustomScreenShareConfiguration() else null,
                if (optionView.isWhiteboardChecked) CustomWhiteboardConfiguration() else null
            ),
            getOptions(optionView)
        )

    private fun getOptions(optionView: CallOptionsView) = CallOptions(
        callRecordingType = if (optionView.isRecordingChecked) CallRecordingType.AUTOMATIC else CallRecordingType.NONE,
        backCameraAsDefault = optionView.isBackCameraChecked,
        disableProximitySensor = optionView.isProximitySensorDisabled,
        feedbackEnabled = optionView.isFeedbackChecked
    )

    companion object {

        @JvmStatic
        fun showCallConfigurationDialog(
            context: AppCompatActivity,
            callOptionsType: CallOptionsType
        ) {
            val f = CustomConfigurationDialog()
            val args = Bundle()
            args.putString("type", CallOptionsDialogType.CALL.toString())
            args.putString("call_type", callOptionsType.toString())
            f.arguments = args
            f.show(context.supportFragmentManager, "configuration")
        }

        @JvmStatic
        fun showChatConfigurationDialog(
            context: AppCompatActivity
        ) {
            val f = CustomConfigurationDialog()
            val args = Bundle()
            args.putString("type", CallOptionsDialogType.CHAT.toString())
            f.arguments = args
            f.show(context.supportFragmentManager, "configuration")
        }
    }
}