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
package com.kaleyra.app_configuration.utils

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

/**
 *
 * @author kristiyan
 */
object WifiConnector {

    fun getNetworkSSID(data: Uri): String? {
        val allKeys = data.schemeSpecificPart.split(";")
        return allKeys.firstOrNull { it.startsWith("S:") }?.split(":")?.getOrNull(1)
    }

    fun connectToAP(context: Context, data: Uri) {
        val allKeys = data.schemeSpecificPart.split(";")
        val securityMode = allKeys.firstOrNull { it.startsWith("T:") }?.split(":")?.getOrNull(1)
                ?: "OPEN"
        val networkSSID = getNetworkSSID(
                data
        ) ?: return
        val networkPasskey = allKeys.firstOrNull { it.startsWith("P:") }?.split(":")?.getOrNull(1)
        connectToAP(
                context,
                networkSSID,
                networkPasskey,
                securityMode
        )
    }

    fun connectToAP(context: Context, networkSSID: String, networkPasskey: String?, securityMode: String): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiConfiguration =
                createAPConfiguration(
                        networkSSID,
                        networkPasskey,
                        securityMode
                )
        val res: Int = wifiManager.addNetwork(wifiConfiguration)
        wifiManager.enableNetwork(res, true)
        wifiManager.isWifiEnabled = true
        val changeHappen: Boolean = wifiManager.saveConfiguration()
        return res != -1 && changeHappen
    }

    private fun createAPConfiguration(networkSSID: String, networkPasskey: String?, securityMode: String): WifiConfiguration? {
        val wifiConfiguration = WifiConfiguration()
        wifiConfiguration.SSID = "\"" + networkSSID + "\""
        when {
            securityMode.equals("OPEN", ignoreCase = true) -> {
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
            securityMode.equals("WEP", ignoreCase = true) -> {
                wifiConfiguration.wepKeys[0] = "\"" + networkPasskey + "\""
                wifiConfiguration.wepTxKeyIndex = 0
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            }
            securityMode.equals("WPA", ignoreCase = true) -> {
                wifiConfiguration.preSharedKey = "\"" + networkPasskey + "\""
                wifiConfiguration.hiddenSSID = true
                wifiConfiguration.status = WifiConfiguration.Status.ENABLED
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            }
            else -> {
                return null
            }
        }
        return wifiConfiguration
    }
}