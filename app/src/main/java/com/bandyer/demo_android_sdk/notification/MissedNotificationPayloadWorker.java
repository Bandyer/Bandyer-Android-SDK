package com.bandyer.demo_android_sdk.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bandyer.android_sdk.client.BandyerSDK;
import com.bandyer.android_sdk.client.Completion;
import com.bandyer.android_sdk.client.Session;
import com.bandyer.android_sdk.module.BandyerComponent;
import com.bandyer.android_sdk.notification.FormatContext;
import com.bandyer.android_sdk.utils.provider.UserDetails;
import com.bandyer.android_sdk.utils.provider.UserDetailsFormatter;
import com.bandyer.android_sdk.utils.provider.UserDetailsProvider;
import com.bandyer.demo_android_sdk.MainActivity;
import com.bandyer.demo_android_sdk.R.drawable;
import com.bandyer.demo_android_sdk.R.string;
import com.bandyer.demo_android_sdk.ui.utils.UserDetailsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


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
            Session session = BandyerSDK.getInstance().getSession();
            if (payload == null || session == null) return Result.failure();
            Log.d(TAG, "Received payload\n" + payload + "\nready to be processed.");
            JSONObject missedCall = new JSONObject(payload);
            ArrayList<String> userDetailsRequest = new ArrayList<>();
            String caller = missedCall.getJSONObject("data").getString("caller_id");
            userDetailsRequest.add(caller);
            ArrayList<String> callbackUsers = callbackUsers(session, missedCall);
            getUserDetailsProvider().onUserDetailsRequested(userDetailsRequest, new Completion<Iterable<UserDetails>>() {

                @Override
                public void success(Iterable<UserDetails> data) {
                    UserDetails userDetails = null;
                    for (UserDetails datum : data) {
                        userDetails = datum;
                    }
                    if (userDetails == null) userDetails = new UserDetails.Builder(caller).build();
                    showMissedCallNotification(userDetails, payload.hashCode(), callbackUsers);
                }

                @Override
                public void error(@NonNull Throwable error) {
                    onUserDetailsCompletionError();
                }

                @Override
                public void error(@NonNull Exception error) {
                    onUserDetailsCompletionError();
                }

                private void onUserDetailsCompletionError() {
                    showMissedCallNotification(new UserDetails.Builder(caller).build(), payload.hashCode(), callbackUsers);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }

    private void showMissedCallNotification(UserDetails userDetails, Integer notificationId, ArrayList<String> callbackUsers) {
        UserDetailsUtils.getUserImageBitmap(userDetails, new Completion<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                onBitmapLoaded(data);
            }

            @Override
            public void error(@NonNull Exception error) {
                onBitmapLoaded(UserDetailsUtils.getFallbackUserBitmapIcon());
            }

            @Override
            public void error(@NonNull Throwable error) {
                onBitmapLoaded(UserDetailsUtils.getFallbackUserBitmapIcon());
            }

            private void onBitmapLoaded(Bitmap image) {
                String callerDisplayName = getUserDetailsFormatter().format(userDetails, new FormatContext(BandyerComponent.CallComponent.INSTANCE, true));
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getNotificationChannel())
                        .setSmallIcon(drawable.ic_missed_call)
                        .setContentTitle(callerDisplayName)
                        .setContentText(getApplicationContext().getString(string.missed_call))
                        .setAutoCancel(true)
                        .setLargeIcon(image)
                        .setContentIntent(openMainActivity())
                        .addAction(drawable.ic_kaleyra_audio_call, getApplicationContext().getString(string.callback), callBack(callbackUsers, notificationId))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                if (callbackUsers.size() == 1)
                    builder.addAction(drawable.ic_kaleyra_chat, getApplicationContext().getString(string.chatback), chatBack(callbackUsers.get(0), notificationId));
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(notificationId, builder.build());
            }
        });
    }

    private ArrayList<String> callbackUsers(Session session, JSONObject missedCall) throws org.json.JSONException {
        String sessionUser = session.getUserId();
        String caller = missedCall.getJSONObject("data").getString("caller_id");
        JSONArray callees = missedCall.getJSONObject("data").getJSONArray("called_users");
        ArrayList<String> callbackUsers = new ArrayList<String>();
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

    private PendingIntent callBack(ArrayList<String> callees, int notification) {
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

    private UserDetailsProvider getUserDetailsProvider() {
        UserDetailsProvider sdkUserDetailsProvider = BandyerSDK.getInstance().getUserDetailsProvider();
        return (sdkUserDetailsProvider != null) ? sdkUserDetailsProvider : (userAliases, completion) -> {
            ArrayList<UserDetails> userDetails = new ArrayList<>();
            for (String userAlias : userAliases) {
                userDetails.add(new UserDetails.Builder(userAlias).build());
            }
            completion.success(userDetails);
        };
    }

    private UserDetailsFormatter getUserDetailsFormatter() {
        UserDetailsFormatter sdkUserDetailsFormatter = BandyerSDK.getInstance().getUserDetailsFormatter();
        return (sdkUserDetailsFormatter != null) ? sdkUserDetailsFormatter : (userDetails, context) -> userDetails.getUserAlias();
    }
}