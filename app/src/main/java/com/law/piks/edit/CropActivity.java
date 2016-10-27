package com.law.piks.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseActivity;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.widget.TitleBar;

/**
 * Created by Law on 2016/10/13.
 */

public class CropActivity extends AppBaseActivity {
    @ViewInject(R.id.titlebar)
    private TitleBar mTitleBar;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_crop;
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
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void destroyTask() {

    }
}
