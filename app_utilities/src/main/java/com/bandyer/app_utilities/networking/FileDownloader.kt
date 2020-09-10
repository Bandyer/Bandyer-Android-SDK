package com.bandyer.app_utilities.networking

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