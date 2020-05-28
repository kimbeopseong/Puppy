package com.example.puppy.ui.list;

import android.graphics.drawable.Drawable;

public class ListViewItem {

    private Drawable icon;
    private String date;
    private String stat;
    private String lv;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getLv() {
        return lv;
    }

    public void setLv(String lv) {
        this.lv = lv;
    }
}