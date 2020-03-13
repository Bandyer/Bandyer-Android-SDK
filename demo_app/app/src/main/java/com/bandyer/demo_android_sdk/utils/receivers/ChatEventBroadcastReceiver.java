/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.receivers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bandyer.android_sdk.chat.ChatException;


/**
 * A broadcast receiver that receive broadcast when a chat starts, ends or ends with error.
 */
public class ChatEventBroadcastReceiver extends com.bandyer.android_sdk.chat.notification.ChatEventBroadcastReceiver {

    private static final String TAG = "CHAT EVENT";

    @Override
    public void onChatStarted() {
        Log.d(TAG, "Chat started.");
    }

    @Override
    public void onChatEnded() {
        Log.d(TAG, "Chat ended.");
    }

    @Override
    public void onChatEndedWithError(@NonNull ChatException chatException) {
        Log.d(TAG, "Chat ended with error: " + chatException.getMessage());
    }
}

