/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.networking;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box,
 * with the least efforts.
 * <p>
 * MockedNetwork
 *
 * @author kristiyan
 */
public class MockedNetwork {

    private static Call<BandyerUsers> call;

    private static Call<Void> registerDevice;
    private static Call<Void> unregisterDevice;

    private static GetBandyerUsersCallback networkCallback;

    public interface GetBandyerUsersCallback {

        void onUsers(List<String> users);

        void onError(String error);
    }

    public static void getSampleUsers(Activity context, GetBandyerUsersCallback callback) {
        cancelGetUsers();
        networkCallback = callback;
        String apiKey = ConfigurationPrefsManager.getApiKey(context);
        String envName = ConfigurationPrefsManager.getEnvironmentName(context);

        call = APIClient.getClient(apiKey, envName).create(APIInterface.class).getUsers();
        call.enqueue(new Callback<BandyerUsers>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<BandyerUsers> call, @NonNull Response<BandyerUsers> response) {
                if (networkCallback == null) return;
                if (response.body() == null || response.body().user_id_list == null || !response.isSuccessful()) {
                    networkCallback.onError("No users found or credentials are invalid!");
                    return;
                }
                networkCallback.onUsers(response.body().user_id_list);
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<BandyerUsers> call, @NonNull Throwable t) {
                if (networkCallback == null) return;
                networkCallback.onError(t.getMessage());
            }
        });
    }

    public static void registerDeviceForPushNotification(Context context, String userAlias, String devicePushToken) {
        cancelRegisterUser();
        String appId = ConfigurationPrefsManager.getAppId(context);
        String apiKey = ConfigurationPrefsManager.getApiKey(context);
        String envName = ConfigurationPrefsManager.getEnvironmentName(context);
        String pushProvider = ConfigurationPrefsManager.getPushProvider(context);

        DeviceRegistrationInfo info = new DeviceRegistrationInfo(userAlias, appId, devicePushToken, pushProvider);
        registerDevice = APIClient.getClient(apiKey, envName).create(APIInterface.class).registerDeviceForPushNotifications(info);

        registerDevice.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("PushNotification", "Failed to register device for push notifications!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("PushNotification", "Failed to register device for push notifications!");
            }
        });
    }

    public static void unregisterDeviceForPushNotification(Context context, String userAlias, String devicePushToken) {
        cancelUnRegisterUser();
        String appId = ConfigurationPrefsManager.getAppId(context);
        String apiKey = ConfigurationPrefsManager.getApiKey(context);
        String envName = ConfigurationPrefsManager.getEnvironmentName(context);

        unregisterDevice = APIClient.getClient(apiKey, envName).create(APIInterface.class).unregisterDeviceForPushNotifications(userAlias, appId, devicePushToken);

        unregisterDevice.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("PushNotification", "Failed to unregister device for push notifications!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("PushNotification", "Failed to unregister device for push notifications!");
            }
        });
    }

    private static void cancelGetUsers() {
        if (call == null) return;
        if (!call.isExecuted()) call.cancel();
        call = null;
        networkCallback = null;
    }

    private static void cancelUnRegisterUser() {
        if (unregisterDevice == null) return;
        if (!unregisterDevice.isExecuted()) unregisterDevice.cancel();
        unregisterDevice = null;
    }

    private static void cancelRegisterUser() {
        if (registerDevice == null) return;
        if (!registerDevice.isExecuted()) registerDevice.cancel();
        registerDevice = null;
    }

    public static void cancel() {
        cancelGetUsers();
        cancelRegisterUser();
        cancelUnRegisterUser();
    }
}
