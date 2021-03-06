package com.law.piks.browse.adapter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseFragmentV4;
import com.law.piks.browse.GalleryActivity;
import com.law.piks.medias.entity.Media;
import com.law.piks.utils.ScaleUtils;
import com.law.think.frame.imageloader.ImageLoader;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.widget.ThinkToast;

/**
 * Created by Law on 2016/9/25.
 */

public class GifImageFragment extends AppBaseFragmentV4 {
    private static final String MEDIA = "media";
    private static final int DOUBLE_CLICK_TIME = 250; //两次单击的时间间隔
    private Media media;
    private boolean waitDouble = true;

    public static GifImageFragment newInstance(Media media) {
        GifImageFragment gifImageFragment = new GifImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(MEDIA, media);
        gifImageFragment.setArguments(args);
        return gifImageFragment;
    }

    @Override
    public int setContentViewLayout() {
        return R.layout.fragment_gif_display;
    }

    @Override
    public void initVariables() {
        media = getArguments().getParcelable(MEDIA);
    }

    @Override
    public void initViews(@Nullable View view) {
        final ImageView mGifImageView = (ImageView) view.findViewById(R.id.img_gif_display);
        final RelativeLayout mRootGif = (RelativeLayout) view.findViewById(R.id.root_gif);
        ImageLoader.with(getContext()).signature(media.signature()).fitXY().asGif().load(media.getPath()).into(mGifImageView);
        mGifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waitDouble == true) {
                    waitDouble = false;
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(DOUBLE_CLICK_TIME);
                                if (waitDouble == false) {
                                    waitDouble = true;
                                    singleClick();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                } else {
                    waitDouble = true;
                    doubleClick();
                }
            }

        });
        mGifImageView.post(new Runnable() {
            @Override
            public void run() {
                int[] size = ScaleUtils.getScaleSize(media.getWidth(), media.getHeight(), mRootGif.getWidth(), mRootGif.getHeight());
                mGifImageView.setLayoutParams(new RelativeLayout.LayoutParams(size[0], size[1]));
            }
        });
    }

    @Override
    public void initListener() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void destroyTask() {

    }// 单击响应事件

    private void singleClick() {
        ((GalleryActivity) getActivity()).toggleUI();
    }

    // 双击响应事件
    private void doubleClick() {
        ThinkToast.showToast(getContext(), "浏览GIF格式图片时，不支持缩放", ThinkToast.LENGTH_SHORT, ThinkToast.DEFAULT);
    }
}
