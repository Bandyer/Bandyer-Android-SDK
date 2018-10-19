/*
 * Copyright (C) 2018 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.demo_android_sdk.utils.networking;

import android.content.Context;
import retrofit2.Call;
import retrofit2.Callback;
import com.bandyer.demo_android_sdk.R;

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

    public static void getSampleUsers(Context context, Callback<BandyerUsers> callback) {
        String apikey = context.getString(R.string.api_key);
        Call<BandyerUsers> call = APIClient.getClient(apikey).create(APIInterface.class).getUsers();
        call.enqueue(callback);
    }
}
