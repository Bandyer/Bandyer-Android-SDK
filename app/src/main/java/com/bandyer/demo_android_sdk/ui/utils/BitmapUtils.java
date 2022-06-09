package com.bandyer.demo_android_sdk.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bandyer.android_sdk.client.Completion;
import com.kaleyra.collaboration_suite_utils.ContextRetainer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileDescriptor;
import java.io.IOException;

public class BitmapUtils {

    public static Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = ContextRetainer.Companion.getContext().getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return new CircularBitmapTransformation().transform(image);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static void urlToBitmap(String url, Completion<Bitmap> completion) {
        Picasso.get().load(url).transform(new CircularBitmapTransformation()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                completion.success(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                completion.error(e);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable)drawable).getBitmap();

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap vectorDrawableToBitmap(int vectorRes, int backgroundColor) {
        Drawable drawable = VectorDrawableCompat.create(ContextRetainer.Companion.getContext().getResources(), vectorRes, null);
        Bitmap bitmap = BitmapUtils.drawableToBitmap(drawable);
        return new CircularBitmapTransformation().transform(bitmap, backgroundColor);
    }
}
