package com.law.piks.others;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.law.piks.R;
import com.law.piks.app.Constants;
import com.law.piks.app.base.AppBaseActivity;
import com.law.piks.others.update.UpdateService;
import com.law.piks.others.update.VersionInfo;
import com.law.piks.splash.IntroActivity;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.inject.annotation.event.OnClick;
import com.law.think.frame.prefs.AnyPref;
import com.law.think.frame.utils.AppUtils;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.widget.ThinkToast;
import com.law.think.frame.widget.TitleBar;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.io.File;

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
    @ViewInject(R.id.shimmer_text_copyright)
    private ShimmerTextView mShimmerTextCopyright;

    private AlertDialog mUpdateDialog;
    private TextView mTextUpdateInfo;
    private Button mBtnUpdate;

    private VersionInfo mVersionInfo;

    private Shimmer mShimmer;

    private DownloadReceiver mReceiver;
    private int mDownloadStatus = -1;
    private String mNewVersionFilePath;

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

        mShimmer = new Shimmer();
        mShimmer.setDuration(1000).setStartDelay(500).setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        mShimmer.start(mShimmerTextCopyright);
    }

    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregister();
    }

    @Override
    public void destroyTask() {
        mShimmer.cancel();
    }

    @OnClick({R.id.btn_check_for_update, R.id.btn_intro_page, R.id.btn_appreciation, R.id.btn_about_me})
    private void startAboutMeActivity(View view) {
        switch (view.getId()) {
            case R.id.btn_check_for_update:
                switch (mDownloadStatus) {
                    case -1:
                        checkForUpdate();
                        break;
                    case UpdateService.DOWNLOAD_GOING:
                        ThinkToast.showToast(this, "正在下载新版本", ThinkToast.LENGTH_LONG, ThinkToast.INFO);
                        break;
                    case UpdateService.DOWNLOAD_ERROR:
                        ThinkToast.showToast(this, "下载新版本出现错误", ThinkToast.LENGTH_LONG, ThinkToast.INFO);
                        mDownloadStatus = -1;
                        break;
                    case UpdateService.DOWNLOAD_COMPELET:
                        //                        ThinkToast.showToast(this, "新版本下载完成", ThinkToast.LENGTH_LONG, ThinkToast.INFO);
                        AppUtils.installApp(this, new File(mNewVersionFilePath));
                        break;
                }
                break;
            case R.id.btn_intro_page:
                IntroActivity.navigateToIntroActivity(this, true);
                break;
            case R.id.btn_appreciation:
                startActivity(new Intent(this, AppreciationActivity.class));
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
                UpdateService.startDownload(AboutActivity.this, mVersionInfo.getInstallUrl(), mVersionInfo.getVersionName());
            }
        });
        if (!mUpdateDialog.isShowing())
            mUpdateDialog.show();
    }

    private void register() {
        mReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdateService.DOWNLOAD_BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }

    private void unregister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
    }

    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (TextUtils.isEmpty(action) || !action.equals(UpdateService.DOWNLOAD_BROADCAST)) {
                return;
            }
            mDownloadStatus = intent.getIntExtra(UpdateService.DOWNLOAD_STATUS, -1);
            if (mDownloadStatus == UpdateService.DOWNLOAD_COMPELET) {
                mNewVersionFilePath = intent.getStringExtra(UpdateService.DOWNLOAD_NEW_VERSION_FILE);
            }
            Logger.i("mDownloadStatus = " + mDownloadStatus);
        }
    }
}
