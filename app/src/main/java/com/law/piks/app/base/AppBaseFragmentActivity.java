package com.law.piks.app.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.law.piks.R;
import com.law.piks.widget.ProgressBarDialog;
import com.law.think.frame.protocol.ProtocolFragmentActivity;
import com.law.think.frame.tinybus.TinyBus;
import com.law.think.frame.utils.SDKUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class AppBaseFragmentActivity extends ProtocolFragmentActivity {
    public static final int[] entryAnims = {R.anim.slide_entry_right, R.anim.slide_exit_left};
    public static final int[] exitAnims = {R.anim.slide_entry_left, R.anim.slide_exit_right};
    private TinyBus mBus;
    private ProgressBarDialog mProgressBarDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mBus = TinyBus.from(this);
        setNavBarColor();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        mBus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public int[] loadEntryAnimation() {
        // TODO Auto-generated method stub
        return entryAnims;
    }

    @Override
    public int[] loadExitAnimation() {
        // TODO Auto-generated method stub
        return exitAnims;
    }

    public TinyBus getBus() {
        return mBus;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setNavBarColor() {
        if (SDKUtils.hasLollopop()) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void showProgress() {
        showProgress(true);
    }

    public void showProgress(boolean cancelable) {
        if (mProgressBarDialog == null) {
            mProgressBarDialog = ProgressBarDialog.createDialog(this);
        }
        mProgressBarDialog.setCancelable(false);
        if (!mProgressBarDialog.isShowing())
            mProgressBarDialog.show();
    }

    public void dismissProgress() {
        if (mProgressBarDialog != null && mProgressBarDialog.isShowing())
            mProgressBarDialog.dismiss();
    }
}
