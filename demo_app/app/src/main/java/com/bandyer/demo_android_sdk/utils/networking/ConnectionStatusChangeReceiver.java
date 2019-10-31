package com.bandyer.demo_android_sdk.utils.networking;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * ConnectionStatusChangeReceiver
 * Sends broadcast on connection change.
 */
public class ConnectionStatusChangeReceiver extends BroadcastReceiver {

    private static Boolean mIsConnected = null;

    private static OnNetworkConnectionChanged mOnNetworkConnectionChanged;

    private static boolean connectionStatusReceivedRegistered = false;

    private static ConnectionStatusChangeReceiver connectionStatusChangeReceiver = new ConnectionStatusChangeReceiver();

    public static void register(Context context, OnNetworkConnectionChanged onNetworkConnectionChanged) {
        if (connectionStatusReceivedRegistered) return;
        connectionStatusReceivedRegistered = true;
        mOnNetworkConnectionChanged = onNetworkConnectionChanged;
        context.getApplicationContext().registerReceiver(connectionStatusChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public static void unRegister(Context context) {
        if (!connectionStatusReceivedRegistered) return;
        connectionStatusReceivedRegistered = false;
        mOnNetworkConnectionChanged = null;
        try {
            context.getApplicationContext().unregisterReceiver(connectionStatusChangeReceiver);
        } catch (Throwable ignored) {
            // ignored
        }
    }

    public static boolean isConnected(Context context) {
        if (mIsConnected == null) return isNetworkAvailable(context.getApplicationContext());
        return mIsConnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) return;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        boolean hasInternetConnection = isNetworkAvailable(context);

        if (mIsConnected != null && mIsConnected == hasInternetConnection) return;

        mIsConnected = hasInternetConnection;

        if (isInitialStickyBroadcast()) return;

        mOnNetworkConnectionChanged.onNetworkConnectionChanged(context, hasInternetConnection);

    }

    @SuppressWarnings("deprecation")
    private static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            try {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}

