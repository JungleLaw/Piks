package com.law.piks.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.law.piks.R;
import com.law.piks.medias.Configuration;
import com.law.piks.medias.entity.Media;
import com.law.piks.widget.peekandpop.PeekAndPop;
import com.law.think.frame.imageloader.ImageLoader;
import com.law.think.frame.inject.ThinkInject;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.utils.ScreenUtils;
import com.law.think.frame.widget.labelview.LabelView;

import java.util.List;

/**
 * Created by Law on 2016/9/26.
 */

public class PhotoAdapter extends RecyclerView.Adapter {
    private static final int[] COLORSID = {R.color.color_for_photo_fri, R.color.color_for_photo_sec, R.color.color_for_photo_thi, R.color.color_for_photo_fou, R.color.color_for_photo_fif};

    private Context mContext;
    private List<Media> mMedias;

    private int width;

    private PeekAndPop mPeekAndPop;
    private View mPeekAndPopView;
    private ImageView mPeekAndPopImageView;
    private OnClickCallback mOnClickCallback;

    public PhotoAdapter(final Context context, final List<Media> medias, PeekAndPop peekAndPop) {
        this.mContext = context;
        this.mMedias = medias;
        this.mPeekAndPop = peekAndPop;

        mPeekAndPopView = mPeekAndPop.getPeekView();
        mPeekAndPopImageView = (ImageView) mPeekAndPopView.findViewById(R.id.img_peek_and_pop);
        width = (int) (ScreenUtils.getScreenWidth(context) * 0.85f);
        mPeekAndPopImageView.setLayoutParams(new FrameLayout.LayoutParams(width, width));

        mPeekAndPop.setOnGeneralActionListener(new PeekAndPop.OnGeneralActionListener() {
            @Override
            public void onPeek(View longClickView, int position) {
                Logger.i("OnPeek");
                if (mMedias != null && mMedias.size() != 0) {
                    Media media = mMedias.get(position);
                    ImageLoader.with(mContext).load(media.getPath()).signature(media.signature()).centerCrop().into(mPeekAndPopImageView);
                }
            }

            @Override
            public void onPop(View longClickView, int position) {

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_media_item, null);
        view.findViewById(R.id.layout_photos_item_root).setLayoutParams(new AbsListView.LayoutParams(Configuration.GalleryConstants.lenght, Configuration.GalleryConstants.lenght));
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Media media = mMedias.get(position);
        PhotoViewHolder mHolder = (PhotoViewHolder) holder;
        if (media.isGif()) {
            mHolder.mLabelView.setVisibility(View.VISIBLE);
            mHolder.mLabelView.setLabel("GIF");
        } else {
            mHolder.mLabelView.setVisibility(View.GONE);
        }
        if (mOnClickCallback != null) {
            mHolder.mRootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickCallback.OnClickCallback(position);
                }
            });
        }
        //        mHolder.mRootLayout.setOnLongClickListener(new OnMediaLongClickListener(position));
        mPeekAndPop.addLongClickView(mHolder.mRootLayout, position);
        ImageLoader.with(mContext).centerCrop().signature(media.signature()).placeholder(COLORSID[position % COLORSID.length]).thumbnail(0.5f).load(media.getPath()).into(mHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMedias == null ? 0 : mMedias.size();
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.mOnClickCallback = onClickCallback;
    }

    public void refresh(List<Media> medias) {
        this.mMedias = medias;
        mPeekAndPop.setOnGeneralActionListener(new PeekAndPop.OnGeneralActionListener() {
            @Override
            public void onPeek(View longClickView, int position) {
                Logger.i("OnPeek");
                Logger.i("inner: " + (mMedias == null));
                if (mMedias != null && mMedias.size() != 0) {
                    Media media = mMedias.get(position);
                    ImageLoader.with(mContext).load(media.getPath()).signature(media.signature()).centerCrop().into(mPeekAndPopImageView);
                }
            }

            @Override
            public void onPop(View longClickView, int position) {

            }
        });
    }

    public interface OnClickCallback {
        public void OnClickCallback(int position);
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.layout_photos_item_root)
        public RelativeLayout mRootLayout;
        @ViewInject(R.id.img_photos_item)
        public ImageView mImageView;
        @ViewInject(R.id.lable_photos_item)
        public LabelView mLabelView;

        public PhotoViewHolder(View view) {
            super(view);
            ThinkInject.bind(this, view);
        }
    }
}
