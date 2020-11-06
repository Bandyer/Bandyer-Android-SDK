/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.networking

import java.util.*

/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box,
 * with the least efforts.
 *
 *
 * Model used to map the users coming from the mocked network call
 *
 * @author kristiyan
 */
class BandyerUsers {
    @JvmField
    var user_id_list: ArrayList<String>? = null
}