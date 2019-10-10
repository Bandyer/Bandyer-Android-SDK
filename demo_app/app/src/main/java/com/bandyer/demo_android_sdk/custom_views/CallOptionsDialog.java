/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.custom_views;

import android.annotation.SuppressLint;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bandyer.android_sdk.intent.call.CallCapabilities;
import com.bandyer.android_sdk.intent.call.CallOptions;
import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;

import java.util.ArrayList;

public class CallOptionsDialog extends DialogFragment {

    private ArrayList<String> calleeSelected;
    private Configuration configuration = Configuration.CALL;
    private OnCallOptionsUpdatedListener onCallOptionsUpdatedListener;

    private LifecycleObserver lifecycleObserver = new LifecycleObserver() {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void disconnectListener() {
            removeLifecycleObserver();
            dismiss();
        }
    };

    public enum Configuration {
        CALL,
        CHAT
    }

    public enum CallOptionsType {
        AUDIO_ONLY,
        AUDIO_UPGRADABLE,
        AUDIO_VIDEO
    }

    public CallOptionsDialog() {}

    public static CallOptionsDialog newInstance(ArrayList<String> calleeSelected, Configuration configuration) {
        CallOptionsDialog f = new CallOptionsDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("callees", calleeSelected);
        args.putString("configuration", configuration.toString());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calleeSelected = getArguments().getStringArrayList("callees");
        configuration = Configuration.valueOf(getArguments().getString("configuration"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT <= 23) {
            getDialog().setTitle(getTitle());
        }
        addLifecycleObserver();
        return setup();
    }

    private void addLifecycleObserver() {
        if (getActivity() != null && getActivity() instanceof AppCompatActivity)
            getActivity().getLifecycle().addObserver(lifecycleObserver);
    }

    private void removeLifecycleObserver() {
        if (getActivity() != null && getActivity() instanceof AppCompatActivity)
            getActivity().getLifecycle().removeObserver(lifecycleObserver);
    }

    public void setOnCallOptionsUpdatedListener(OnCallOptionsUpdatedListener onCallOptionsUpdatedListener) {
        this.onCallOptionsUpdatedListener = onCallOptionsUpdatedListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
        if (ConfigurationPrefsManager.isSimplifiedVersionEnabled(getContext()))
            dismiss();
    }

    private View setup() {
        CallOptionsDialogView callOptionsDialogView = new CallOptionsDialogView(getContext(), configuration);

        TextView textViewTitle = callOptionsDialogView.findViewById(R.id.title);
        if (Build.VERSION.SDK_INT >= 23) {
            textViewTitle.setText(getTitle());
        } else {
            textViewTitle.setVisibility(View.GONE);
        }

        Button action = callOptionsDialogView.findViewById(R.id.action);
        action.setOnClickListener(v -> {

            if (callOptionsDialogView.isAudioOnlyCallChecked()) {
                onCallOptionsUpdatedListener.onCallOptionsUpdated(
                        CallOptionsType.AUDIO_ONLY,
                        getCapabilities(callOptionsDialogView.getAudioOnlyCallOptionsView()),
                        getOptions(callOptionsDialogView.getAudioOnlyCallOptionsView())
                );
            }

            if (callOptionsDialogView.isAudioUpgradableCallChecked()) {
                onCallOptionsUpdatedListener.onCallOptionsUpdated(
                        CallOptionsType.AUDIO_UPGRADABLE,
                        getCapabilities(callOptionsDialogView.getAudioUpgradableCallOptionsView()),
                        getOptions(callOptionsDialogView.getAudioUpgradableCallOptionsView())
                );
            }

            if (callOptionsDialogView.isAudioVideoCallChecked()) {
                onCallOptionsUpdatedListener.onCallOptionsUpdated(
                        CallOptionsType.AUDIO_VIDEO,
                        getCapabilities(callOptionsDialogView.getAudioVideoCallOptionsView()),
                        getOptions(callOptionsDialogView.getAudioVideoCallOptionsView())
                );
            }

            onCallOptionsUpdatedListener.onOptionsConfirmed();
        });

        callOptionsDialogView.findViewById(R.id.cancel_action).setOnClickListener(v -> {
            dismiss();
            onCallOptionsUpdatedListener = null;
        });

        if (ConfigurationPrefsManager.isSimplifiedVersionEnabled(getContext()))
            action.performClick();

        return callOptionsDialogView;
    }

    private String getTitle() {
        String title;
        if (configuration == Configuration.CHAT) {
            title = "Chat with " + TextUtils.join(", ", calleeSelected);
        } else {
            title = "Call " + TextUtils.join(", ", calleeSelected);
        }
        return title;
    }

    public void dismiss() {
        removeLifecycleObserver();
        if (getDialog() != null) getDialog().dismiss();
    }

    @SuppressLint("NewApi")
    private CallOptions getOptions(CallOptionsDialogView.CallOptionsView optionView) {
        return new CallOptions(optionView.isRecordingChecked(), optionView.isBackCameraChecked(), optionView.isProximitySensorDisabled());
    }

    @SuppressLint("NewApi")
    private CallCapabilities getCapabilities(CallOptionsDialogView.CallOptionsView optionView) {
        return new CallCapabilities(optionView.isChatChecked(),
                optionView.isFileShareChecked(),
                optionView.isScreenShareChecked(),
                optionView.isWhiteboardChecked());
    }

    public interface OnCallOptionsUpdatedListener {
        void onCallOptionsUpdated(CallOptionsType callOptionsType, CallCapabilities callCapabilities, CallOptions callOptions);
        void onOptionsConfirmed();
    }
}
