package com.law.piks.edit.entity;

/**
 * Created by Law on 2016/10/24.
 */

public class FilterEntity {
    private int filterNameResId;
    private int index;
    private int displayResId;
    private boolean selected;

    public FilterEntity(int filterNameResId, int index, int displayResId, boolean selected) {
        this.filterNameResId = filterNameResId;
        this.index = index;
        this.displayResId = displayResId;
        this.selected = selected;
    }

    public int getFilterNameResId() {
        return filterNameResId;
    }

    public void setFilterNameResId(int filterNameResId) {
        this.filterNameResId = filterNameResId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDisplayResId() {
        return displayResId;
    }

    public void setDisplayResId(int displayResId) {
        this.displayResId = displayResId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
