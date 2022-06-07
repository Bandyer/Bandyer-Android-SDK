package com.bandyer.demo_android_sdk.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bandyer.android_sdk.client.BandyerSDK;
import com.bandyer.demo_android_sdk.MainActivity;
import com.bandyer.demo_android_sdk.R.drawable;
import com.bandyer.demo_android_sdk.R.string;

import org.json.JSONArray;
import org.json.JSONObject;


import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MissedNotificationPayloadWorker extends Worker {

    private static final String event = "on_missed_call";
    public static final String startCall = "startCall";
    public static final String startChat = "startChat";
    public static final String notificationId = "notificationId";

    static boolean isMissingCallMessage(String payload) throws org.json.JSONException {
        JSONObject webhookPayload = new JSONObject(payload);
        return webhookPayload.getString("event").equals(MissedNotificationPayloadWorker.event);
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }

    private static final String TAG = MissedNotificationPayloadWorker.class.getSimpleName();

    public MissedNotificationPayloadWorker(@NonNull Context context,
                                           @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            String payload = getInputData().getString("payload");
            if (payload == null) return Result.failure();
            Log.d(TAG, "Received payload\n" + payload + "\nready to be processed.");
            JSONObject missedCall = new JSONObject(payload);
            String caller = missedCall.getJSONObject("data").getString("caller_id");
            java.util.ArrayList<String> callbackUsers = callbackUsers(missedCall);
            int notificationId = payload.hashCode();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getNotificationChannel())
                    .setSmallIcon(drawable.ic_missed_call)
                    .setContentTitle(caller)
                    .setContentText(getApplicationContext().getString(string.missed_call))
                    .setAutoCancel(true)
                    .setContentIntent(openMainActivity())
                    .addAction(drawable.ic_kaleyra_audio_call, getApplicationContext().getString(string.callback), callBack(callbackUsers, notificationId))
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            if (callbackUsers.size() == 1)
                builder.addAction(drawable.ic_kaleyra_chat, getApplicationContext().getString(string.chatback), chatBack(caller, notificationId));
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(notificationId, builder.build());
        } catch (Throwable e) {
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }

    private java.util.ArrayList<String> callbackUsers(JSONObject missedCall) throws org.json.JSONException {
        String sessionUser = BandyerSDK.getInstance().getSession().getUserId();
        String caller = missedCall.getJSONObject("data").getString("caller_id");
        JSONArray callees = missedCall.getJSONObject("data").getJSONArray("called_users");
        java.util.ArrayList<String> callbackUsers = new java.util.ArrayList();
        callbackUsers.add(caller);
        for (int i = 0; i < callees.length(); i++) {
            String userId = callees.getJSONObject(i).getString("user_id");
            if (!sessionUser.equals(userId)) callbackUsers.add(userId);
        }
        return callbackUsers;
    }

    private PendingIntent chatBack(String user, int notification) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(startChat, user);
        intent.putExtra(notificationId, notification);
        int flags = android.app.PendingIntent.FLAG_ONE_SHOT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) flags = flags | android.app.PendingIntent.FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT;
        return PendingIntent.getActivity(getApplicationContext(), 1, intent, flags);
    }

    private PendingIntent callBack(java.util.ArrayList<String> callees, int notification) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(startCall, callees);
        intent.putExtra(notificationId, notification);
        int flags = android.app.PendingIntent.FLAG_ONE_SHOT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) flags = flags | android.app.PendingIntent.FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT;
        return PendingIntent.getActivity(getApplicationContext(), 2, intent, flags);
    }

    private PendingIntent openMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        int flags = android.app.PendingIntent.FLAG_ONE_SHOT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) flags = flags | android.app.PendingIntent.FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT;
        return PendingIntent.getActivity(getApplicationContext(), 3, intent, flags);
    }

    private String getNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return event;
        NotificationChannel channel = new NotificationChannel(event, event, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(event);
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        return event;
    }
}