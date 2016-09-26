package com.law.piks.splash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.law.piks.R;
import com.law.piks.app.Constants;
import com.law.piks.app.base.AppBaseActivity;
import com.law.piks.home.HomeActivity;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.prefs.AnyPref;
import com.law.think.frame.utils.AppUtils;
import com.law.think.frame.utils.ConstUtils;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.utils.SDKUtils;

import me.wangyuwei.particleview.ParticleView;

public class SplashActivity extends AppBaseActivity {
    @ViewInject(R.id.pv_splash)
    private ParticleView mParticleView;

    private Thread mThread = new Thread(new Runnable() {
        long MIN_DURATION = ConstUtils.SEC * 1;
        boolean interrupted = false;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(MIN_DURATION);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                Logger.e("InterruptedException");
                interrupted = true;
            }
            if (!interrupted) {
                Intent mIntent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(mIntent);
                finish();
            }
        }
    });

    @Override
    public int setContentViewLayout() {
        // TODO Auto-generated method stub
        return R.layout.activity_splash;
    }

    @Override
    public void initVariables() {
        // TODO Auto-generated method stub

    }

    @Override
    public void initViews() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initListener() {
        // TODO Auto-generated method stub
        mParticleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                mThread.start();
            }
        });
    }

    @Override
    public void loadData(Bundle savedInstanceState) {
        mParticleView.startAnim();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                //                | View.SYSTEM_UI_FLAG_FULLSCREEN
                ;
        findViewById(R.id.layout_root).setSystemUiVisibility(uiOptions);
        if (SDKUtils.hasLollopop()) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            //            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.primary_dark));
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void destroyTask() {
        // TODO Auto-generated method stub
        mThread.interrupt();
    }

    @Override
    public int[] loadEntryAnimation() {
        return null;
    }

    @Override
    public int[] loadExitAnimation() {
        return null;
    }
}
