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
package com.kaleyra.app_configuration.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import com.kaleyra.app_configuration.R
import com.kaleyra.app_configuration.databinding.ActivityQrconfigurationBinding
import com.kaleyra.app_configuration.ui.ViewFinderView
import com.kaleyra.app_configuration.utils.QrCodeAnalyzer
import com.kaleyra.app_configuration.utils.WifiConnector
import java.util.concurrent.Executors

/**
 *
 * @author kristiyan
 */
open class QRConfigurationActivity : BaseConfigurationActivity(false) {

    private lateinit var binding: ActivityQrconfigurationBinding

    companion object {
        const val WIFI_CONFIGURATION_RESULT = "WIFI_CONFIGURATION_RESULT"
        const val WIFI_CONFIGURATION_SSID_RESULT = "WIFI_CONFIGURATION_SSID_RESULT"
        const val TAG = "QRConfigurationActivity"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private var wifiConnectedToast: Toast? = null
    private val configuredNetworks = mutableListOf<String>()

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private fun configureWifi(uri: Uri) {
        WifiConnector.connectToAP(applicationContext, uri)
        val networkSsid = WifiConnector.getNetworkSSID(uri)
        sendBroadcastWifiConfigurationResult(
                CONFIGURATION_ACTION_UPDATE,
                networkSsid,
                true
        )
        wifiConnectedToast?.cancel()
        wifiConnectedToast = Toast.makeText(
                applicationContext,
                "${resources.getString(R.string.wifi_connected)} \"$networkSsid\"",
                Toast.LENGTH_LONG
        )
        wifiConnectedToast!!.show()
        configuredNetworks.add(uri.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrconfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.insertManuallyButton.setOnClickListener {
            stopScanQR()
        }

        binding.qrView.setLifecycleOwner(this)
        binding.qrView.addFrameProcessor(QrCodeAnalyzer {
            val uri = if (it.text != null) Uri.parse(it.text) else Uri.EMPTY
            if (configuredNetworks.contains(uri.toString())) return@QrCodeAnalyzer
            if (uri.scheme == "WIFI") configureWifi(uri)
            else configureFromUri(uri)
        })
        (binding.qrView.parent as ViewGroup).addView(ViewFinderView(this), 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    private fun stopScanQR() {
        ConfigurationActivity.show(this, currentConfiguration, false)
    }

    private fun sendBroadcastWifiConfigurationResult(
            action: String, wifiSsid: String?, isWifiConfigured: Boolean
    ) {
        val resultIntent = Intent()
        resultIntent.`package` = this.packageName
        resultIntent.action = action
        resultIntent.putExtra(WIFI_CONFIGURATION_RESULT, isWifiConfigured)
        resultIntent.putExtra(WIFI_CONFIGURATION_SSID_RESULT, wifiSsid)
        resultIntent.action = action
        sendBroadcast(resultIntent)
    }
}