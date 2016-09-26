package com.law.think.frame.imageloader.display;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public interface Display {

    public abstract void initialization(Context context);

    public abstract void display(Context context, String url, ImageView imageView, ScaleType mScaleType, boolean isGif, int resourceId, float sizeMultiplier, String signature);

    public abstract void clear(Context context);

    // public abstract void display(Context context, String url, ImageView
    // imageView, ScaleType mScaleType);

}
