package com.law.piks.home.adapter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseFragmentV4;
import com.law.piks.home.GalleryActivity;
import com.law.piks.medias.entity.Media;
import com.law.think.frame.imageloader.ImageLoader;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Law on 2016/9/25.
 */

public class ImageFragment extends AppBaseFragmentV4 {
    private static final String MEDIA = "media";

    private Media media;

    public static ImageFragment newInstance(Media media) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(MEDIA, media);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public int setContentViewLayout() {
        return R.layout.fragment_img_display;
    }

    @Override
    public void initVariables() {
        media = getArguments().getParcelable(MEDIA);
    }

    @Override
    public void initViews(@Nullable View view) {
        PhotoView mPhotoView = (PhotoView) view.findViewById(R.id.img_display_photoview);
        ImageLoader.with(getContext()).signature(media.signature()).load(media.getPath()).into(mPhotoView);
        mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
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