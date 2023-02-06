/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaleyra.app_configuration.model

import android.content.Context
import com.kaleyra.app_configuration.BuildConfig
import com.kaleyra.app_configuration.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @author kristiyan
 */
@Serializable
data class Configuration(
    var appId: String,
    var apiKey: String,
    var region: String = "eu",
    var environment: String,
    var userId: String? = null,
    var projectNumber: String? = null,
    var pushProvider: PushProvider = PushProvider.NONE,
    var logoUrl: String? = null,
    var logoName: String? = null,
    var customUserDetailsName: String? = null,
    var customUserDetailsImageUrl: String? = null,
    var userDetailsProviderMode: UserDetailsProviderMode = UserDetailsProviderMode.REMOTE,
    var useLeakCanary: Boolean = BuildConfig.USE_LEAK_CANARY,
    var defaultCallType: CallOptionsType = CallOptionsType.AUDIO_VIDEO,
    var withMockAuthentication: Boolean = false,
    var firebaseProjectId: String? = null,
    var firebaseMobileAppId: String? = null,
    var firebaseApiKey: String? = null,
    var hmsAppId: String? = null): java.io.Serializable {

    fun toJson(): String = json.encodeToString(this)

    companion object {

        private val json: Json by lazy {
            Json {
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
                coerceInputValues = true
            }
        }

        fun create(encoded: String): Configuration = json.decodeFromString(encoded)
    }

    fun isMockConfiguration(): Boolean = appId == "mAppId_xxx" || apiKey == "ak_xxx" || appId.isBlank() || apiKey.isBlank()
}

fun getMockConfiguration(context: Context): Configuration {
    return Configuration(
        region = context.getString(R.string.region),
        environment = context.getString(R.string.environment),
        apiKey = context.getString(R.string.api_key),
        appId = context.getString(R.string.app_id)
    ).apply {
        with(context) {
            getString(R.string.push_provider).takeIf { it.isNotBlank() }?.let { pushProvider = PushProvider.valueOf(it) }
            projectNumber = getString(R.string.project_number)
            firebaseProjectId = getString(R.string.firebase_project_id)
            firebaseMobileAppId = getString(R.string.firebase_mobile_app_id)
            firebaseApiKey = getString(R.string.firebase_api_key)
            hmsAppId = getString(R.string.hms_app_id)
            logoUrl = "android.resource://" + context.packageName + "/drawable/logo";
        }
    }
}