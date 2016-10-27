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
 * Created by Law on 2016/10/13.
 */

public class SettingSheetView extends FrameLayout {
    @ViewInject(R.id.text_setting_sheet_title)
    private TextView mTextTitle;
    @ViewInject(R.id.setting_sheet_grid)
    private GridView mGridShareSheet;

    private SettingSheetView.Adapter mAdapter;

    private String mTitle;
    private OnSettingItemClickListener mOnSettingItemClickListener;

    public SettingSheetView(Context context, int titleRes, OnSettingItemClickListener onSettingItemClickListener) {
        this(context, context.getString(titleRes), onSettingItemClickListener);
    }

    public SettingSheetView(Context context, String title, OnSettingItemClickListener onSettingItemClickListener) {
        super(context);
        this.mTitle = title;
        this.mOnSettingItemClickListener = onSettingItemClickListener;

        inflate(context, R.layout.layout_sheet_settings, this);
        ThinkInject.bind(this, getRootView());
        mTextTitle.setText(mTitle);
        //        mGridShareSheet.setVerticalSpacing(Configuration.GalleryConstants.divider);
        //        mGridShareSheet.setHorizontalSpacing(Configuration.GalleryConstants.divider);
        //        mGridShareSheet.setNumColumns(Configuration.GalleryConstants.numColumns);
        mGridShareSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnSettingItemClickListener != null) {
                    mOnSettingItemClickListener.onItemClick(mAdapter.getItem(position));
                }
            }
        });
        ViewCompat.setElevation(this, BottomSheetUtils.dp2px(getContext(), 16f));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAdapter = new SettingSheetView.Adapter(getSettingItems());
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

    public List<SettingItem> getSettingItems() {
        List<SettingItem> SettingItems = new ArrayList<>();
        //微信
        SettingItem wechatItem = new SettingItem(R.drawable.selector_icon_logo_wechat, "微信");
        SettingItems.add(wechatItem);
        //朋友圈
        SettingItem momentsItem = new SettingItem(R.drawable.selector_icon_logo_moments, "朋友圈");
        SettingItems.add(momentsItem);
        //微信收藏
        SettingItem wxFavoriteItem = new SettingItem(R.drawable.selector_icon_logo_wx_favorite, "微信收藏");
        SettingItems.add(wxFavoriteItem);
        //新浪微博
        SettingItem weiboItem = new SettingItem(R.drawable.selector_icon_logo_weibo, "微博");
        SettingItems.add(weiboItem);
        //邮件
        //        SettingItem emailItem = new SettingItem(R.drawable.selector_icon_logo_email, "邮件", SettingItem.ShareType.EMAIL);
        //        SettingItems.add(emailItem);
        //短信
        //        SettingItem smsSettingItem = new SettingItem(R.drawable.selector_icon_logo_sms, "短信", SettingItem.ShareType.SMS);
        //        SettingItems.add(smsSettingItem);
        return SettingItems;
    }

    public void setOnItemClickListener(OnSettingItemClickListener onSettingItemClickListener) {
        this.mOnSettingItemClickListener = onSettingItemClickListener;
    }

    public interface OnSettingItemClickListener {
        void onItemClick(SettingItem item);
    }

    private class Adapter extends BaseAdapter {
        List<SettingItem> mItems;

        public Adapter(List<SettingItem> items) {
            this.mItems = items;
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public SettingItem getItem(int position) {
            return mItems == null ? null : mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SettingItem mItem = getItem(position);
            SettingSheetView.Adapter.ViewHolder mHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_sheet_settings_item, null);
                mHolder = new SettingSheetView.Adapter.ViewHolder(convertView);
                //                mHolder.mImgItem.setLayoutParams(new LinearLayout.LayoutParams(Configuration.GalleryConstants.lenght, Configuration.GalleryConstants.lenght));
                convertView.setTag(mHolder);
            } else {
                mHolder = (SettingSheetView.Adapter.ViewHolder) convertView.getTag();
            }
            mHolder.mImgItem.setImageResource(mItem.getItemDisplayImgRes());
            mHolder.mTextItem.setText(mItem.getItemDisplayText());
            return convertView;
        }

        private class ViewHolder {
            @ViewInject(R.id.img_setting_item)
            public ImageView mImgItem;
            @ViewInject(R.id.text_setting_item)
            public TextView mTextItem;

            public ViewHolder(View view) {
                ThinkInject.bind(this, view);
            }
        }
    }

    public class SettingItem {
        private int itemDisplayImgRes;
        private String itemDisplayPath;
        private String itemDisplayText;
        private int itemType;

        public SettingItem(int itemDisplayImgRes, String itemDisplayText) {
            this.itemDisplayImgRes = itemDisplayImgRes;
            this.itemDisplayText = itemDisplayText;
        }

        public SettingItem(String itemDisplayPath, String itemDisplayText) {
            this.itemDisplayPath = itemDisplayPath;
            this.itemDisplayText = itemDisplayText;
        }

        public SettingItem(int itemDisplayImgRes, String itemDisplayText, int itemType) {
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

    }
}
