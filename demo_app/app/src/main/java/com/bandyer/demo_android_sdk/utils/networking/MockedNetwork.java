/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.networking;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.bandyer.demo_android_sdk.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box.
 * With the least efforts.
 * <p>
 * MockedNetwork
 *
 * @author kristiyan
 */
public class MockedNetwork {

    private static Call<BandyerUsers> call;

    private static GetBandyerUsersCallback networkCallback;

    public interface GetBandyerUsersCallback {

        void onUsers(List<String> users);

        void onError(String error);
    }

    public static void getSampleUsers(Activity context, GetBandyerUsersCallback callback) {
        cancel();
        networkCallback = callback;
        String apikey = context.getString(R.string.api_key);
        call = APIClient.getClient(apikey).create(APIInterface.class).getUsers();
        call.enqueue(new Callback<BandyerUsers>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<BandyerUsers> call, @NonNull Response<BandyerUsers> response) {
                if (networkCallback == null) return;
                if (response.body() == null || response.body().user_id_list == null) return;
                networkCallback.onUsers(response.body().user_id_list);
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<BandyerUsers> call, @NonNull Throwable t) {
                if (networkCallback == null) return;
                networkCallback.onError(t.getMessage());
            }
        });
    }

    public static void cancel() {
        networkCallback = null;
        if (call == null) return;
        if (!call.isExecuted()) call.cancel();
        call = null;
    }
}
