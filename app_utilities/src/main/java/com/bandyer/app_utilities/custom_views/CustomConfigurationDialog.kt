/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.custom_views

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
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
import com.bandyer.android_sdk.tool_configuration.*
import com.bandyer.app_configuration.external_configuration.model.Configuration
import com.bandyer.app_utilities.R
import com.bandyer.app_utilities.custom_views.CallOptionsDialogView.CallOptionsView
import kotlinx.android.synthetic.main.call_options_container_layout.*
import java.util.*

class CustomConfigurationDialog() : DialogFragment() {
    private var configuration: Configuration? = null
    private var calleeSelected: ArrayList<String?>? = null
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
        calleeSelected = requireArguments().getStringArrayList("callees")
        callOptionsDialogType = CallOptionsDialogType.valueOf(requireArguments().getString("type")!!)
        configuration = requireArguments().getParcelable("configuration")
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
       val callOptionsDialogView = CallOptionsDialogView(requireContext(), callOptionsDialogType, configuration!!)
        val textViewTitle = callOptionsDialogView.findViewById<TextView>(R.id.title)
        if (Build.VERSION.SDK_INT >= 23) textViewTitle.text = title else textViewTitle.visibility = View.GONE

        callOptionsDialogView.findViewById<Button>(R.id.action).setOnClickListener {
            setFragmentResult("customize_configuration", bundleOf("configuration" to when(callOptionsDialogType) {
                CallOptionsDialogType.CALL -> getCallConfiguration(callOptionsDialogView)
                CallOptionsDialogType.CHAT -> getChatConfiguration(callOptionsDialogView)
            }, "call_type" to when(callOptionsDialogType) {
                CallOptionsDialogType.CALL -> when {
                    callOptionsDialogView.isAudioOnlyCallChecked -> Callype.AUDIO_ONLY.name
                    callOptionsDialogView.isAudioUpgradableCallChecked -> CallType.AUDIO_UPGRADABLE.name
                    callOptionsDialogView.isAudioVideoCallChecked -> CallType.AUDIO_VIDEO.name
                    else -> null
                }
                CallOptionsDialogType.CHAT -> null
            }))
        }

        callOptionsDialogView.findViewById<View>(R.id.cancel_action).setOnClickListener { dismiss() }

        return callOptionsDialogView
    }

    private val title: String
        get() {
            return if (callOptionsDialogType == CallOptionsDialogType.CHAT) {
                "Chat with " + TextUtils.join(", ", calleeSelected!!)
            } else {
                "Call " + TextUtils.join(", ", calleeSelected!!)
            }
        }

    override fun dismiss() {
        removeLifecycleObserver()
        if (dialog != null) dialog!!.dismiss()
    }

    private fun getCallConfiguration(callOptionsDialogView: CallOptionsDialogView) = CallConfiguration(when {
        callOptionsDialogView.isAudioOnlyCallChecked -> getCallCapabilities(callOptionsDialogView.audioOnlyCallOptionsView!!)
        callOptionsDialogView.isAudioUpgradableCallChecked -> getCallCapabilities(callOptionsDialogView.audioUpgradableCallOptionsView!!)
        callOptionsDialogView.isAudioVideoCallChecked -> getCallCapabilities(callOptionsDialogView.audioVideoCallOptionsView!!)
        else -> CallConfiguration.CapabilitySet()
    }, when {
        callOptionsDialogView.isAudioOnlyCallChecked -> getOptions(callOptionsDialogView.audioOnlyCallOptionsView!!)
        callOptionsDialogView.isAudioUpgradableCallChecked -> getOptions(callOptionsDialogView.audioUpgradableCallOptionsView!!)
        callOptionsDialogView.isAudioVideoCallChecked -> getOptions(callOptionsDialogView.audioVideoCallOptionsView!!)
        else -> CallOptions()
    }
    )

    private fun getChatConfiguration(callOptionsDialogView: CallOptionsDialogView): ChatConfiguration = ChatConfiguration(ChatConfiguration.CapabilitySet(
            if (callOptionsDialogView.isAudioOnlyCallChecked) getChatCallCapabilities(callOptionsDialogView.audioOnlyCallOptionsView!!) else null,
            if (callOptionsDialogView.isAudioUpgradableCallChecked) getChatCallCapabilities(callOptionsDialogView.audioUpgradableCallOptionsView!!) else null,
            if (callOptionsDialogView.isAudioVideoCallChecked) getChatCallCapabilities(callOptionsDialogView.audioVideoCallOptionsView!!) else null))

    private fun getCallCapabilities(optionView: CallOptionsView): CallCapabilitySet = CallConfiguration.CapabilitySet(
            if (optionView.isChatChecked) ChatConfiguration(
                    ChatConfiguration.CapabilitySet(
                            audioCall = getChatCallCapabilities(optionView),
                            audioVideoCall = getChatCallCapabilities(optionView),
                            audioUpgradableCall = getChatCallCapabilities(optionView))
            ) else null,
            if (optionView.isFileShareChecked) FileShareConfiguration() else null,
            if (optionView.isScreenShareChecked) ScreenShareConfiguration() else null,
            if (optionView.isWhiteboardChecked) WhiteboardConfiguration() else null)

    private fun getChatCallCapabilities(optionView: CallOptionsView): ChatConfiguration.CapabilitySet.CallConfiguration =
            ChatConfiguration.CapabilitySet.CallConfiguration(ChatConfiguration.CapabilitySet.CallConfiguration.CapabilitySet(
                    if (optionView.isFileShareChecked) FileShareConfiguration() else null,
                    if (optionView.isScreenShareChecked) ScreenShareConfiguration() else null,
                    if (optionView.isWhiteboardChecked) WhiteboardConfiguration() else null),
                    getOptions(optionView))

    private fun getOptions(optionView: CallOptionsView): CallOptionSet {
        return CallOptions(
            recordingEnabled = optionView.isRecordingChecked,
            backCameraAsDefault = optionView.isBackCameraChecked,
            disableProximitySensor = optionView.isProximitySensorDisabled,
            callRating = optionView.isCallRatingChecked)
    }

    companion object {

        @JvmStatic
        fun show(context: AppCompatActivity,
                 calleeSelected: ArrayList<String>,
                 callOptionsDialogType: CallOptionsDialogType,
                 configuration: Configuration) {
            val f = CustomConfigurationDialog()
            val args = Bundle()
            args.putStringArrayList("callees", calleeSelected)
            args.putString("type", callOptionsDialogType.toString())
            args.putParcelable("configuration", configuration)
            f.arguments = args
            f.show(context.supportFragmentManager, calleeSelected.toString())
        }
    }
}