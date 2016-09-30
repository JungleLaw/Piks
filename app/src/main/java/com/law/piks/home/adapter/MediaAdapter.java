package com.law.piks.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
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
 * Created by Law on 2016/9/9.
 */
public class MediaAdapter extends BaseAdapter {
    private static final int[] COLORSID = {R.color.color_for_photo_fri, R.color.color_for_photo_sec, R.color.color_for_photo_thi, R.color.color_for_photo_fou, R.color.color_for_photo_fif};

    private Context mContext;
    private List<Media> mMedias;

    private int width;

    private PeekAndPop mPeekAndPop;
    private View mPeekAndPopView;
    private ImageView mPeekAndPopImageView;
    private OnClickCallback onClickCallback;

    public MediaAdapter(final Context context, final List<Media> medias, PeekAndPop peekAndPop) {
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
    public int getCount() {
        return mMedias == null ? 0 : mMedias.size();
    }

    @Override
    public Object getItem(int i) {
        return mMedias == null ? null : mMedias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Media media = mMedias.get(position);
        ViewHolder mHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_media_item, null);
            mHolder = new ViewHolder(convertView);
            mHolder.mRootLayout.setLayoutParams(new AbsListView.LayoutParams(Configuration.GalleryConstants.lenght, Configuration.GalleryConstants.lenght));
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
//        if (media.isGif()) {
//            mHolder.mLabelView.setVisibility(View.VISIBLE);
//            mHolder.mLabelView.setLabel("GIF");
//        } else {
//            mHolder.mLabelView.setVisibility(View.GONE);
//        }
        mHolder.mRootLayout.setOnClickListener(new OnMediaClickListener(position));
        //        mHolder.mRootLayout.setOnLongClickListener(new OnMediaLongClickListener(position));
        mPeekAndPop.addLongClickView(mHolder.mRootLayout, position);
        ImageLoader.with(mContext).centerCrop().signature(media.signature()).placeholder(COLORSID[position % COLORSID.length]).thumbnail(0.5f).load(media.getPath()).into(mHolder.mImageView);
        //        Glide.with(mContext).load(Uri.fromFile(new File(media.getPath()))).signature(new StringSignature(media.getPath() + "-" + media.getDateModified())).placeholder(COLORSID[position % COLORSID.length]).thumbnail(0.5f).centerCrop().into(mHolder.mImageView);
        return convertView;
    }

    public void refresh(List<Media> medias) {
        this.mMedias = medias;
        notifyDataSetInvalidated();
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

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public interface OnClickCallback {
        public void OnClickCallback(int position);
    }

    private final class ViewHolder {
        @ViewInject(R.id.layout_photos_item_root)
        public RelativeLayout mRootLayout;
        @ViewInject(R.id.img_photos_item)
        public ImageView mImageView;
//        @ViewInject(R.id.lable_photos_item)
//        public LabelView mLabelView;

        public ViewHolder(View view) {
            ThinkInject.bind(this, view);
        }
    }

    private class OnMediaClickListener implements View.OnClickListener {
        private int position;

        public OnMediaClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onClickCallback != null) {
                onClickCallback.OnClickCallback(position);
            }
        }
    }
}
