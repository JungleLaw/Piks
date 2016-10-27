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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.medias.entity.Album;
import com.law.think.frame.imageloader.ImageLoader;
import com.law.think.frame.inject.ThinkInject;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.widget.bottomsheet.commons.BottomSheetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Law on 2016/10/13.
 */

public class MoveSheetView extends FrameLayout {
    @ViewInject(R.id.text_move_sheet_title)
    private TextView mTextTitle;
    @ViewInject(R.id.move_sheet_list)
    private ListView mListShareSheet;

    private MoveSheetView.Adapter mAdapter;

    private String mTitle;
    private OnMoveItemClickListener mOnMoveItemClickListener;

    public MoveSheetView(Context context, int titleRes, OnMoveItemClickListener onMoveItemClickListener) {
        this(context, context.getString(titleRes), onMoveItemClickListener);
    }

    public MoveSheetView(Context context, String title, OnMoveItemClickListener onMoveItemClickListener) {
        super(context);
        this.mTitle = title;
        this.mOnMoveItemClickListener = onMoveItemClickListener;

        inflate(context, R.layout.layout_sheet_move, this);
        ThinkInject.bind(this, getRootView());
        mTextTitle.setText(mTitle);
        //        mGridShareSheet.setVerticalSpacing(Configuration.GalleryConstants.divider);
        //        mGridShareSheet.setHorizontalSpacing(Configuration.GalleryConstants.divider);
        //        mGridShareSheet.setNumColumns(Configuration.GalleryConstants.numColumns);
        mListShareSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnMoveItemClickListener != null) {
                    mOnMoveItemClickListener.onItemClick(mAdapter.getItem(position));
                }
            }
        });
        ViewCompat.setElevation(this, BottomSheetUtils.dp2px(getContext(), 16f));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAdapter = new MoveSheetView.Adapter(getMoveItems());
        mListShareSheet.setAdapter(mAdapter);
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

    public List<MoveItem> getMoveItems() {
        List<MoveItem> MoveItems = new ArrayList<>();
        return MoveItems;
    }

    public void setOnItemClickListener(OnMoveItemClickListener OnMoveItemClickListener) {
        this.mOnMoveItemClickListener = OnMoveItemClickListener;
    }

    public interface OnMoveItemClickListener {
        void onItemClick(MoveItem item);
    }

    private class Adapter extends BaseAdapter {
        List<MoveItem> mItems;

        public Adapter(List<MoveItem> items) {
            this.mItems = items;
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public MoveItem getItem(int position) {
            return mItems == null ? null : mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MoveItem mItem = getItem(position);
            MoveSheetView.Adapter.ViewHolder mHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_sheet_move_item, null);
                mHolder = new MoveSheetView.Adapter.ViewHolder(convertView);
                //                mHolder.mImgItem.setLayoutParams(new LinearLayout.LayoutParams(Configuration.GalleryConstants.lenght, Configuration.GalleryConstants.lenght));
                convertView.setTag(mHolder);
            } else {
                mHolder = (MoveSheetView.Adapter.ViewHolder) convertView.getTag();
            }
            //            mHolder.mImgItem.setImageResource(mItem.getItemDisplayImgRes());
            //            Glide.with(getContext()).load(mItem.getItemDisplayPath()).centerCrop().into(mHolder.mImgItem);
            ImageLoader.with(getContext())
                    //                    .signature(albumDisplayEntities.get(position).getCoverMedia().signature())
                    .centerCrop().load(mItem.getItemDisplayPath()).into(mHolder.mImgItem);
            mHolder.mTextItem.setText(mItem.getItemDisplayText());
            return convertView;
        }

        private class ViewHolder {
            @ViewInject(R.id.img_move_item)
            public ImageView mImgItem;
            @ViewInject(R.id.text_move_item)
            public TextView mTextItem;

            public ViewHolder(View view) {
                ThinkInject.bind(this, view);
            }
        }
    }

    public class MoveItem {
        private int itemDisplayImgRes;
        private String itemDisplayPath;
        private String itemDisplayText;
        private int itemType;

        public MoveItem(int itemDisplayImgRes, String itemDisplayText) {
            this.itemDisplayImgRes = itemDisplayImgRes;
            this.itemDisplayText = itemDisplayText;
        }

        public MoveItem(String itemDisplayPath, String itemDisplayText) {
            this.itemDisplayPath = itemDisplayPath;
            this.itemDisplayText = itemDisplayText;
        }

        public MoveItem(int itemDisplayImgRes, String itemDisplayText, int itemType) {
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
