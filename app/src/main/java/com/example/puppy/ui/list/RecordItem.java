package com.example.puppy.ui.list;

import android.graphics.drawable.Drawable;

public class RecordItem {

    private String item_Id;

    private String date;

    private String stat;
    private String lv;

    public RecordItem(){}

    public RecordItem(String date, String stat, String lv, String item_Id){
        this.date = date;
        this.stat = stat;
        this.lv = lv;
        this.item_Id = item_Id;
    }

    public String getItem_Id() {
        return item_Id;
    }

    public void setItem_Id(String item_Id) {
        this.item_Id = item_Id;
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