/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.mock;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bandyer.android_sdk.utils.provider.OnUserInformationProviderListener;
import com.bandyer.android_sdk.utils.provider.UserContactProvider;
import com.bandyer.android_sdk.utils.provider.UserDetails;
import com.bandyer.demo_android_sdk.utils.networking.APIInterface;
import com.bandyer.demo_android_sdk.utils.networking.DemoAppUsers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Implementation of a UserContactProvider interface, used from the SDK to retrieve user details.
 * Results can be fetched from your data sources synchronously or asynchronously.
 */
public class MockedUserProvider implements UserContactProvider {

    @Override
    public void provideUserDetails(@NonNull final List<String> userAliases, @NonNull final OnUserInformationProviderListener<UserDetails> onProviderListener) {
        // this will be called multiple times, use a cache to avoid excessive work
        // You may also work on another thread and then call the onProviderListener to notify when you have the list of user details ready
        // You have 2 seconds to fetch all the necessary information from your DB/Cache.
        // The rest call following is an example of an asynchronous usage.
        // It is NOT intended to be done here as this method will be called multiple times

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();


        final HashMap<String, UserDetails> userDetailsMap = new LinkedHashMap<>(userAliases.size());
        for (String userAlias : userAliases) userDetailsMap.put(userAlias, null);

        for (String userAlias : userAliases) {

            Call<DemoAppUsers> call = retrofit.create(APIInterface.class).getDemoAppUsers(userAlias);
            call.enqueue(new Callback<DemoAppUsers>() {

                @Override
                public void onFailure(@NonNull Call<DemoAppUsers> call, @NonNull Throwable t) {
                    Log.e("MockedUserProvider", "Error provider of demo app users" + t.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NonNull Call<DemoAppUsers> call, @NonNull Response<DemoAppUsers> response) {
                    if (response.body() == null) return;

                    DemoAppUsers.DemoAppUser user = response.body().results.get(0);
                    String userAlias = response.body().info.userAlias;

                    userDetailsMap.put(userAlias, generateUserDisplayInfo(userAlias, user));

                    if (!userDetailsMap.values().contains(null))
                        onProviderListener.onProvided(userDetailsMap.values());
                }
            });
        }
    }

    private UserDetails generateUserDisplayInfo(String userAlias, DemoAppUsers.DemoAppUser user) {
        return new UserDetails.Builder(userAlias)
                .withFirstName(user.name.first)
                .withLastName(user.name.last)
                .withImageUrl(user.picture.large)
                .withEmail(user.email)
                .build();
    }
}