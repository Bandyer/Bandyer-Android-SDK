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
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Utils for storing files and reading
 * @author kristiyan
 */
object MediaStorageUtils {
    /**
     * Utility method to get an uri from string
     *
     * @param uri uri defined as string
     * @return the correct uri
     */
    fun getUriFromString(uri: String?): Uri? {
        if (uri == null) return null
        return if (uri.startsWith("content") || uri.startsWith("android.resource") || uri.startsWith("file") || uri.startsWith("http")) Uri.parse(uri) else Uri.fromFile(File(uri))
    }

    /**
     * Store an image
     * @param selectedImage the image to store
     * @return the path where it was stored
     */
    fun saveFileInApp(context: Context, selectedImage: Uri?, name: String): String? {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = context.contentResolver.openInputStream(selectedImage!!)
            val filePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/Bandyer/" + name
            val file = File(filePath)
            if (file.exists()) file.delete()
            file.parentFile?.mkdirs()
            file.createNewFile()
            out = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`!!.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            return file.absolutePath
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                `in`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
}