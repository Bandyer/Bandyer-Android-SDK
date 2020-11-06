/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.networking

/**
 * @author kristiyan
 */
class DemoAppUsers {
    @JvmField
    var results: List<DemoAppUser>? = null
    @JvmField
    var info: DemoUserInfo? = null

    inner class DemoAppUser {
        @JvmField
        var name: Name? = null
        @JvmField
        var email: String? = null
        @JvmField
        var picture: Picture? = null

        inner class Name {
            @JvmField
            var first: String? = null
            @JvmField
            var last: String? = null
        }

        inner class Picture {
            @JvmField
            var large: String? = null
        }
    }
}