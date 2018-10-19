/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils;

import com.facebook.stetho.inspector.network.NetworkEventReporter;
import com.facebook.stetho.inspector.network.NetworkEventReporterImpl;
import com.facebook.stetho.inspector.network.SimpleTextInspectorWebSocketFrame;

/**
 * StethoReporter for networking activity
 * Usefull for debug via chrome-console
 * <p>
 * For more information visit:
 * https://github.com/facebook/stetho
 */
public class StethoReporter {

    private final NetworkEventReporter mReporter = NetworkEventReporterImpl.get();
    private String mRequestId;

    public void onCreated(String host) {
        mRequestId = mReporter.nextRequestId();
        mReporter.webSocketCreated(mRequestId, host);
    }

    public void onError(String e) {
        mReporter.webSocketFrameError(mRequestId, e);
    }

    public void onClosed() {
        mReporter.webSocketClosed(mRequestId);
    }

    public void onSend(String message) {
        mReporter.webSocketFrameSent(new SimpleTextInspectorWebSocketFrame(mRequestId, message));
    }

    public void onReceive(String message) {
        mReporter.webSocketFrameReceived(new SimpleTextInspectorWebSocketFrame(mRequestId, message));
    }
}
