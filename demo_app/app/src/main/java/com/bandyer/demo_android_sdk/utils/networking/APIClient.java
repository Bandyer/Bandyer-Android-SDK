/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.networking;

import com.bandyer.android_sdk.Environment;
import com.bandyer.demo_android_sdk.App;
import com.bandyer.demo_android_sdk.utils.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box,
 * with the least effort.
 * <p>
 * RetroFit ApiClient used to make the rest calls
 */
class APIClient {

    private static Retrofit retrofit = null;

    static Retrofit getClient(final String apikey, final String environmentName) {
        if (retrofit != null) return retrofit;

        OkHttpClient client = App.client.newBuilder()
                .addInterceptor(authenticationHeaders(apikey))
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build();

        Environment env = Utils.getEnvironmentByName(environmentName);

        retrofit = new Retrofit.Builder()
                .baseUrl(env.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    private static Interceptor authenticationHeaders(final String apikey) {
        return chain -> {

            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("apikey", apikey)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };
    }

}