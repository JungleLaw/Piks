package com.law.piks.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.law.piks.app.Constants;
import com.law.think.frame.prefs.AnyPref;
import com.law.think.frame.utils.AppUtils;

/**
 * Created by Law on 2016/9/24.
 */

public class DispatchActivity extends Activity {
    private int versioncode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        versioncode = AnyPref.getDefault().getInt(Constants.SharedPrefrenced.VERSION_CODE, 0);
        if (AppUtils.getAppInfo(this).getVersionCode() > versioncode) {
            IntroActivity.navigateToIntroActivity(this, false);
        } else {
            startActivity(new Intent(this, SplashActivity.class));
        }
        finish();
    }
}
