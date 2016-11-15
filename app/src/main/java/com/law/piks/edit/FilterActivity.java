package com.law.piks.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.law.piks.R;
import com.law.piks.app.base.AppBaseActivity;
import com.law.piks.edit.adapter.FilterAdapter;
import com.law.piks.edit.entity.FilterEntity;
import com.law.piks.edit.filter.InstaFilter;
import com.law.piks.utils.ScaleUtils;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.utils.PixelUtils;
import com.law.think.frame.widget.TitleBar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by Law on 2016/10/12.
 */

public class FilterActivity extends AppBaseActivity implements Handler.Callback {
    private static final int MSG_SWITH_FILTER = 1001;

    private static final int MARGIN = 16;

    private static final String FILE_PATH = "path";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    private final int[] FILTER_NAMES = {R.string.filter_normal, R.string.filter_amaro, R.string.filter_rise, R.string.filter_hudson, R.string.filter_xproii, R.string.filter_sierra, R.string.filter_lomo, R.string.filter_earlybird, R.string.filter_sutro, R.string.filter_toaster, R.string.filter_brannan, R.string.filter_inkwell, R.string.filter_walden, R.string.filter_hefe, R.string.filter_valencia, R.string.filter_nashville, R.string.filter_1977, R.string.filter_lordkelvin};
    private final int[] FILTER_IMGS = {R.drawable.filter_normal, R.drawable.filter_amaro, R.drawable.filter_rise, R.drawable.filter_hudson, R.drawable.filter_xproii, R.drawable.filter_sierra, R.drawable.filter_lomo, R.drawable.filter_earlybird, R.drawable.filter_sutro, R.drawable.filter_toaster, R.drawable.filter_brannan, R.drawable.filter_inkwell, R.drawable.filter_walden, R.drawable.filter_hefe, R.drawable.filter_valencia, R.drawable.filter_nashville, R.drawable.filter_1977, R.drawable.filter_lordkelvin};

    @ViewInject(R.id.titlebar)
    private TitleBar mTitleBar;
    @ViewInject(R.id.gpu_img)
    private GPUImageView mGPUImageView;
    @ViewInject(R.id.recycleview_filter)
    private RecyclerView mFiltersRecyclerView;
    @ViewInject(R.id.parent_gpuimageview)
    private RelativeLayout mParentGPUImageView;

    private HandlerThread thread;
    private Handler handler;

    private String filePath;
    private int width;
    private int height;
    private List<FilterEntity> mEntities;

    private FilterAdapter mAdapter;

    public static void navigateToFilterActivity(Context context, String path, int width, int height, int requestCode) {
        Intent mIntent = new Intent(context, FilterActivity.class);
        mIntent.putExtra(FILE_PATH, path);
        mIntent.putExtra(WIDTH, width);
        mIntent.putExtra(HEIGHT, height);
        ((Activity) context).startActivityForResult(mIntent, requestCode);
    }

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_filter;
    }

    @Override
    public void initVariables() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        width = getIntent().getIntExtra(WIDTH, 0);
        height = getIntent().getIntExtra(HEIGHT, 0);

        mEntities = new ArrayList<>();
        mEntities.add(new FilterEntity(FILTER_NAMES[0], 0, FILTER_IMGS[0], true));
        mEntities.add(new FilterEntity(FILTER_NAMES[1], 1, FILTER_IMGS[1], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[2], 2, FILTER_IMGS[2], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[3], 3, FILTER_IMGS[3], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[4], 4, FILTER_IMGS[4], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[5], 5, FILTER_IMGS[5], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[6], 6, FILTER_IMGS[6], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[7], 7, FILTER_IMGS[7], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[8], 8, FILTER_IMGS[8], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[9], 9, FILTER_IMGS[9], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[10], 10, FILTER_IMGS[10], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[11], 11, FILTER_IMGS[11], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[12], 12, FILTER_IMGS[12], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[13], 13, FILTER_IMGS[13], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[14], 14, FILTER_IMGS[14], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[15], 15, FILTER_IMGS[15], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[16], 16, FILTER_IMGS[16], false));
        mEntities.add(new FilterEntity(FILTER_NAMES[17], 17, FILTER_IMGS[17], false));
        mAdapter = new FilterAdapter(this, mEntities);
    }

    @Override
    public void initViews() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFiltersRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void initListener() {
        mTitleBar.setOnLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        thread = new HandlerThread("filter");
        thread.start();
        handler = new Handler(thread.getLooper(), this);
        Glide.with(this).load(filePath).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mGPUImageView.setImage(resource);
            }
        });
        mFiltersRecyclerView.setAdapter(mAdapter);
        mAdapter.setCallback(new FilterAdapter.OnFilterClickedCallback() {
            @Override
            public void onFilterClickedCallback(int position) {
                handler.removeMessages(MSG_SWITH_FILTER);
                Message msg = handler.obtainMessage(MSG_SWITH_FILTER);
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        });
        mGPUImageView.post(new Runnable() {
            @Override
            public void run() {
                Logger.i("media.getWidth() = " + width + ",media.getHeight() = " + height + ",mParentGPUImageView.getWidth() = " + mParentGPUImageView.getWidth() + ",mParentGPUImageView.getHeight() = " + mParentGPUImageView.getHeight());
                int[] size = ScaleUtils.getScaleSize(width, height, mParentGPUImageView.getWidth(), mParentGPUImageView.getHeight() - PixelUtils.dp2px(FilterActivity.this, MARGIN) * 2);
                Logger.i("size width = " + size[0] + ",size height = " + size[1]);
                mGPUImageView.setLayoutParams(new RelativeLayout.LayoutParams(size[0], size[1]));
            }
        });

    }

    @Override
    public void destroyTask() {
        if (thread != null)
            thread.quit();
        FilterHelper.destroyFilters();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SWITH_FILTER:
                int index = msg.arg1;
                InstaFilter filter = FilterHelper.getFilter(this, index);
                if (filter != null) {
                    mGPUImageView.setFilter(filter);
                }
                break;

            default:
                break;
        }
        return true;
    }

    private void saveBitmap() {
        BufferedOutputStream bos = null;
        try {
            File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");

            String tmpFilePath = outFile.getAbsolutePath();
            File tmpFile = new File(tmpFilePath);
            Bitmap bitmap = mGPUImageView.capture();
            bos = new BufferedOutputStream(new FileOutputStream(tmpFilePath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bitmap.recycle();
            MediaScannerConnection.scanFile(this, new String[]{tmpFilePath}, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e2) {
                }
            }
        }
    }
}
