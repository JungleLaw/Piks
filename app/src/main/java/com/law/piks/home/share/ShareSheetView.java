package com.law.piks.home.share;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.medias.Configuration;
import com.law.think.frame.inject.ThinkInject;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.widget.bottomsheet.commons.BottomSheetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Law on 2016/10/10.
 */

public class ShareSheetView extends FrameLayout {
    @ViewInject(R.id.text_share_sheet_title)
    private TextView mTextTitle;
    @ViewInject(R.id.text_share_sheet_grid)
    private GridView mGridShareSheet;

    private Adapter mAdapter;

    private String mTitle;
    private OnShareItemClickListener mOnShareItemClickListener;

    public ShareSheetView(Context context, int titleRes, OnShareItemClickListener onShareItemClickListener) {
        this(context, context.getString(titleRes), onShareItemClickListener);
    }

    public ShareSheetView(Context context, String title, OnShareItemClickListener onShareItemClickListener) {
        super(context);
        this.mTitle = title;
        this.mOnShareItemClickListener = onShareItemClickListener;

        inflate(context, R.layout.layout_share_sheet, this);
        ThinkInject.bind(this, getRootView());
        mTextTitle.setText(mTitle);
        //        mGridShareSheet.setVerticalSpacing(Configuration.GalleryConstants.divider);
        //        mGridShareSheet.setHorizontalSpacing(Configuration.GalleryConstants.divider);
        //        mGridShareSheet.setNumColumns(Configuration.GalleryConstants.numColumns);
        mGridShareSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnShareItemClickListener != null) {
                    mOnShareItemClickListener.onShareItemClick(mAdapter.getItem(position));
                }
            }
        });
        ViewCompat.setElevation(this, BottomSheetUtils.dp2px(getContext(), 16f));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAdapter = new Adapter(getShareItems());
        mGridShareSheet.setAdapter(mAdapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new BottomSheetUtils.ShadowOutline(w, h));
        }
    }

    public List<ShareItem> getShareItems() {
        List<ShareItem> shareItems = new ArrayList<>();
        //微信
        ShareItem wechatItem = new ShareItem(R.drawable.selector_icon_logo_wechat, "微信", ShareType.WECHAT);
        shareItems.add(wechatItem);
        //朋友圈
        ShareItem momentsItem = new ShareItem(R.drawable.selector_icon_logo_moments, "朋友圈", ShareType.MOMENTS);
        shareItems.add(momentsItem);
        //微信收藏
        ShareItem wxFavoriteItem = new ShareItem(R.drawable.selector_icon_logo_wx_favorite, "微信收藏", ShareType.WXFAVORITE);
        shareItems.add(wxFavoriteItem);
        //新浪微博
        ShareItem weiboItem = new ShareItem(R.drawable.selector_icon_logo_weibo, "微博", ShareType.WEIBO);
        shareItems.add(weiboItem);
        //邮件
        //        ShareItem emailItem = new ShareItem(R.drawable.selector_icon_logo_email, "邮件", ShareType.EMAIL);
        //        shareItems.add(emailItem);
        //短信
        //        ShareItem smsShareItem = new ShareItem(R.drawable.selector_icon_logo_sms, "短信", ShareType.SMS);
        //        shareItems.add(smsShareItem);
        return shareItems;
    }

    public void setOnShareItemClickListener(OnShareItemClickListener onShareItemClickListener) {
        this.mOnShareItemClickListener = onShareItemClickListener;
    }


    public interface OnShareItemClickListener {
        void onShareItemClick(ShareItem item);
    }

    public static final class ShareType {
        public static final int WEIBO = 0;
        public static final int SMS = 1;
        public static final int WECHAT = 2;
        public static final int MOMENTS = 3;
        public static final int WXFAVORITE = 4;
        public static final int EMAIL = 5;
    }

    public class ShareItem {

        private int itemDisplayImgRes;
        private String itemDisplayText;
        private int itemShareType;

        public ShareItem(int itemDisplayImgRes, String itemDisplayText, int itemShareType) {
            this.itemDisplayImgRes = itemDisplayImgRes;
            this.itemDisplayText = itemDisplayText;
            this.itemShareType = itemShareType;
        }

        public int getItemDisplayImgRes() {
            return itemDisplayImgRes;
        }

        public String getItemDisplayText() {
            return itemDisplayText;
        }

        public int getItemShareType() {
            return itemShareType;
        }
    }

    private class Adapter extends BaseAdapter {
        List<ShareItem> mItems;

        public Adapter(List<ShareItem> items) {
            this.mItems = items;
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public ShareItem getItem(int position) {
            return mItems == null ? null : mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ShareItem mItem = getItem(position);
            ViewHolder mHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_share_sheet_item, null);
                mHolder = new ViewHolder(convertView);
                //                mHolder.mImgItem.setLayoutParams(new LinearLayout.LayoutParams(Configuration.GalleryConstants.lenght, Configuration.GalleryConstants.lenght));
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            mHolder.mImgItem.setImageResource(mItem.getItemDisplayImgRes());
            mHolder.mTextItem.setText(mItem.getItemDisplayText());
            return convertView;
        }

        private class ViewHolder {
            @ViewInject(R.id.img_share_item)
            public ImageView mImgItem;
            @ViewInject(R.id.text_share_item)
            public TextView mTextItem;

            public ViewHolder(View view) {
                ThinkInject.bind(this, view);
            }
        }
    }
}
