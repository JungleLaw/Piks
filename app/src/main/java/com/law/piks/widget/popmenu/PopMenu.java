package com.law.piks.widget.popmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.law.piks.R;
import com.law.piks.home.adapter.MenuAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jungle on 16/10/3.
 */

public class PopMenu extends PopupWindow {
    private Context mContext;
    private ListView mPopupMenuListView;
    private List<MenuItem> mItems;
    private MenuAdapter mMenuAdapter;
    private OnItemClick mOnItemClick;

    public PopMenu(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        View menuView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_gallery_menu, null);
        menuView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        mPopupMenuListView = (ListView) menuView.findViewById(R.id.list_gallery_menu);
        mItems = new ArrayList<>();
        mMenuAdapter = new MenuAdapter(mContext, mItems);
        mPopupMenuListView.setAdapter(mMenuAdapter);
        setWidth(500);
        setContentView(menuView);
//            setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        mPopupMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mOnItemClick != null) {
                    mOnItemClick.onItemClick(i);
                }
            }
        });
    }

    public void setMenu(List<MenuItem> items) {
//        MenuAdapter menuAdapter = new MenuAdapter(mContext, items);
////            mPopupMenu.setAdapter(menuAdapter);
//        mPopupMenuListView.setAdapter(menuAdapter);
        mItems.addAll(items);
        mMenuAdapter.notifyDataSetChanged();
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.mOnItemClick = onItemClick;
    }

    public interface OnItemClick {
        public void onItemClick(int position);
    }
}
