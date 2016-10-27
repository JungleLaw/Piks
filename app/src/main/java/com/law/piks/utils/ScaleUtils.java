package com.law.piks.utils;

import com.law.think.frame.utils.Logger;

/**
 * Created by Law on 2016/10/25.
 */

public class ScaleUtils {
    public static int[] getScaleSize(int width, int height, int panelWidth, int panelHeight) {
        int w, h;
        if (width > height || (panelHeight > panelWidth * height / width)) {
            w = panelWidth;
            h = height * panelWidth / width;
        } else {
            h = panelHeight;
            w = width * panelHeight / height;
        }
        int[] sizes = {w, h};
        return sizes;
    }
}
