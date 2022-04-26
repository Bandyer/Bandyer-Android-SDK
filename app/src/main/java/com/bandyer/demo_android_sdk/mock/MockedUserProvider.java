/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.mock;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bandyer.android_sdk.client.BandyerSDK;
import com.bandyer.android_sdk.client.Completion;
import com.bandyer.android_sdk.utils.provider.UserDetails;
import com.bandyer.android_sdk.utils.provider.UserDetailsProvider;
import com.kaleyra.app_configuration.model.Configuration;
import com.kaleyra.app_configuration.utils.MediaStorageUtils;
import com.kaleyra.app_utilities.MultiDexApplication;
import com.kaleyra.app_utilities.networking.DemoAppUser;
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;


/**
 * Implementation of a UserContactProvider interface, used from the SDK to retrieve user details.
 * Results can be fetched from your data sources synchronously or asynchronously.
 */
public class MockedUserProvider implements UserDetailsProvider {

    private Context context;
    private OkHttpClient client = new OkHttpClient();
    private String TAG = "MockedUserProvider";

    public MockedUserProvider(Context context) {
        this.context = context;
    }

    @Override
    public void onUserDetailsRequested(@NonNull final List<String> userAliases, @NonNull final Completion<Iterable<UserDetails>> completion) {
        // this will be called multiple times, use a cache to avoid excessive work
        // You may also work on another thread and then call the onProviderListener to notify when you have the list of user details ready
        // You have 2 seconds to fetch all the necessary information from your DB/Cache.
        // The rest call following is an example of an asynchronous usage.
        // It is NOT intended to be done here as this method will be called multiple times

        com.kaleyra.app_configuration.model.UserDetailsProviderMode userDetailsProviderMode =
                ConfigurationPrefsManager.INSTANCE.getConfiguration(context).getUserDetailsProviderMode();

        if (userDetailsProviderMode == null) return;

        switch (userDetailsProviderMode) {
            case SAMPLE:
                provideSampleUsers(userAliases, completion);
                break;
            case CUSTOM:
                provideCustomUserDetails(userAliases, completion);
                break;
        }
    }

    private void provideCustomUserDetails(@NonNull final List<String> userAliases, @NonNull final Completion<Iterable<UserDetails>> completion) {
        Configuration configuration = ConfigurationPrefsManager.INSTANCE.getConfiguration(context);
        String displayName = configuration.getCustomUserDetailsName();
        String displayImageUrl = configuration.getCustomUserDetailsImageUrl();
        Uri customImage = null;
        if (displayImageUrl != null)
            customImage = MediaStorageUtils.INSTANCE.getUriFromString(configuration.getCustomUserDetailsImageUrl());

        ArrayList<UserDetails> customDetails = new ArrayList<>();

        for (String alias : userAliases) {

            if (alias.equals(Objects.requireNonNull(BandyerSDK.getInstance().getSession()).getUserId()))
                customDetails.add(new UserDetails.Builder(alias).build());
            else {
                UserDetails.Builder userDetailsBuilder = new UserDetails.Builder(alias);
                if (displayName != null) userDetailsBuilder.withNickName(displayName);
                if (customImage != null) userDetailsBuilder.withImageUri(customImage);
                customDetails.add(userDetailsBuilder.build());
            }
        }

        completion.success(customDetails);
    }

    private void provideSampleUsers(@NonNull final List<String> userAliases, @NonNull final Completion<Iterable<UserDetails>> completion) {
        MultiDexApplication.getRestApi().getSampleUsers(userAliases, demoAppUsers -> {
            ArrayList<UserDetails> userDetails = new ArrayList<>();
            for (DemoAppUser demoAppUser : demoAppUsers) userDetails.add(generateUserDisplayInfo(demoAppUser));
            completion.success(userDetails);
            return null;
        }, throwable -> {
            Log.e(TAG, "Unable to fetch sample users " + throwable.getMessage());
            return null;
        });
    }

    private UserDetails generateUserDisplayInfo(DemoAppUser user) {
        UserDetails.Builder builder = new UserDetails.Builder(user.getUser_id());
        if (user.getDisplay_name() != null) builder.withNickName(user.getDisplay_name());
        if (user.getUser_avatar() != null) builder.withImageUrl(user.getUser_avatar());
        return builder.build();
    }
}