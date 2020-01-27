package com.bandyer.demo_android_sdk.settings;

import androidx.annotation.NonNull;

/**
 * @author kristiyan
 */
@SuppressWarnings("WeakerAccess")
public class Configuration {

    String environment;
    String userAlias;
    String appId;
    String apiKey;
    String projectNumber;
    String pushProvider;
    String logoUrl = "";
    String logoName = "";
    boolean useLeakCanary;
    boolean useMockUserDetailsProvider;
    boolean useSimplifiedVersion;
    String defaultCallType;
    boolean withWhiteboardCapability;
    boolean withFileSharingCapability;
    boolean withChatCapability;
    boolean withScreenSharingCapability;
    boolean withRecordingEnabled;
    boolean withBackCameraAsDefault;
    boolean withProximityEnabled;

    @NonNull
    @Override
    public String toString() {
        return "Configuration{" +
                "environment='" + environment + '\'' +
                ", userAlias='" + userAlias + '\'' +
                ", appId='" + appId + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", projectNumber='" + projectNumber + '\'' +
                ", pushProvider='" + pushProvider + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", logoName='" + logoName + '\'' +
                ", useMockUserDetailsProvider=" + useMockUserDetailsProvider +
                ", useLeakCanary=" + useLeakCanary +
                ", useSimplifiedVersion=" + useSimplifiedVersion +
                ", defaultCallType='" + defaultCallType + '\'' +
                ", withWhiteboardCapability=" + withWhiteboardCapability +
                ", withFileSharingCapability=" + withFileSharingCapability +
                ", withChatCapability=" + withChatCapability +
                ", withScreenSharingCapability=" + withScreenSharingCapability +
                ", withRecordingEnabled=" + withRecordingEnabled +
                ", withBackCameraAsDefault=" + withBackCameraAsDefault +
                ", withProximityEnabled=" + withProximityEnabled +
                '}';
    }
}