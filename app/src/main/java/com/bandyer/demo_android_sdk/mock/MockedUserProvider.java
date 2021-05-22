/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.mock;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bandyer.android_sdk.client.BandyerSDKClient;
import com.bandyer.android_sdk.utils.provider.OnUserDetailsListener;
import com.bandyer.android_sdk.utils.provider.UserDetails;
import com.bandyer.android_sdk.utils.provider.UserDetailsProvider;
import com.bandyer.app_configuration.external_configuration.model.Configuration;
import com.bandyer.app_configuration.external_configuration.model.UserDetailsProviderMode;
import com.bandyer.app_configuration.external_configuration.utils.MediaStorageUtils;
import com.bandyer.app_utilities.networking.DemoAppUser;
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager;
import com.bandyer.app_utilities.networking.APIInterface;

import java.util.ArrayList;
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
public class MockedUserProvider implements UserDetailsProvider {

    private Context context;

    public MockedUserProvider(Context context) {
        this.context = context;
    }

    @Override
    public void onUserDetailsRequested(@NonNull final List<String> userAliases, @NonNull final OnUserDetailsListener onDetailsListener) {
        // this will be called multiple times, use a cache to avoid excessive work
        // You may also work on another thread and then call the onProviderListener to notify when you have the list of user details ready
        // You have 2 seconds to fetch all the necessary information from your DB/Cache.
        // The rest call following is an example of an asynchronous usage.
        // It is NOT intended to be done here as this method will be called multiple times


        UserDetailsProviderMode userDetailsProviderMode =
                ConfigurationPrefsManager.INSTANCE.getConfiguration(context).getUserDetailsProviderMode();

        if (userDetailsProviderMode == null) return;

        switch (userDetailsProviderMode) {
            case SAMPLE:
                provideSampleUsers(userAliases, onDetailsListener);
                break;
            case CUSTOM:
                provideCustomUserDetails(userAliases, onDetailsListener);
                break;
        }
    }

    private void provideCustomUserDetails(@NonNull final List<String> userAliases, @NonNull final OnUserDetailsListener onDetailsListener) {
        Configuration configuration = ConfigurationPrefsManager.INSTANCE.getConfiguration(context);
        String displayName = configuration.getCustomUserDetailsName();
        String displayImageUrl = configuration.getCustomUserDetailsImageUrl();
        Uri customImage = null;
        if (displayImageUrl != null)
            customImage = MediaStorageUtils.INSTANCE.getUriFromString(configuration.getCustomUserDetailsImageUrl());

        ArrayList<UserDetails> customDetails = new ArrayList<>();

        for (String alias : userAliases) {

            if (alias.equals(BandyerSDKClient.getInstance().getMyAlias()))
                customDetails.add(new UserDetails.Builder(alias).build());
            else {
                UserDetails.Builder userDetailsBuilder = new UserDetails.Builder(alias);
                if (displayName != null) userDetailsBuilder.withNickName(displayName);
                if (customImage != null) userDetailsBuilder.withImageUri(customImage);
                customDetails.add(userDetailsBuilder.build());
            }
        }

        onDetailsListener.provide(customDetails);
    }

    private void provideSampleUsers(@NonNull final List<String> userAliases, @NonNull final OnUserDetailsListener onDetailsListener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://608c623c9f42b20017c3dd9d.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        Call<List<DemoAppUser>> call = retrofit.create(APIInterface.class).getDemoAppUsers();
        call.enqueue(new Callback<List<DemoAppUser>>() {

            @Override
            public void onFailure(@NonNull Call<List<DemoAppUser>> call, @NonNull Throwable t) {
                Log.e("MockedUserProvider", "Error provider of demo app users" + t.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call<List<DemoAppUser>> call, @NonNull Response<List<DemoAppUser>> response) {
                if (response.body() == null) return;

                List<UserDetails> users = new ArrayList<>(userAliases.size());

                for (DemoAppUser user : response.body()) {
                    if (!userAliases.contains(user.getUser_id())) continue;
                    users.add(generateUserDisplayInfo(user));
                }

                onDetailsListener.provide(users);
            }
        });

    }

    private UserDetails generateUserDisplayInfo(DemoAppUser user) {
        UserDetails.Builder builder = new UserDetails.Builder(user.getUser_id());
        if (user.getDisplay_name() != null) builder.withNickName(user.getDisplay_name());
        if (user.getUser_avatar() != null) builder.withImageUrl(user.getUser_avatar());
        return builder.build();
    }
}