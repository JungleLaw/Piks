package com.law.piks.other;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseActivity;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.widget.TitleBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Law on 2016/10/11.
 */

public class AppreciationActivity extends AppBaseActivity {
    @ViewInject(R.id.titlebar)
    private TitleBar mTitleBar;
    @ViewInject(R.id.text_thanks_list)
    private TextView mTextThanksList;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_appreciation;
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
        try {
            String content = readTextFile(getAssets().open("thanks_list.txt"));
            mTextThanksList.setText(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyTask() {

    }

    private String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }
        return outputStream.toString();
    }
}
