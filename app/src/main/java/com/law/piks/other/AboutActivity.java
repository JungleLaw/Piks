package com.law.piks.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseActivity;
import com.law.piks.splash.IntroActivity;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.inject.annotation.event.OnClick;
import com.law.think.frame.utils.AppUtils;
import com.law.think.frame.widget.TitleBar;

/**
 * Created by Law on 2016/9/18.
 */
public class AboutActivity extends AppBaseActivity {
    @ViewInject(R.id.text_version_name)
    protected TextView mVersionName;
    @ViewInject(R.id.titlebar)
    private TitleBar mTitleBar;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListener() {
        mTitleBar.setOnLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        mVersionName.setText(AppUtils.getAppInfo(this).getVersionName());
        //        mTextLogo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Figa.ttf"));
    }

    @Override
    public void destroyTask() {

    }

    @OnClick({R.id.btn_check_for_update, R.id.btn_intro_page, R.id.btn_thx_for_opensource, R.id.btn_about_me})
    private void startAboutMeActivity(View view) {
        switch (view.getId()) {
            case R.id.btn_check_for_update:
                break;
            case R.id.btn_intro_page:
                IntroActivity.navigateToIntroActivity(this, true);
                break;
            case R.id.btn_thx_for_opensource:
                break;
            case R.id.btn_about_me:
                startActivity(new Intent(this, AboutMeActivity.class));
                break;
            default:
        }
    }
}
