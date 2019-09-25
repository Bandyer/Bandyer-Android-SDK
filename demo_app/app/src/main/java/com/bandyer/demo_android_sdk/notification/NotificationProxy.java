package com.bandyer.demo_android_sdk.notification;

import android.content.Context;

import com.bandyer.demo_android_sdk.utils.activities.BaseActivity;
import com.bandyer.demo_android_sdk.utils.storage.ConfigurationPrefsManager;
import com.bandyer.demo_android_sdk.utils.storage.LoginManager;

/**
 * This class is a proxy to the different notification services we want to show case
 * For the moment we implemented the FCM ( standard ) which works everywhere except for China.
 * For China we show case the Pushy library.
 */
public class NotificationProxy {

    public static final String FCM_PROVIDER = "FCM";
    public static final String PUSHY_PROVIDER = "Pushy";

    public static void registerDevice(Context context) {
        if (!LoginManager.isUserLogged(context)) return;
        String pushProvider = ConfigurationPrefsManager.getPushProvider(context);
        switch (pushProvider) {
            case FCM_PROVIDER:
                FirebaseCompat.registerDevice(context);
                break;
            case PUSHY_PROVIDER:
                PushyCompat.registerDevice(context);
                break;
        }
    }

    public static void unregisterDevice(BaseActivity context, String loggedUser) {
        String pushProvider = ConfigurationPrefsManager.getPushProvider(context);
        switch (pushProvider) {
            case FCM_PROVIDER:
                FirebaseCompat.unregisterDevice(context, loggedUser);
                break;
            case PUSHY_PROVIDER:
                PushyCompat.unregisterDevice(context, loggedUser);
                break;
        }
    }

    public static void listen(BaseActivity context) {
        String pushProvider = ConfigurationPrefsManager.getPushProvider(context);
        switch (pushProvider) {
            case PUSHY_PROVIDER:
                PushyCompat.listen(context);
                break;
        }
    }

}
