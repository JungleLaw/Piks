package com.law.piks.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.law.piks.app.Constants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Constants.SHARE.WX.APP_ID);
        api.registerApp(Constants.SHARE.WX.APP_ID);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Constants.SHARE.WX.APP_ID);
        api.registerApp(Constants.SHARE.WX.APP_ID);
        handleIntent(getIntent());
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        finish();
    }

    protected void handleIntent(Intent intent) {
        api.handleIntent(intent, this);
    }

}
