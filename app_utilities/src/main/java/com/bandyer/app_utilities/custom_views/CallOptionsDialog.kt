/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.custom_views

import android.annotation.SuppressLint
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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bandyer.android_sdk.intent.call.CallCapabilities
import com.bandyer.android_sdk.intent.call.CallOptions
import com.bandyer.app_configuration.external_configuration.model.CallOptionsType
import com.bandyer.app_configuration.external_configuration.model.Configuration
import com.bandyer.app_utilities.R
import com.bandyer.app_utilities.custom_views.CallOptionsDialogView.CallOptionsView
import java.util.*

class CallOptionsDialog : DialogFragment() {
    private var configuration: Configuration? = null
    private var calleeSelected: ArrayList<String?>? = null
    private var callOptionsDialogType = CallOptionsDialogType.CALL
    private var onCallOptionsUpdatedListener: OnCallOptionsUpdatedListener? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calleeSelected = arguments!!.getStringArrayList("callees")
        callOptionsDialogType = CallOptionsDialogType.valueOf(arguments!!.getString("type")!!)
        configuration = arguments!!.getParcelable("configuration")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (Build.VERSION.SDK_INT <= 23) {
            dialog!!.setTitle(title)
        }
        addLifecycleObserver()
        return setup()
    }

    private fun addLifecycleObserver() {
        if (activity != null && activity is AppCompatActivity) activity!!.lifecycle.addObserver(lifecycleObserver)
    }

    private fun removeLifecycleObserver() {
        if (activity != null && activity is AppCompatActivity) activity!!.lifecycle.removeObserver(lifecycleObserver)
    }

    fun setOnCallOptionsUpdatedListener(onCallOptionsUpdatedListener: OnCallOptionsUpdatedListener?) {
        this.onCallOptionsUpdatedListener = onCallOptionsUpdatedListener
    }

    override fun onResume() {
        super.onResume()
        val params = dialog!!.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params
        if (configuration!!.useSimplifiedVersion) dismiss()
    }

    private fun setup(): View {
        val callOptionsDialogView = CallOptionsDialogView(context!!, callOptionsDialogType, configuration!!)
        val textViewTitle = callOptionsDialogView.findViewById<TextView>(R.id.title)
        if (Build.VERSION.SDK_INT >= 23) {
            textViewTitle.text = title
        } else {
            textViewTitle.visibility = View.GONE
        }
        val action = callOptionsDialogView.findViewById<Button>(R.id.action)
        action.setOnClickListener {
            if (onCallOptionsUpdatedListener == null) return@setOnClickListener
            if (callOptionsDialogView.isAudioOnlyCallChecked) {
                onCallOptionsUpdatedListener!!.onCallOptionsUpdated(
                        CallOptionsType.AUDIO_ONLY,
                        getCapabilities(callOptionsDialogView.audioOnlyCallOptionsView!!),
                        getOptions(callOptionsDialogView.audioOnlyCallOptionsView!!)
                )
            }
            if (callOptionsDialogView.isAudioUpgradableCallChecked) {
                onCallOptionsUpdatedListener!!.onCallOptionsUpdated(
                        CallOptionsType.AUDIO_UPGRADABLE,
                        getCapabilities(callOptionsDialogView.audioUpgradableCallOptionsView!!),
                        getOptions(callOptionsDialogView.audioUpgradableCallOptionsView!!)
                )
            }
            if (callOptionsDialogView.isAudioVideoCallChecked) {
                onCallOptionsUpdatedListener!!.onCallOptionsUpdated(
                        CallOptionsType.AUDIO_VIDEO,
                        getCapabilities(callOptionsDialogView.audioVideoCallOptionsView!!),
                        getOptions(callOptionsDialogView.audioVideoCallOptionsView!!)
                )
            }
            onCallOptionsUpdatedListener!!.onOptionsConfirmed()
        }
        callOptionsDialogView.findViewById<View>(R.id.cancel_action).setOnClickListener {
            dismiss()
            onCallOptionsUpdatedListener = null
        }
        if (configuration!!.useSimplifiedVersion) action.performClick()
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

    @SuppressLint("NewApi")
    private fun getOptions(optionView: CallOptionsView): CallOptions {
        return CallOptions(optionView.isRecordingChecked, optionView.isBackCameraChecked, optionView.isProximitySensorDisabled)
    }

    @SuppressLint("NewApi")
    private fun getCapabilities(optionView: CallOptionsView): CallCapabilities {
        return CallCapabilities(optionView.isChatChecked,
                optionView.isFileShareChecked,
                optionView.isScreenShareChecked,
                optionView.isWhiteboardChecked)
    }

    interface OnCallOptionsUpdatedListener {
        fun onCallOptionsUpdated(callOptionsType: CallOptionsType?, callCapabilities: CallCapabilities?, callOptions: CallOptions?)
        fun onOptionsConfirmed()
    }

    companion object {
        @JvmStatic
        fun newInstance(calleeSelected: ArrayList<String?>?,
                        callOptionsDialogType: CallOptionsDialogType,
                        configuration: Configuration): CallOptionsDialog {
            val f = CallOptionsDialog()
            val args = Bundle()
            args.putStringArrayList("callees", calleeSelected)
            args.putString("type", callOptionsDialogType.toString())
            args.putParcelable("configuration", configuration)
            f.arguments = args
            return f
        }
    }
}