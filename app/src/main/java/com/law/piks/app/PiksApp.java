package com.law.piks.app;

import android.app.Application;

import com.law.piks.medias.Configuration;
import com.law.piks.medias.engine.MediasLoadEngine;
import com.law.think.frame.app.ThinkAndroid;
import com.law.think.frame.confs.AppConf;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Law on 2016/9/7.
 */
public class PiksApp extends ThinkAndroid {
    private static Application mInstance;

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppConf.Config.setDebug(true);
        mInstance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/San_Francisco_Display_Thin.ttf").setFontAttrId(uk.co.chrisjenx.calligraphy.R.attr.fontPath).build());
        Configuration.initGalleryConstants(this);
    }
}
