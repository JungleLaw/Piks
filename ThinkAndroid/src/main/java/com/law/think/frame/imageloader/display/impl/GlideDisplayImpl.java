package com.law.think.frame.imageloader.display.impl;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.law.think.frame.imageloader.display.Display;
import com.law.think.frame.utils.Logger;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GlideDisplayImpl implements Display {

    @Override
    public void initialization(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void display(Context context, String url, ImageView imageView, ScaleType mScaleType, boolean isGif, int resourceId, float sizeMultiplier, String signature) {
        // TODO Auto-generated method stub
        if (mScaleType != null)
            imageView.setScaleType(mScaleType);
        if (TextUtils.isEmpty(signature))
            signature = url;
        if (isGif) {
            Glide.with(context).load(url).asGif().signature(new StringSignature(signature)).placeholder(resourceId).into(imageView);
        } else {
            Glide.with(context).load(url).asBitmap().signature(new StringSignature(signature)).thumbnail(sizeMultiplier).placeholder(resourceId).into(imageView);
        }
    }

    @Override
    public void clear(Context context) {
        Glide.get(context.getApplicationContext()).clearMemory();
        Glide.get(context.getApplicationContext()).trimMemory(ComponentCallbacks2.TRIM_MEMORY_COMPLETE);
        System.gc();
    }
}
