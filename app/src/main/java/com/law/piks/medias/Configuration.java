package com.law.piks.medias;

import android.content.Context;

import com.law.think.frame.utils.ScreenUtils;

/**
 * Created by Law on 2016/9/9.
 */
public class Configuration {
    public static void initGalleryConstants(Context context) {
        GalleryConstants.screenWidth = ScreenUtils.getScreenWidth(context);
        GalleryConstants.screenHeight = ScreenUtils.getScreenHeight(context);
        if (GalleryConstants.screenWidth < 720) {
            GalleryConstants.numColumns = 2;
        } else if (GalleryConstants.screenWidth >= 720 && GalleryConstants.screenWidth <= 1080) {
            GalleryConstants.numColumns = 3;
        } else {
            GalleryConstants.numColumns = 4;
        }
        for (int i = 1; ; i++) {
            if ((GalleryConstants.screenWidth - (GalleryConstants.numColumns - 1) * i) % GalleryConstants.numColumns == 0) {
                GalleryConstants.lenght = (GalleryConstants.screenWidth - (GalleryConstants.numColumns - 1) * i) / GalleryConstants.numColumns;
                GalleryConstants.divider = i;
                break;
            }
        }
        GalleryConstants.albumHeight = (int) (GalleryConstants.lenght * 1.25f);
    }

    public static class GalleryConstants {
        public static int screenWidth;
        public static int screenHeight;
        public static int numColumns;
        public static int lenght;
        public static int divider;
        public static int albumHeight;
    }
}
