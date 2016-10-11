package com.law.piks.widget.popmenu;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.law.piks.R;
import com.law.think.frame.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jungle on 16/10/3.
 */

public class PopMenu extends PopupWindow {
    private Context mContext;
    private List<MenuItem> mItems;
    private OnItemClick mOnItemClick;
    private LinearLayout mLinearLayoutMenu;

    public PopMenu(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        View menuView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_gallery_menu, null);
        menuView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        mLinearLayoutMenu = (LinearLayout) menuView.findViewById(R.id.layout_gallery_menu);
        mItems = new ArrayList<>();
        mLinearLayoutMenu.removeAllViews();
        if (mItems != null && mItems.size() > 0) {
            for (int i = 0, count = mItems.size(); i < count; i++) {
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_popupmenu_item, null);
                TextView itemText = (TextView) itemView.findViewById(R.id.text_menu_item_name);
                ImageView itemImg = (ImageView) itemView.findViewById(R.id.img_menu_item);
                itemText.setText(mItems.get(i).getItemDisplay());
                if (mItems.get(i).getDrawableId() > 0) {
                    itemImg.setImageResource(mItems.get(i).getDrawableId());
                } else {
                    itemImg.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new MenuItemClickListener(i));
                mLinearLayoutMenu.addView(itemView);
                if (i != count - 1) {
                    mLinearLayoutMenu.addView(getDivider());
                }
            }
        }
        setWidth((int) (ScreenUtils.getScreenWidth(mContext) * 0.4f));
        setContentView(menuView);
        setFocusable(true);
        setOutsideTouchable(true);
    }

    public void setMenu(List<MenuItem> items) {
        mItems.addAll(items);
        mLinearLayoutMenu.removeAllViews();
        if (mItems != null && mItems.size() > 0) {
            for (int i = 0, count = mItems.size(); i < count; i++) {
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_popupmenu_item, null);
                TextView itemText = (TextView) itemView.findViewById(R.id.text_menu_item_name);
                ImageView itemImg = (ImageView) itemView.findViewById(R.id.img_menu_item);
                itemText.setText(mItems.get(i).getItemDisplay());
                if (mItems.get(i).getDrawableId() > 0) {
                    itemImg.setImageResource(mItems.get(i).getDrawableId());
                } else {
                    itemImg.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new MenuItemClickListener(i));
                mLinearLayoutMenu.addView(itemView);
                if (i != count - 1) {
                    mLinearLayoutMenu.addView(getDivider());
                }
            }
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.mOnItemClick = onItemClick;
    }

    private View getDivider() {
        View divider = new View(mContext);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
        return divider;
    }

    public interface OnItemClick {
        void onItemClick(int position);
    }

    private class MenuItemClickListener implements View.OnClickListener {
        private int position;

        public MenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClick != null) {
                mOnItemClick.onItemClick(position);
            }
        }
    }
}
