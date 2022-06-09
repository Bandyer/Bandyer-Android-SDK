package com.bandyer.demo_android_sdk.ui.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bandyer.android_sdk.client.Completion;
import com.bandyer.android_sdk.utils.provider.UserDetails;
import com.bandyer.demo_android_sdk.R;
import com.kaleyra.collaboration_suite_utils.ContextRetainer;

public class UserDetailsUtils {

    public static void getUserImageBitmap(UserDetails userDetails, Completion<Bitmap> completion) {
        if (userDetails.getImageUri() != null)
            completion.success(BitmapUtils.uriToBitmap(userDetails.getImageUri()));
        else if (userDetails.getImageUrl() != null) {
            BitmapUtils.urlToBitmap(userDetails.getImageUrl(), new Completion<Bitmap>() {
                @Override
                public void success(Bitmap data) {
                    completion.success(data);
                }

                @Override
                public void error(@NonNull Exception error) {
                    completion.success(getFallbackUserBitmapIcon());
                }

                @Override
                public void error(@NonNull Throwable error) {
                    completion.success(getFallbackUserBitmapIcon());
                }
            });
        } else if (userDetails.getImageResId() != null) {
            try {
                Bitmap imageResBitmap = BitmapUtils.drawableToBitmap(ContextCompat.getDrawable(ContextRetainer.Companion.getContext(), userDetails.getImageResId()));
                completion.success(new CircularBitmapTransformation().transform(imageResBitmap));
            } catch (Exception e) {
                try {
                    Bitmap imageResBitmap = BitmapUtils.vectorDrawableToBitmap(userDetails.getImageResId(), Color.TRANSPARENT);
                    completion.success(new CircularBitmapTransformation().transform(imageResBitmap));
                } catch (Exception e2) {
                    completion.success(getFallbackUserBitmapIcon());
                }
            }
        } else completion.success(getFallbackUserBitmapIcon());
    }

    public static Bitmap getFallbackUserBitmapIcon() {
        return BitmapUtils.vectorDrawableToBitmap(R.drawable.kaleyra_z_user_1, Color.LTGRAY);
    }
}
