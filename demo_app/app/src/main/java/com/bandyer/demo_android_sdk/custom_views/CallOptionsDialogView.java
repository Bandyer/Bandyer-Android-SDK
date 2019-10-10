/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.storage.DefaultCallSettingsManager;
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;

import static com.bandyer.demo_android_sdk.utils.Utils.dpToPx;

@SuppressLint("ViewConstructor")
public class CallOptionsDialogView extends LinearLayout {

    public CallOptionsDialog.Configuration configuration;

    public CallOptionsView audioOnlyCallOptions;
    public CallOptionsView audioUpgradableCallOptions;
    public CallOptionsView audioVideoCallOptions;

    public CallOptionsDialogView(Context context, CallOptionsDialog.Configuration configuration) {
        super(context);
        setup(context, configuration);
    }

    private void setup(Context context, CallOptionsDialog.Configuration configuration) {
        this.configuration = configuration;
        setOrientation(LinearLayout.VERTICAL);
        @SuppressLint("InflateParams")
        View layout = LayoutInflater.from(context).inflate(R.layout.call_options_dialog_layout, null);
        addView(layout);
        Button chat = findViewById(R.id.action);
        TextView info = findViewById(R.id.info);

        audioOnlyCallOptions = new CallOptionsView(context);
        audioOnlyCallOptions.expansionHeader.titleView.setText(context.getString(R.string.audio_only));
        audioOnlyCallOptions.callOptionsViewContainer.findViewById(R.id.call_options_back_camera).setVisibility(View.GONE);
        audioUpgradableCallOptions = new CallOptionsView(context);
        audioUpgradableCallOptions.expansionHeader.titleView.setText(context.getString(R.string.audio_upgradable));
        audioVideoCallOptions = new CallOptionsView(context);
        audioVideoCallOptions.expansionHeader.titleView.setText(context.getString(R.string.audio_video));

        setupCallOptionsViewCompoundClickListener(audioOnlyCallOptions);
        setupCallOptionsViewCompoundClickListener(audioUpgradableCallOptions);
        setupCallOptionsViewCompoundClickListener(audioVideoCallOptions);

        LinearLayout options = layout.findViewById(R.id.options);
        options.addView(audioVideoCallOptions, 2);
        options.addView(audioUpgradableCallOptions, 2);
        options.addView(audioOnlyCallOptions, 2);

        AppCompatButton selectAllCallCapabilityButton = findViewById(R.id.select_all_call_capabilities);
        AppCompatButton deselectAllCallCapabilityButton = findViewById(R.id.deselect_all_call_capabilities);

        AppCompatButton selectAllCallOptionsButton = findViewById(R.id.select_all_call_options);
        AppCompatButton deselectAllCallOptionsButton = findViewById(R.id.deselect_all_call_options);

        deselectAllCallCapabilityButton.setTextColor(Color.BLACK);
        deselectAllCallOptionsButton.setTextColor(Color.BLACK);

        selectAllCallCapabilityButton.setOnClickListener((buttonView) -> {
            if (configuration == CallOptionsDialog.Configuration.CALL) {
                getSelectedOptionsView().selectAllCallCapabilities();
                return;
            }
            selectAllCallCapabilities();
        });

        deselectAllCallCapabilityButton.setOnClickListener((buttonView) -> {
            deselectAllCallCapabilities();
        });

        selectAllCallOptionsButton.setOnClickListener((buttonView) -> {
            if (configuration == CallOptionsDialog.Configuration.CALL) {
                getSelectedOptionsView().selectAllCallOptions();
                return;
            }
            selectAllCallOptions();
        });

        deselectAllCallOptionsButton.setOnClickListener((buttonView) -> {
            deselectAllCallOptions();
        });

        switch (configuration) {
            case CALL:
                chat.setText(context.getString(R.string.call));
                info.setText(context.getString(R.string.select_call_type));
                deSelectAllCallTypes();
                CallOptionsDialog.CallOptionsType defaultCallOptionType = DefaultCallSettingsManager.getDefaultCallType(getContext());
                switch (defaultCallOptionType) {
                    case AUDIO_ONLY:
                        audioOnlyCallOptions.selectingProgrammatically = true;
                        audioOnlyCallOptions.expansionHeader.titleView.setChecked(true);
                        break;
                    case AUDIO_UPGRADABLE:
                        audioUpgradableCallOptions.selectingProgrammatically = true;
                        audioUpgradableCallOptions.expansionHeader.titleView.setChecked(true);
                        break;
                    case AUDIO_VIDEO:
                        audioVideoCallOptions.selectingProgrammatically = true;
                        audioVideoCallOptions.expansionHeader.titleView.setChecked(true);
                        break;
                }
                break;
            case CHAT:
                chat.setText(context.getString(R.string.chat));
                info.setText(context.getString(R.string.select_call_capabilities_from_chat_ui));
                selectAllCallTypes();
                break;
        }
    }

