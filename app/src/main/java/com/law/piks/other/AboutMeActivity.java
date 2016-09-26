package com.law.piks.other;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseActivity;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.inject.annotation.event.OnClick;
import com.law.think.frame.widget.ThinkToast;
import com.law.think.frame.widget.TitleBar;

/**
 * Created by Law on 2016/9/19.
 */
public class AboutMeActivity extends AppBaseActivity {
    @ViewInject(R.id.titlebar)
    private TitleBar mTitleBar;

    private Dialog mWechatQRCodeDialog;
    private ClipboardManager clipboardManager;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_about_me;
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
        initQRCodeDialog();
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void destroyTask() {

    }

    @OnClick({R.id.layout_email, R.id.layout_weibo, R.id.layout_wechat_qrcode, R.id.layout_github})
    private void OnViewClick(View view) {
        switch (view.getId()) {
            case R.id.layout_email:
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, getString(R.string.email_account)));
                ThinkToast.showToast(this, "已复制到剪贴板", ThinkToast.LENGTH_SHORT, ThinkToast.INFO);
                break;
            case R.id.layout_weibo:
                startBrowser(getString(R.string.weibo_account));
                break;
            case R.id.layout_wechat_qrcode:
                if (mWechatQRCodeDialog != null) {
                    mWechatQRCodeDialog.show();
                }
                break;
            case R.id.layout_github:
                startBrowser(getString(R.string.github_account));
                break;
            default:
        }
    }

    private void initQRCodeDialog() {
        mWechatQRCodeDialog = new Dialog(this, R.style.dialogStyle);
        mWechatQRCodeDialog.setContentView(R.layout.layout_wechat_qrcode_dialog);
    }

    private void startBrowser(String uri) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(uri);
        intent.setData(content_url);
        //        intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
        startActivity(intent);
    }
}
