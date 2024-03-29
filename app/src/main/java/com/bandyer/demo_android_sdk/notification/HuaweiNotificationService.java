/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.notification;

import static com.bandyer.demo_android_sdk.notification.MissedNotificationPayloadWorker.isMissingCallMessage;
import android.os.Bundle;
import android.util.Log;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.kaleyra.app_utilities.notification.HuaweiCompat;


public class HuaweiNotificationService extends HmsMessageService {

    private static final String TAG = HuaweiNotificationService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            String payload = remoteMessage.getData();
            Log.d(TAG, "payload received: " + payload);

            Data data = new Data.Builder()
                    .putString("payload", payload)
                    .build();

            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(isMissingCallMessage(payload) ? MissedNotificationPayloadWorker.class : PushNotificationPayloadWorker.class)
                    .setInputData(data)
                    .build();

            WorkManager.getInstance(getApplicationContext()).enqueue(mRequest);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        HuaweiCompat.registerDevice(this);
    }

    @Override
    public void onNewToken(String s, Bundle bundle) {
        super.onNewToken(s, bundle);
        HuaweiCompat.registerDevice(this);
    }
}
