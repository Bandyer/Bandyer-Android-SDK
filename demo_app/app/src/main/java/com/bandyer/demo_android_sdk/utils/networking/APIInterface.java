/*
 * Copyright (C) 2019 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.networking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box,
 * with the least efforts.
 * <p>
 * Defines Rest calls available to be used
 */
public interface APIInterface {

    /**
     * Method returns the mocked users that have been created by us or you via server side rest calls.
     *
     * @return BandyerUsers
     */
    @GET("rest/user/list")
    Call<BandyerUsers> getUsers();


    /**
     * Method fakes a fetch to a remote server for users
     *
     * @param seed get the same result based on the value of this seed
     * @return DemoAppUsers
     */
    @GET("/api/?inc=name,email,picture")
    Call<DemoAppUsers> getDemoAppUsers(@Query("seed") String seed);


    /**
     * Register device for push notifications
     */
    @POST("/mobile_push_notifications/rest/device")
    Call<Void> registerDeviceForPushNotifications(@Body DeviceRegistrationInfo deviceRegistrationInfo);


    /**
     * Unregister device for push notifications
     */
    @DELETE("/mobile_push_notifications/rest/device/{userAlias}/{appId}/{pushToken}")
    Call<Void> unregisterDeviceForPushNotifications(@Path("userAlias") String userAlias,
                                                    @Path("appId") String appId,
                                                    @Path("pushToken") String pushToken);
}