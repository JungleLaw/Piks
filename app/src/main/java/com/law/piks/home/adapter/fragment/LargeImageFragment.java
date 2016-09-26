package com.law.piks.home.adapter.fragment;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.law.piks.R;
import com.law.piks.app.base.AppBaseFragmentV4;
import com.law.piks.home.GalleryActivity;
import com.law.piks.medias.entity.Media;

/**
 * Created by Law on 2016/9/25.
 */

public class LargeImageFragment extends AppBaseFragmentV4 {
    private static final String MEDIA = "media";

    private Media media;

    public static LargeImageFragment newInstance(Media media) {
        LargeImageFragment mLargeImageFragment = new LargeImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(MEDIA, media);
        mLargeImageFragment.setArguments(args);
        return mLargeImageFragment;
    }

    @Override
    public int setContentViewLayout() {
        return R.layout.fragment_large_img_display;
    }

    @Override
    public void initVariables() {
        media = getArguments().getParcelable(MEDIA);
    }

    @Override
    public void initViews(@Nullable View view) {
        SubsamplingScaleImageView mDisplayImg = (SubsamplingScaleImageView) view.findViewById(R.id.img_display_SubsamplingScaleImageView);
        mDisplayImg.setImage(ImageSource.uri(media.getPath()), new ImageViewState(0.0f, new PointF(0, 0), media.getOrientation() == -1 ? 0 : media.getOrientation()));
        mDisplayImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GalleryActivity) getActivity()).toggleUI();
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

    }
}