    public CallOptionsView getSelectedOptionsView() {
        if (configuration == CallOptionsDialog.Configuration.CHAT) return null;
        if (audioOnlyCallOptions.isChecked()) return audioOnlyCallOptions;
        else if (audioUpgradableCallOptions.isChecked()) return audioUpgradableCallOptions;
        else if (audioVideoCallOptions.isChecked()) return audioVideoCallOptions;
        else return null;
    }

    public CallOptionsView getAudioOnlyCallOptionsView() {
        return audioOnlyCallOptions;
    }

    public CallOptionsView getAudioUpgradableCallOptionsView() {
        return audioUpgradableCallOptions;
    }

    public CallOptionsView getAudioVideoCallOptionsView() {
        return audioVideoCallOptions;
    }

    public boolean isAudioOnlyCallChecked() {
        return audioOnlyCallOptions.isChecked();
    }

    public boolean isAudioUpgradableCallChecked() {
        return audioUpgradableCallOptions.isChecked();
    }

    public boolean isAudioVideoCallChecked() {
        return audioVideoCallOptions.isChecked();
    }

    private void selectAllCallTypes() {
        audioOnlyCallOptions.selectingProgrammatically = true;
        audioOnlyCallOptions.expansionHeader.titleView.setChecked(true);
        audioUpgradableCallOptions.selectingProgrammatically = true;
        audioUpgradableCallOptions.expansionHeader.titleView.setChecked(true);
        audioVideoCallOptions.selectingProgrammatically = true;
        audioVideoCallOptions.expansionHeader.titleView.setChecked(true);
    }

    private void deSelectAllCallTypes() {
        audioOnlyCallOptions.selectingProgrammatically = false;
        audioOnlyCallOptions.expansionHeader.titleView.setChecked(false);
        audioUpgradableCallOptions.selectingProgrammatically = false;
        audioUpgradableCallOptions.expansionHeader.titleView.setChecked(false);
        audioVideoCallOptions.selectingProgrammatically = false;
        audioVideoCallOptions.expansionHeader.titleView.setChecked(false);
    }

    private void selectAllCallCapabilities() {
        audioOnlyCallOptions.selectAllCallCapabilities();
        audioUpgradableCallOptions.selectAllCallCapabilities();
        audioVideoCallOptions.selectAllCallCapabilities();
    }

    private void selectAllCallOptions() {
        audioOnlyCallOptions.selectAllCallOptions();
        audioUpgradableCallOptions.selectAllCallOptions();
        audioVideoCallOptions.selectAllCallOptions();
    }

    private void deselectAllCallCapabilities() {
        audioOnlyCallOptions.deselectAllCallCapabilities();
        audioUpgradableCallOptions.deselectAllCallCapabilities();
        audioVideoCallOptions.deselectAllCallCapabilities();
    }

    private void deselectAllCallOptions() {
        audioOnlyCallOptions.deselectAllCallOptions();
        audioUpgradableCallOptions.deselectAllCallOptions();
        audioVideoCallOptions.deselectAllCallOptions();
    }

