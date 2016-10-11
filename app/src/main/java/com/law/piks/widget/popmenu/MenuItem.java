package com.law.piks.widget.popmenu;

/**
 * Created by Jungle on 16/10/3.
 */

public class MenuItem {
    private String itemDisplay;
    private int drawableId;

    public MenuItem(String itemDisplay) {
        this.itemDisplay = itemDisplay;
    }

    public MenuItem(String itemDisplay, int drawableId) {
        this.itemDisplay = itemDisplay;
        this.drawableId = drawableId;
    }

    public String getItemDisplay() {
        return itemDisplay;
    }

    public void setItemDisplay(String itemDisplay) {
        this.itemDisplay = itemDisplay;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
