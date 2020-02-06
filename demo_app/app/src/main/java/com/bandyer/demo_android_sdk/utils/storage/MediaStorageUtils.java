package com.bandyer.demo_android_sdk.utils.storage;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utils for storing files and reading
 * @author kristiyan
 */
public class MediaStorageUtils {

    /**
     * Utility method to get an uri from string
     *
     * @param uri uri defined as string
     * @return the correct uri
     */
    public static Uri getUriFromString(String uri) {
        if(uri == null) return null;
        return uri.startsWith("content") || uri.startsWith("android.resource") || uri.startsWith("file") || uri.startsWith("http") ? Uri.parse(uri) : Uri.fromFile(new File(uri));
    }

    /**
     * Store an image
     * @param selectedImage the image to store
     * @return the path where it was stored
     */
    public static String saveFileInApp(Context context, Uri selectedImage, String name) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getContentResolver().openInputStream(selectedImage);
            String filePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Bandyer/" + name;
            File file = new File(filePath);
            if (file.exists()) file.delete();
            file.getParentFile().mkdirs();
            file.createNewFile();
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return file.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
