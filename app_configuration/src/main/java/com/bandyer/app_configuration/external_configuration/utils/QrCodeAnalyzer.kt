/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */

package com.bandyer.app_configuration.external_configuration.utils

import android.graphics.ImageFormat.*
import android.media.Image
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor

@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun Image.toByteArray(): ByteArray? {
    val nv21: ByteArray
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer[nv21, 0, ySize]
    vBuffer[nv21, ySize, vSize]
    uBuffer[nv21, ySize + vSize, uSize]
    return nv21
}

class QrCodeAnalyzer(private val onQrCodesDetected: (qrCode: Result) -> Unit
) : FrameProcessor {

    private val yuvFormats = mutableListOf(YUV_420_888)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            yuvFormats.addAll(listOf(YUV_422_888, YUV_444_888))
        }
    }

    private val reader = MultiFormatReader().apply {
        val map = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
        )
        setHints(map)
    }


    override fun process(frame: Frame) {
        val width =  frame.size.width
        val height =  frame.size.height
        if (frame.dataClass === ByteArray::class.java) {
            val data: ByteArray = frame.getData()

            val source = PlanarYUVLuminanceSource(
                data,
                width,
                height,
                0,
                0,
                width,
                height,
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                // Whenever reader fails to detect a QR code in image
                // it throws NotFoundException
                val result = reader.decode(binaryBitmap)
                onQrCodesDetected(result)
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
            // Process byte array...
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && frame.dataClass === Image::class.java) {
            val image: Image = frame.getData()
            val source = PlanarYUVLuminanceSource(
                image.toByteArray(),
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                // Whenever reader fails to detect a QR code in image
                // it throws NotFoundException
                val result = reader.decode(binaryBitmap)
                onQrCodesDetected(result)
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
        }


    }
}