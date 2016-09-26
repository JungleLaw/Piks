package com.law.think.frame.imageloader;

import com.law.think.frame.imageloader.display.Display;
import com.law.think.frame.imageloader.display.impl.GlideDisplayImpl;
import com.law.think.frame.utils.Logger;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import retrofit2.http.Url;

public class ImageLoader {

    private static Display imageDisplayer;

    public static ImageLoaderBuilder with(Context context) {
        if (imageDisplayer == null) {
            imageDisplayer = new GlideDisplayImpl();
        }
        return ImageLoaderBuilder.getInstance(context);
    }

    private static void displayImage(Context context, String url, ImageView imageView, ScaleType mScaleType, boolean isGif, int resourceId, float sizeMultiplier, String signature) {
        if (imageDisplayer == null) {
            imageDisplayer = new GlideDisplayImpl();
        }
        imageDisplayer.display(context, url, imageView, mScaleType, isGif, resourceId, sizeMultiplier, signature);
    }

    public static void init(Context context, Display display) {
        display.initialization(context);
        imageDisplayer = display;
    }

    public static void clear(Context context) {
        if (imageDisplayer != null)
            imageDisplayer.clear(context);
    }

    public static class ImageLoaderBuilder {

        private ScaleType scaleType;
        private boolean isGif = false;
        private String url;
        private Context mContext;
        private int resId;
        private float sizeMultiplier = 1f;
        private String signature;

        private ImageLoaderBuilder(Context mContext) {
            this.mContext = mContext;
        }

        public static ImageLoaderBuilder getInstance(Context mContext) {
            return new ImageLoaderBuilder(mContext);
        }

        public ImageLoaderBuilder fitCenter() {
            this.scaleType = ScaleType.FIT_CENTER;
            return this;
        }

        public ImageLoaderBuilder fitXY() {
            this.scaleType = ScaleType.FIT_XY;
            return this;
        }

        public ImageLoaderBuilder matrix() {
            this.scaleType = ScaleType.MATRIX;
            return this;
        }

        public ImageLoaderBuilder centerCrop() {
            this.scaleType = ScaleType.CENTER_CROP;
            return this;
        }

        public ImageLoaderBuilder centerInside() {
            // TODO Auto-generated method stub
            this.scaleType = ScaleType.CENTER_INSIDE;
            return this;
        }

        public ImageLoaderBuilder center() {
            // TODO Auto-generated method stub
            this.scaleType = ScaleType.CENTER;
            return this;
        }

        public ImageLoaderBuilder asGif() {
            // TODO Auto-generated method stub
            this.isGif = true;
            return this;
        }

        public ImageLoaderBuilder load(String url) {
            this.url = url;
            return this;
        }

        public ImageLoaderBuilder placeholder(int resouceId) {
            this.resId = resouceId;
            return this;
        }

        public ImageLoaderBuilder thumbnail(float thumbnail) {
            this.sizeMultiplier = thumbnail;
            return this;
        }

        public ImageLoaderBuilder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public void into(ImageView target) {
            displayImage(mContext, url, target, scaleType, isGif, resId, sizeMultiplier, signature);
        }
    }
}
