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

/**
 * Mocked user provider mode
 */
enum class UserDetailsProviderMode {

    /**
     * Display name and image coming from server side user details provider will be used to display user.
     */
    REMOTE,

    /**
     * A custom display name and image will be user for ALL users.
     */
    CUSTOM
}