    private void setupCallOptionsViewCompoundClickListener(final CallOptionsView callOptionsView) {
        callOptionsView.expansionHeader.titleView.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked && configuration == CallOptionsDialog.Configuration.CALL) {

                if (callOptionsView == audioOnlyCallOptions) {
                    deselectAll(audioUpgradableCallOptions);
                    deselectAll(audioVideoCallOptions);
                } else if (callOptionsView == audioUpgradableCallOptions) {
                    deselectAll(audioOnlyCallOptions);
                    deselectAll(audioVideoCallOptions);
                } else if (callOptionsView == audioVideoCallOptions) {
                    deselectAll(audioUpgradableCallOptions);
                    deselectAll(audioOnlyCallOptions);
                }
            }

            callOptionsView.applyCallOptionsPreferences();

            if(isChecked) {
                if (callOptionsView.selectingProgrammatically) {
                    callOptionsView.selectingProgrammatically = false;
                    return;
                }
                callOptionsView.callOptionsViewContainer.expand(true);
            } else {
                callOptionsView.deselectAllCallOptions();
                callOptionsView.deselectAllCallCapabilities();
                callOptionsView.expansionHeader.titleView.setChecked(false);
                callOptionsView.callOptionsViewContainer.collapse(true);
            }
        });
    }

    private void deselectAll(CallOptionsView callOptionsView) {
        callOptionsView.expansionHeader.titleView.setChecked(false);
        callOptionsView.deselectAllCallCapabilities();
        callOptionsView.deselectAllCallOptions();
        callOptionsView.callOptionsViewContainer.collapse(false);
        callOptionsView.selectingProgrammatically = false;
    }

    public class CallOptionsView extends LinearLayout {

        public CallOptionViewHeader expansionHeader;
        public CallOptionsViewContainer callOptionsViewContainer;
        private boolean selectingProgrammatically;

        class CallOptionViewHeader extends ExpansionHeader {

            CompoundButton titleView;

            public CallOptionViewHeader(Context context) {
                super(context);
                View view = null;
                switch (configuration) {
                    case CALL:
                        view = inflate(context, R.layout.call_options_header_layout, this);
                        break;
                    case CHAT:
                        view = inflate(context, R.layout.chat_options_header_layout, this);
                        break;
                }
                titleView = view.findViewById(R.id.call_options_title);
                view.setPadding(0, 0, 0, dpToPx(context, 16));
                setToggleOnClick(true);
                setHeaderIndicatorId(R.id.call_options_headerIndicator);
            }
        }

        class CallOptionsViewContainer extends ExpansionLayout {

            public CallOptionsViewContainer(Context context) {
                super(context);
                setup(context);
            }

            public CallOptionsViewContainer(Context context, AttributeSet attrs) {
                super(context, attrs);
                setup(context);
            }

            public CallOptionsViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                setup(context);
            }

            private void setup(Context context) {
                View view = inflate(context, R.layout.call_options_container_layout, this);
                view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        public CallOptionsView(Context context) {
            super(context);
            setup(context, null);
        }

        @SuppressLint("CustomViewStyleable")
        private void setup(Context context, @Nullable AttributeSet attrs) {
            if (attrs != null) {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CallOptionsViewContainer, 0, 0);
                a.recycle();
            }

            setOrientation(VERTICAL);

            expansionHeader = new CallOptionViewHeader(context);
            addView(expansionHeader);

            callOptionsViewContainer = new CallOptionsViewContainer(context);
            addView(callOptionsViewContainer);

            expansionHeader.setExpansionLayout(callOptionsViewContainer);

            enableOptionClickListeners();
        }

        private void applyCallOptionsPreferences() {
            if (DefaultCallSettingsManager.isWiteboardEnabled(getContext())) setChecked(R.id.call_options_whiteboard, true);
            if (DefaultCallSettingsManager.isFileSharingEnabled(getContext())) setChecked(R.id.call_options_file_share, true);
            if (DefaultCallSettingsManager.isChatEnabled(getContext())) setChecked(R.id.call_options_chat, true);
            if (DefaultCallSettingsManager.isScreenSharingEnabled(getContext())) setChecked(R.id.call_options_screen_sharing, true);
            if (DefaultCallSettingsManager.isCallRecordingEnabled(getContext())) setChecked(R.id.call_options_recording, true);
            if (DefaultCallSettingsManager.isBackCameraAsDefaultEnabled(getContext())) setChecked(R.id.call_options_back_camera, true);
            if (DefaultCallSettingsManager.isProximitySensorDisabled(getContext())) setChecked(R.id.call_options_disable_proximity_sensor, true);
        }

        private void enableOptionClickListeners() {
            CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> {
                if (isChecked && !expansionHeader.titleView.isChecked()) {
                    expansionHeader.titleView.setChecked(true);
                }
            };
            addCheckedChangeListener(R.id.call_options_recording, checkedChangeListener);
            addCheckedChangeListener(R.id.call_options_whiteboard, checkedChangeListener);
            addCheckedChangeListener(R.id.call_options_file_share, checkedChangeListener);
            addCheckedChangeListener(R.id.call_options_screen_sharing, checkedChangeListener);
            addCheckedChangeListener(R.id.call_options_chat, checkedChangeListener);
            addCheckedChangeListener(R.id.call_options_back_camera, checkedChangeListener);
            addCheckedChangeListener(R.id.call_options_disable_proximity_sensor, checkedChangeListener);
        }

        private void addCheckedChangeListener(int id, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
            CheckBox checkBox = callOptionsViewContainer.findViewById(id);
            checkBox.setOnCheckedChangeListener(checkedChangeListener);
        }

        public void selectAllCallCapabilities() {
            selectingProgrammatically = true;
            setChecked(R.id.call_options_whiteboard, true);
            setChecked(R.id.call_options_file_share, true);
            setChecked(R.id.call_options_screen_sharing, true);
            setChecked(R.id.call_options_chat, true);
        }

        public void deselectAllCallCapabilities() {
            setChecked(R.id.call_options_whiteboard, false);
            setChecked(R.id.call_options_file_share, false);
            setChecked(R.id.call_options_screen_sharing, false);
            setChecked(R.id.call_options_chat, false);
        }

        public void selectAllCallOptions() {
            selectingProgrammatically = true;
            setChecked(R.id.call_options_recording, true);
            if (CallOptionsView.this != audioOnlyCallOptions)
                setChecked(R.id.call_options_back_camera, true);
            setChecked(R.id.call_options_disable_proximity_sensor, true);
        }

        public void deselectAllCallOptions() {
            setChecked(R.id.call_options_recording, false);
            setChecked(R.id.call_options_back_camera, false);
            setChecked(R.id.call_options_disable_proximity_sensor, false);
        }

        public boolean isRecordingChecked() {
            return isChecked(R.id.call_options_recording);
        }

        public boolean isWhiteboardChecked() {
            return isChecked(R.id.call_options_whiteboard);
        }

        public boolean isFileShareChecked() {
            return isChecked(R.id.call_options_file_share);
        }

        public boolean isScreenShareChecked() {
            return isChecked(R.id.call_options_screen_sharing);
        }

        public boolean isChatChecked() {
            return isChecked(R.id.call_options_chat);
        }

        public boolean isBackCameraChecked() {
            return isChecked(R.id.call_options_back_camera);
        }

        public boolean isProximitySensorDisabled() { return isChecked(R.id.call_options_disable_proximity_sensor); }

        public boolean isChecked() {
            return expansionHeader.titleView.isChecked();
        }

        public boolean isChecked(int id) {
            CheckBox checkBox = findViewById(id);
            return checkBox.isChecked();
        }

        public void setChecked(int id, boolean checked) {
            CheckBox checkBox = findViewById(id);
            checkBox.setChecked(checked);
            if (!this.callOptionsViewContainer.isExpanded())
                checkBox.jumpDrawablesToCurrentState();
        }
    }
}
