package com.bandyer.demo_android_sdk.utils.networking;

import android.content.Context;

public interface OnNetworkConnectionChanged {
    void onNetworkConnectionChanged(Context context, boolean connected);
}
