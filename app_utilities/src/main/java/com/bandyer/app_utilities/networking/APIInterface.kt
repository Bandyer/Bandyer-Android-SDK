/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.networking

import retrofit2.Call
import retrofit2.http.*

/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box,
 * with the least efforts.
 *
 *
 * Defines Rest calls available to be used
 */
interface APIInterface {
    /**
     * Method returns the mocked users that have been created by us or you via server side rest calls.
     *
     * @return BandyerUsers
     */
    @get:GET("rest/user/list")
    val users: Call<BandyerUsers?>?

    /**
     * Method fakes a fetch to a remote server for users
     *
     * @return DemoAppUsers
     */
    @GET("/user_details/client")
    fun getDemoAppUsers(): Call<List<DemoAppUser>>

    /**
     * Register device for push notifications
     */
    @POST("/mobile_push_notifications/rest/device")
    fun registerDeviceForPushNotifications(@Body deviceRegistrationInfo: DeviceRegistrationInfo?): Call<Void?>?

    /**
     * Unregister device for push notifications
     */
    @DELETE("/mobile_push_notifications/rest/device/{userAlias}/{appId}/{pushToken}")
    fun unregisterDeviceForPushNotifications(@Path("userAlias") userAlias: String?,
                                             @Path("appId") appId: String?,
                                             @Path("pushToken") pushToken: String?): Call<Void?>?
}