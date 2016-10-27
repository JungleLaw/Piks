package com.law.piks.edit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.law.piks.R;
import com.law.piks.edit.entity.FilterEntity;
import com.law.think.frame.imageloader.ImageLoader;
import com.law.think.frame.inject.ThinkInject;
import com.law.think.frame.inject.annotation.ViewInject;

import java.util.List;

/**
 * Created by Law on 2016/10/24.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private Context mContext;
    private List<FilterEntity> mFilterEntities;
    private OnFilterClickedCallback callback;
    private int selected = 0;

    public FilterAdapter(Context context, List<FilterEntity> entities) {
        this.mContext = context;
        this.mFilterEntities = entities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_filter_recycleview_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(mContext).load(mFilterEntities.get(position).getDisplayResId()).asBitmap().centerCrop().into(holder.mImgFilter);
        holder.mTextFilter.setText(mContext.getString(mFilterEntities.get(position).getFilterNameResId()));
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onFilterClickedCallback(position);
                    selected = position;
                    notifyDataSetChanged();
                }
            }
        });
        if (position == selected) {
            holder.mViewSelected.setSelected(true);
        } else {
            holder.mViewSelected.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mFilterEntities == null ? 0 : mFilterEntities.size();
    }

    public void setCallback(OnFilterClickedCallback callback) {
        this.callback = callback;
    }

    public interface OnFilterClickedCallback {
        void onFilterClickedCallback(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.root_filter_item)
        public View mRoot;
        @ViewInject(R.id.img_filter_item_display)
        public ImageView mImgFilter;
        @ViewInject(R.id.text_filter_item_display)
        public TextView mTextFilter;
        @ViewInject(R.id.view_filter_selected)
        public View mViewSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            ThinkInject.bind(this, itemView);
        }
    }
}
