package com.law.piks.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.widget.popmenu.MenuItem;
import com.law.think.frame.inject.ThinkInject;
import com.law.think.frame.inject.annotation.ViewInject;

import java.util.List;

/**
 * Created by Jungle on 16/10/1.
 */

public class MenuAdapter extends BaseAdapter {
    private Context mContext;
    private List<MenuItem> mItems;

    public MenuAdapter(Context context, List<MenuItem> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems == null ? null : mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder mHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_popupmenu_item, null);
            mHolder = new ViewHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) view.getTag();
        }
        mHolder.mItemName.setText(mItems.get(i).getItemDisplay());
        return view;
    }

    private class ViewHolder {
        @ViewInject(R.id.text_menu_item_name)
        public TextView mItemName;
        @ViewInject(R.id.root_menu_item)
        public View rootView;

        public ViewHolder(View view) {
            ThinkInject.bind(this, view);
        }
    }

}
