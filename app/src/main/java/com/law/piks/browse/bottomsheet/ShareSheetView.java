package com.law.piks.browse.bottomsheet;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.law.piks.R;
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
    @ViewInject(R.id.share_sheet_grid)
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

        inflate(context, R.layout.layout_sheet_share, this);
        ThinkInject.bind(this, getRootView());
        mTextTitle.setText(mTitle);
        //        mGridShareSheet.setVerticalSpacing(Configuration.GalleryConstants.divider);
        //        mGridShareSheet.setHorizontalSpacing(Configuration.GalleryConstants.divider);
        //        mGridShareSheet.setNumColumns(Configuration.GalleryConstants.numColumns);
        mGridShareSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnShareItemClickListener != null) {
                    mOnShareItemClickListener.onItemClick(mAdapter.getItem(position));
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
        List<ShareItem> ShareItems = new ArrayList<>();
        //微信
        ShareItem wechatItem = new ShareItem(R.drawable.selector_icon_logo_wechat, "微信", ShareItem.ShareType.WECHAT);
        ShareItems.add(wechatItem);
        //朋友圈
        ShareItem momentsItem = new ShareItem(R.drawable.selector_icon_logo_moments, "朋友圈", ShareItem.ShareType.MOMENTS);
        ShareItems.add(momentsItem);
        //微信收藏
        ShareItem wxFavoriteItem = new ShareItem(R.drawable.selector_icon_logo_wx_favorite, "微信收藏", ShareItem.ShareType.WXFAVORITE);
        ShareItems.add(wxFavoriteItem);
        //新浪微博
        //        ShareItem weiboItem = new ShareItem(R.drawable.selector_icon_logo_weibo, "微博", ShareItem.ShareType.WEIBO);
        //        ShareItems.add(weiboItem);
        //邮件
        //        ShareItem emailItem = new ShareItem(R.drawable.selector_icon_logo_email, "邮件", ShareItem.ShareType.EMAIL);
        //        ShareItems.add(emailItem);
        //短信
        //        ShareItem smsShareItem = new ShareItem(R.drawable.selector_icon_logo_sms, "短信", ShareItem.ShareType.SMS);
        //        ShareItems.add(smsShareItem);
        return ShareItems;
    }

    public void setOnItemClickListener(OnShareItemClickListener onShareItemClickListener) {
        this.mOnShareItemClickListener = onShareItemClickListener;
    }


    public interface OnShareItemClickListener {
        void onItemClick(ShareItem item);
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_sheet_share_item, null);
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

    public class ShareItem {
        private int itemDisplayImgRes;
        private String itemDisplayPath;
        private String itemDisplayText;
        private int itemType;

        public ShareItem(int itemDisplayImgRes, String itemDisplayText) {
            this.itemDisplayImgRes = itemDisplayImgRes;
            this.itemDisplayText = itemDisplayText;
        }

        public ShareItem(String itemDisplayPath, String itemDisplayText) {
            this.itemDisplayPath = itemDisplayPath;
            this.itemDisplayText = itemDisplayText;
        }

        public ShareItem(int itemDisplayImgRes, String itemDisplayText, int itemType) {
            this.itemDisplayImgRes = itemDisplayImgRes;
            this.itemDisplayText = itemDisplayText;
            this.itemType = itemType;
        }

        public String getItemDisplayPath() {
            return itemDisplayPath;
        }

        public int getItemDisplayImgRes() {
            return itemDisplayImgRes;
        }

        public String getItemDisplayText() {
            return itemDisplayText;
        }

        public int getItemShareType() {
            return itemType;
        }

        public final class ShareType {
            public static final int SMS = 1;
            public static final int WECHAT = 2;
            public static final int MOMENTS = 3;
            public static final int WXFAVORITE = 4;
            public static final int EMAIL = 5;
            public static final int WEIBO = 6;
        }
    }
}
