package com.bandyer.demo_android_sdk.notification;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bandyer.demo_android_sdk.utils.activities.BaseActivity;
import com.bandyer.demo_android_sdk.utils.networking.MockedNetwork;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.bandyer.demo_android_sdk.utils.storage.LoginManager;

import me.pushy.sdk.Pushy;

/**
 * This class show cases a China compatible notification service.
 * For more info see at https://pushy.me/docs
 */
public class PushyCompat extends BroadcastReceiver {

    private static final String TAG = PushyCompat.class.getSimpleName();

    public static void registerDevice(Context context) {
        Pushy.toggleNotifications(true, context);
        if (Pushy.isRegistered(context)) {
            String devicePushToken = Pushy.getDeviceCredentials(context).token;
            MockedNetwork.registerDeviceForPushNotification(context, LoginManager.getLoggedUser(context), devicePushToken);
            return;
        }
        AsyncTask.execute(() -> {
            try {
                String devicePushToken = Pushy.register(context);
                MockedNetwork.registerDeviceForPushNotification(context, LoginManager.getLoggedUser(context), devicePushToken);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public static void unregisterDevice(BaseActivity context, String loggedUser) {
        if (!Pushy.isRegistered(context)) return;
        String devicePushToken = Pushy.getDeviceCredentials(context).token;
        MockedNetwork.unregisterDeviceForPushNotification(context, loggedUser, devicePushToken);
        Pushy.toggleNotifications(false, context);
        // Every unregister will generate a new token, resulting in a new device usage
//      Pushy.unregister(context);
    }

    public static void listen(BaseActivity context) {
        if (!ConfigurationPrefsManager.getPushProvider(context).equals("Pushy") || !Pushy.isRegistered(context))
            return;
        Pushy.listen(context);
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
        // Pushy SDK will be able to persist the device token in the external storage and no new device is registered every time
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String payload = intent.getStringExtra("payload");
        Log.d(TAG, "Pushy payload received: " + payload);

        Data data = new Data.Builder()
                .putString("payload", payload)
                .build();

        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(PushNotificationPayloadWorker.class)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(mRequest);
    }
}