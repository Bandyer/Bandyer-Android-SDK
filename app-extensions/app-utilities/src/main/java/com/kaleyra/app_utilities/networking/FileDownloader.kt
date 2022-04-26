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

package com.kaleyra.app_utilities.networking

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment.DIRECTORY_DOWNLOADS

/**
 *
 * @author kristiyan
 */
object FileDownloader {

    fun download(context: Context, url: String, fileName: String, fileType: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName)
        else request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, fileName)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // to notify when download is complete
        request.setMimeType(fileType)
        request.allowScanningByMediaScanner() // if you want to be available from media players
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        manager!!.enqueue(request)
    }

    fun download(context: Context, url: String, destinationFileUri: Uri, fileType: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationUri(destinationFileUri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // to notify when download is complete
        request.setMimeType(fileType)
        request.allowScanningByMediaScanner() // if you want to be available from media players
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        manager!!.enqueue(request)
    }
}