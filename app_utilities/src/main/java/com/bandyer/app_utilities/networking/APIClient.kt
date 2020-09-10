/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.networking

import com.bandyer.app_utilities.utils.Utils.getEnvironmentByName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box,
 * with the least effort.
 *
 *
 * RetroFit ApiClient used to make the rest calls
 */
internal object APIClient {
    private var retrofit: Retrofit? = null
    private var client: OkHttpClient? = null
    @JvmStatic
    fun getClient(apikey: String, environmentName: String?): Retrofit? {
        if (retrofit != null) return retrofit
        if (client == null) client = OkHttpClient.Builder()
                .addInterceptor(authenticationHeaders(apikey))
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        val env = getEnvironmentByName(environmentName)
        retrofit = Retrofit.Builder()
                .baseUrl(env.url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        return retrofit
    }

    private fun authenticationHeaders(apikey: String): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val request = original.newBuilder()
                    .header("apikey", apikey)
                    .method(original.method(), original.body())
                    .build()
            chain.proceed(request)
        }
    }
}