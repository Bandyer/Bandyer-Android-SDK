/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_configuration.external_configuration.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.bandyer.app_configuration.R
import kotlinx.android.parcel.Parcelize

/**
 * @author kristiyan
 */
@Parcelize
data class Configuration (
    var environment: String? = null,
    var userAlias: String? = null,
    var appId: String? = null,
    var apiKey: String? = null,
    var projectNumber: String? = null,
    var pushProvider: PushProvider = PushProvider.NONE,
    var logoUrl: String? = null,
    var logoName: String? = null,
    var customUserDetailsName: String? = null,
    var customUserDetailsImageUrl: String? = null,
    var userDetailsProviderMode: UserDetailsProviderMode = UserDetailsProviderMode.NONE,
    var useLeakCanary: Boolean = false,
    var useSimplifiedVersion: Boolean = false,
    var defaultCallType: CallOptionsType = CallOptionsType.AUDIO_VIDEO,
    var withWhiteboardCapability: Boolean = false,
    var withFileSharingCapability: Boolean = false,
    var withChatCapability: Boolean = false,
    var withScreenSharingCapability: Boolean = false,
    var withRecordingEnabled: Boolean = false,
    var withBackCameraAsDefault: Boolean = false,
    var withProximitySensorDisabled: Boolean = false,
    var withFeedbackEnabled: Boolean = false,
    var withMockAuthentication: Boolean = false,
    var firebaseProjectId: String? = null,
    var firebaseMobileAppId: String? = null,
    var firebaseApiKey: String? = null,
    var hmsAppId: String? = null,
    var skipCustomization: Boolean = useSimplifiedVersion
) : Parcelable {


    override fun toString(): String {
        return "Configuration(environment=$environment, userAlias=$userAlias, appId=$appId, apiKey=$apiKey, projectNumber=$projectNumber, pushProvider=$pushProvider, logoUrl=$logoUrl, logoName=$logoName, customUserDetailsName=$customUserDetailsName, customUserDetailsImageUrl=$customUserDetailsImageUrl, useLeakCanary=$useLeakCanary, useSimplifiedVersion=$useSimplifiedVersion, defaultCallType=$defaultCallType, withWhiteboardCapability=$withWhiteboardCapability, withFileSharingCapability=$withFileSharingCapability, withChatCapability=$withChatCapability, withScreenSharingCapability=$withScreenSharingCapability, withRecordingEnabled=$withRecordingEnabled, withBackCameraAsDefault=$withBackCameraAsDefault, withProximityEnabled=$withProximitySensorDisabled, withMockAuthentication=$withMockAuthentication, firebaseProjectId=$firebaseProjectId, firebaseMobileAppId=$firebaseMobileAppId, firebaseApiKey=$firebaseApiKey, hmsAppId=$hmsAppId, customizeConfiguration=$skipCustomization)"
    }

    fun isMockConfiguration(): Boolean {
        return appId == "mAppId_xxx" || apiKey == "ak_xxx" || appId == null || apiKey == null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Configuration

        if (environment != other.environment) return false
        if (userAlias != other.userAlias) return false
        if (appId != other.appId) return false
        if (apiKey != other.apiKey) return false
        if (projectNumber != other.projectNumber) return false
        if (pushProvider != other.pushProvider) return false
        if (logoUrl != other.logoUrl) return false
        if (logoName != other.logoName) return false
        if (customUserDetailsName != other.customUserDetailsName) return false
        if (customUserDetailsImageUrl != other.customUserDetailsImageUrl) return false
        if (userDetailsProviderMode != other.userDetailsProviderMode) return false
        if (useLeakCanary != other.useLeakCanary) return false
        if (useSimplifiedVersion != other.useSimplifiedVersion) return false
        if (defaultCallType != other.defaultCallType) return false
        if (withWhiteboardCapability != other.withWhiteboardCapability) return false
        if (withFileSharingCapability != other.withFileSharingCapability) return false
        if (withChatCapability != other.withChatCapability) return false
        if (withScreenSharingCapability != other.withScreenSharingCapability) return false
        if (withRecordingEnabled != other.withRecordingEnabled) return false
        if (withBackCameraAsDefault != other.withBackCameraAsDefault) return false
        if (withProximitySensorDisabled != other.withProximitySensorDisabled) return false
        if (withMockAuthentication != other.withMockAuthentication) return false
        if (firebaseProjectId != other.firebaseProjectId) return false
        if (firebaseMobileAppId != other.firebaseMobileAppId) return false
        if (firebaseApiKey != other.firebaseApiKey) return false
        if (hmsAppId != other.hmsAppId) return false
        if (skipCustomization != other.skipCustomization) return false
        if (withFeedbackEnabled != other.withFeedbackEnabled) return false

        return true
    }

    override fun hashCode(): Int {
        var result = environment?.hashCode() ?: 0
        result = 31 * result + (userAlias?.hashCode() ?: 0)
        result = 31 * result + (appId?.hashCode() ?: 0)
        result = 31 * result + (apiKey?.hashCode() ?: 0)
        result = 31 * result + (projectNumber?.hashCode() ?: 0)
        result = 31 * result + (pushProvider.hashCode() ?: 0)
        result = 31 * result + (logoUrl?.hashCode() ?: 0)
        result = 31 * result + (logoName?.hashCode() ?: 0)
        result = 31 * result + (customUserDetailsName?.hashCode() ?: 0)
        result = 31 * result + (customUserDetailsImageUrl?.hashCode() ?: 0)
        result = 31 * result + (userDetailsProviderMode.hashCode())
        result = 31 * result + useLeakCanary.hashCode()
        result = 31 * result + useSimplifiedVersion.hashCode()
        result = 31 * result + defaultCallType.hashCode()
        result = 31 * result + withWhiteboardCapability.hashCode()
        result = 31 * result + withFileSharingCapability.hashCode()
        result = 31 * result + withChatCapability.hashCode()
        result = 31 * result + withScreenSharingCapability.hashCode()
        result = 31 * result + withRecordingEnabled.hashCode()
        result = 31 * result + withBackCameraAsDefault.hashCode()
        result = 31 * result + withProximitySensorDisabled.hashCode()
        result = 31 * result + withFeedbackEnabled.hashCode()
        result = 31 * result + withMockAuthentication.hashCode()
        result = 31 * result + (firebaseProjectId?.hashCode() ?: 0)
        result = 31 * result + (firebaseMobileAppId?.hashCode() ?: 0)
        result = 31 * result + (firebaseApiKey?.hashCode() ?: 0)
        result = 31 * result + (hmsAppId?.hashCode() ?: 0)
        result = 31 * result + (skipCustomization.hashCode() ?: 0)
        return result
    }

    fun deepClone(): Configuration {
        var parcel: Parcel? = null
        return try {
            parcel = Parcel.obtain()
            parcel.writeParcelable(this, 0)
            parcel.setDataPosition(0)
            parcel.readParcelable(this.javaClass.classLoader!!)!!
        } finally {
            parcel?.recycle()
        }
    }
}

fun getMockConfiguration(context: Context): Configuration {
    return Configuration().apply {
        with(context.resources) {
            environment = getString(R.string.environment)
            apiKey = getString(R.string.api_key)
            appId = getString(R.string.app_id)
            pushProvider = PushProvider.valueOf(getString(R.string.push_provider))
            projectNumber = getString(R.string.project_number)
            firebaseProjectId = getString(R.string.firebase_project_id)
            firebaseMobileAppId = getString(R.string.firebase_mobile_app_id)
            firebaseApiKey = getString(R.string.firebase_api_key)
            hmsAppId = getString(R.string.hms_app_id)
            logoUrl = "android.resource://" + context.packageName + "/drawable/logo";
        }
    }
}