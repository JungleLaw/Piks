package com.law.piks.splash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.law.piks.R;
import com.law.piks.app.Constants;
import com.law.piks.app.base.AppBaseActivity;
import com.law.piks.home.HomeActivity;
import com.law.think.frame.prefs.AnyPref;
import com.law.think.frame.utils.AppUtils;
import com.law.think.frame.utils.SDKUtils;
import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;

import java.util.ArrayList;

/**
 * Created by Law on 2016/9/19.
 */
public class IntroActivity extends AppBaseActivity {
    private static final String CHECK_MODE = "check";
    private boolean isCheck = false;

    public static void navigateToIntroActivity(Context context, boolean isCheck) {
        Intent mIntent = new Intent(context, IntroActivity.class);
        mIntent.putExtra(CHECK_MODE, isCheck);
        context.startActivity(mIntent);
    }

    @Override
    public int setContentViewLayout() {
        return R.layout.onboarding_main_layout;
    }

    @Override
    public void initVariables() {
        isCheck = getIntent().getBooleanExtra(CHECK_MODE, false);
    }

    @Override
    public void initViews() {
    }

    @Override
    public void initListener() {

    }

    @Override
    public int[] loadEntryAnimation() {
        if (isCheck)
            return super.loadEntryAnimation();
        return null;
    }

    @Override
    public int[] loadExitAnimation() {
        if (isCheck)
            return null;
        return super.loadExitAnimation();
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());
        engine.setOnEntryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheck) {
                    AnyPref.getDefault().putInt(Constants.SharedPrefrenced.VERSION_CODE, AppUtils.getAppInfo(IntroActivity.this).getVersionCode());
                    startActivity(new Intent(IntroActivity.this, HomeActivity.class));
                }
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                //                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        //                View.SYSTEM_UI_FLAG_FULLSCREEN;
        findViewById(R.id.onboardingRootView).setSystemUiVisibility(uiOptions);
        if (SDKUtils.hasLollopop()) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void destroyTask() {

    }

    // Just example data for Onboarding
    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage("欢迎使用", "Piks", Color.parseColor("#678FB4"), R.drawable.drawable_intro_1, R.drawable.drawable_intro_indicator_1);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("简洁不失个性", "Customization", Color.parseColor("#65B0B4"), R.drawable.drawable_intro_2, R.drawable.drawable_intro_indicator_2);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("轻巧不失优雅", "Elegance", Color.parseColor("#9B90BC"), R.drawable.drawable_intro_3, R.drawable.drawable_intro_indicator_3);
        PaperOnboardingPage scr4 = new PaperOnboardingPage("纯粹", "Purely", Color.parseColor("#E35754"), R.drawable.drawable_intro_4, R.drawable.drawable_intro_indicator_4, true);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr4);
        return elements;
    }
}
