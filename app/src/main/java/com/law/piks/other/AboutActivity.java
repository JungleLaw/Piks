package com.law.piks.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.law.piks.R;
import com.law.piks.app.Constants;
import com.law.piks.app.base.AppBaseActivity;
import com.law.piks.other.update.VersionInfo;
import com.law.piks.splash.IntroActivity;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.inject.annotation.event.OnClick;
import com.law.think.frame.prefs.AnyPref;
import com.law.think.frame.utils.AppUtils;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.widget.ThinkToast;
import com.law.think.frame.widget.TitleBar;

import org.w3c.dom.Text;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

/**
 * Created by Law on 2016/9/18.
 */
public class AboutActivity extends AppBaseActivity {
    @ViewInject(R.id.text_version_name)
    protected TextView mVersionName;
    @ViewInject(R.id.titlebar)
    private TitleBar mTitleBar;
    @ViewInject(R.id.img_new_version_flag)
    private ImageView mImgNewVersionFlag;

    private AlertDialog mUpdateDialog;
    private TextView mTextUpdateInfo;
    private Button mBtnUpdate;

    private VersionInfo mVersionInfo;

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
        if (AnyPref.getDefault().getBoolean(Constants.SharedPrefrenced.HAS_NEW_VERSION, false)) {
            mImgNewVersionFlag.setVisibility(View.VISIBLE);
        } else {
            mImgNewVersionFlag.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void destroyTask() {

    }

    @OnClick({R.id.btn_check_for_update, R.id.btn_intro_page, R.id.btn_thx_for_opensource, R.id.btn_about_me})
    private void startAboutMeActivity(View view) {
        switch (view.getId()) {
            case R.id.btn_check_for_update:
                checkForUpdate();
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

    private void checkForUpdate() {
        FIR.checkForUpdateInFIR(Constants.FIR.FIR_TOKEN, new VersionCheckCallback() {
            @Override
            public void onSuccess(String versionJson) {
                Logger.i("fir", "onSuccess");
                try {
                    mVersionInfo = JSON.parseObject(versionJson, VersionInfo.class);
                    //                Logger.json("fir", versionJson);
                    Logger.i(mVersionInfo.toString());
                    if (AppUtils.getAppInfo(AboutActivity.this).getVersionCode() < Integer.parseInt(mVersionInfo.getVersionCode())) {
                        AnyPref.getDefault().getEditor().putBoolean(Constants.SharedPrefrenced.HAS_NEW_VERSION, true).commit();
                    } else {
                        AnyPref.getDefault().getEditor().putBoolean(Constants.SharedPrefrenced.HAS_NEW_VERSION, false).commit();
                    }
                    if (AnyPref.getDefault().getBoolean(Constants.SharedPrefrenced.HAS_NEW_VERSION, false)) {
                        mImgNewVersionFlag.setVisibility(View.VISIBLE);
                        showUpdateDialog();
                    } else {
                        mImgNewVersionFlag.setVisibility(View.INVISIBLE);
                        ThinkToast.showToast(AboutActivity.this, "当前已经是最新版本", ThinkToast.LENGTH_SHORT, ThinkToast.SUCCESS);
                    }
                } catch (Exception e) {
                    ThinkToast.showToast(AboutActivity.this, "检测新版本失败", ThinkToast.LENGTH_LONG, ThinkToast.ERROR);
                }
            }

            @Override
            public void onFail(Exception exception) {
                Logger.e("fir", "onFail" + "\n" + exception.getMessage());
                ThinkToast.showToast(AboutActivity.this, "检测新版本失败", ThinkToast.LENGTH_LONG, ThinkToast.ERROR);
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

    private void showUpdateDialog() {
        if (mUpdateDialog == null) {
            View mUpdateDialogView = getLayoutInflater().inflate(R.layout.dialog_update, null);
            mUpdateDialog = new AlertDialog.Builder(this).setView(mUpdateDialogView).create();
            mUpdateDialog.setCanceledOnTouchOutside(true);
            mUpdateDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUpdateDialog.isShowing())
                        mUpdateDialog.dismiss();
                }
            });
            mTextUpdateInfo = (TextView) mUpdateDialogView.findViewById(R.id.text_update_info);
            mBtnUpdate = (Button) mUpdateDialogView.findViewById(R.id.btn_update);
        }
        mTextUpdateInfo.setText(mVersionInfo.getChangelog());
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpdateDialog.isShowing())
                    mUpdateDialog.dismiss();
                Logger.i(mVersionInfo.getInstallUrl());
            }
        });
        if (!mUpdateDialog.isShowing())
            mUpdateDialog.show();
    }
}
