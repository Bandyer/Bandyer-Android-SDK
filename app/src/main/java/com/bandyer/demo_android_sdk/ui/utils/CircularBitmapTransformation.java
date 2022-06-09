package com.bandyer.demo_android_sdk.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class CircularBitmapTransformation implements Transformation {

    private int backgroundColor = Color.TRANSPARENT;

    public Bitmap transform(Bitmap source, int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return transform(source);
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source)
            source.recycle();

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);

        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, backgroundPaint);
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "CircularBitmapTransformation";
    }
}