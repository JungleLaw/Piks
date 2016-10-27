package com.law.piks.app;

import android.app.Application;

import com.alibaba.fastjson.JSON;
import com.law.piks.medias.Configuration;
import com.law.piks.others.update.VersionInfo;
import com.law.think.frame.app.ThinkAndroid;
import com.law.think.frame.confs.AppConf;
import com.law.think.frame.prefs.AnyPref;
import com.law.think.frame.utils.AppUtils;
import com.law.think.frame.utils.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;
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
        FIR.init(this);
        CrashReport.initCrashReport(getApplicationContext(), "900056786", false);
        checkForUpdate();
    }

    private void checkForUpdate() {
        new Thread() {
            public void run() {
                FIR.checkForUpdateInFIR(Constants.FIR.FIR_TOKEN, new VersionCheckCallback() {
                    @Override
                    public void onSuccess(String versionJson) {
                        try {
                            VersionInfo mVersionInfo = JSON.parseObject(versionJson, VersionInfo.class);
                            //                Logger.json("fir", versionJson);
                            Logger.i(mVersionInfo.toString());
                            if (AppUtils.getAppInfo(mInstance).getVersionCode() < Integer.parseInt(mVersionInfo.getVersionCode())) {
                                AnyPref.getDefault().getEditor().putBoolean(Constants.SharedPrefrenced.HAS_NEW_VERSION, true).commit();
                            } else {
                                AnyPref.getDefault().getEditor().putBoolean(Constants.SharedPrefrenced.HAS_NEW_VERSION, false).commit();
                            }
                            Logger.i(AnyPref.getDefault().getBoolean(Constants.SharedPrefrenced.HAS_NEW_VERSION, false));
                        } catch (Exception e) {
                            Logger.e("fir", "fail");
                        }
                    }

                    @Override
                    public void onFail(Exception exception) {
                        Logger.e("fir", "onFail" + "\n" + exception.getMessage());
                    }

                    @Override
                    public void onStart() {
                        Logger.i("fir", "onStart");
                    }

                    @Override
                    public void onFinish() {
                        Logger.i("fir", "onFinish");
                    }
                });
            }
        }.start();
    }

}
