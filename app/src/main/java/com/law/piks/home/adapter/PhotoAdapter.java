package com.law.piks.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_column_item, null);
        LinearLayout mLayout = (LinearLayout) view.findViewById(R.id.root_linearlayout);
        for (int i = 0; i < Configuration.GalleryConstants.numColumns; i++) {
            mLayout.addView(getItemView());
            if (i != (Configuration.GalleryConstants.numColumns - 1)) {
                mLayout.addView(getSpacingView());
            }
        }
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Media[] medias = new Media[Configuration.GalleryConstants.numColumns];
        int size = 0;
        if ((position + 1) * Configuration.GalleryConstants.numColumns > mMedias.size()) {
            size = mMedias.size() - position * Configuration.GalleryConstants.numColumns;
        } else {
            size = Configuration.GalleryConstants.numColumns;
        }
        for (int i = 0; i < Configuration.GalleryConstants.numColumns; i++) {
            if (i < size) {
                medias[i] = mMedias.get(position * Configuration.GalleryConstants.numColumns + i);
            } else {
                medias[i] = null;
            }
        }
        PhotoViewHolder mHolder = (PhotoViewHolder) holder;
        for (int i = 0; i < Configuration.GalleryConstants.numColumns; i++) {
            View view = mHolder.mRootLayout.getChildAt(i * 2);
            if (medias[i] != null) {
                view.setVisibility(View.VISIBLE);
                ImageView mImageView = (ImageView) view.findViewById(R.id.img_photos_item);
                LabelView mLabelView = (LabelView) view.findViewById(R.id.lable_photos_item);
                ImageLoader.with(mContext).centerCrop().load(medias[i].getPath()).signature(medias[i].signature()).placeholder(COLORSID[(position * Configuration.GalleryConstants.numColumns + i) % COLORSID.length]).into(mImageView);
                if (medias[i].isGif()) {
                    mLabelView.setVisibility(View.VISIBLE);
                    mLabelView.setLabel("GIF");
                } else {
                    mLabelView.setVisibility(View.GONE);
                }
                mPeekAndPop.addLongClickView(view, position * Configuration.GalleryConstants.numColumns + i);
                if (mOnClickCallback != null) {
                    final int index = i;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnClickCallback.OnClickCallback(position * Configuration.GalleryConstants.numColumns + index);
                        }
                    });
                }
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mMedias == null)
            return 0;
        if (mMedias.size() % Configuration.GalleryConstants.numColumns == 0) {
            return mMedias.size() <= Configuration.GalleryConstants.numColumns ? 1 : mMedias.size() / Configuration.GalleryConstants.numColumns;
        } else {
            return mMedias.size() / Configuration.GalleryConstants.numColumns + 1;
        }
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
        notifyDataSetChanged();
    }

    private View getItemView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_media_item, null);
        view.findViewById(R.id.layout_photos_item_root).setLayoutParams(new LinearLayout.LayoutParams(Configuration.GalleryConstants.lenght, Configuration.GalleryConstants.lenght));
        return view;
    }

    private View getSpacingView() {
        View view = new View(mContext);
        view.setLayoutParams(new LinearLayout.LayoutParams(Configuration.GalleryConstants.divider, Configuration.GalleryConstants.lenght));
        return view;
    }

    public interface OnClickCallback {
        public void OnClickCallback(int position);
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.root_linearlayout)
        public LinearLayout mRootLayout;

        public PhotoViewHolder(View view) {
            super(view);
            ThinkInject.bind(this, view);
        }
    }
}
