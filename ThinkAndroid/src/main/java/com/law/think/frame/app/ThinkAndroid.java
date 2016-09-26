package com.law.think.frame.app;

import com.law.think.frame.confs.AppConf;
import com.law.think.frame.core.ThinkCore;
import com.law.think.frame.db.ThinkDb;

import android.app.Application;

public class ThinkAndroid extends Application {
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        AppConf.Config.conf(this);
        AppConf.Config.setDebug(true);
        ThinkCore.getInstance().initCore(this);
        // ThinkDb.initialize(this);
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        // ThinkDb.terminate();
    }
}
