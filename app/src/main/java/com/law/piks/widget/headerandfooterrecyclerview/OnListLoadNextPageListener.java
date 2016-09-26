package com.law.piks.widget.headerandfooterrecyclerview;

import android.view.View;

/**
 * RecyclerView/ListView/GridView callback interface for scroll load next page
 */
public interface OnListLoadNextPageListener {

    /**
     * 开始加载下一页
     *
     * @param view 当前RecyclerView/ListView/GridView
     */
    public void onLoadNextPage(View view);
}
