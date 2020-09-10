/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
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
import com.bandyer.app_configuration.external_configuration.model.CustomUserDetailsProvider;
import com.bandyer.app_configuration.external_configuration.model.Configuration;
import com.bandyer.app_configuration.external_configuration.utils.MediaStorageUtils;
import com.bandyer.app_utilities.storage.ConfigurationPrefsManager;
import com.bandyer.app_utilities.utils.Utils;
import com.bandyer.app_utilities.networking.APIInterface;
import com.bandyer.app_utilities.networking.DemoAppUsers;

import java.util.ArrayList;
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


        CustomUserDetailsProvider customUserDetailsProvider =
                ConfigurationPrefsManager.INSTANCE.getConfiguration(context).getCustomUserDetailsProvider();

        if (customUserDetailsProvider == null) return;

        switch (customUserDetailsProvider) {
            case RANDOM:
                provideRandomUserDetails(userAliases, onDetailsListener);
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

    private void provideRandomUserDetails(@NonNull final List<String> userAliases, @NonNull final OnUserDetailsListener onDetailsListener) {
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
                        onDetailsListener.provide(userDetailsMap.values());
                }
            });
        }
    }

    private UserDetails generateUserDisplayInfo(String userAlias, DemoAppUsers.DemoAppUser user) {
        return new UserDetails.Builder(userAlias)
                .withFirstName(Utils.capitalize(user.name.first))
                .withLastName(Utils.capitalize(user.name.last))
                .withImageUrl(user.picture.large)
                .withEmail(user.email)
                .build();
    }
}