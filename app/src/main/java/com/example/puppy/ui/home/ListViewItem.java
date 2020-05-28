package com.example.puppy.ui.home;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable iconDrawable ;
    private String titleStr ;
    private int ageInt ;

    public void setIcon(Drawable icon) { iconDrawable = icon ; }
    public void setTitle(String title) { titleStr = title ; }
    public void setAge(int age) { ageInt = age ; }

    public Drawable getIcon() { return this.iconDrawable ; }
    public String getTitle() { return this.titleStr ; }
    public int getAge() { return this.ageInt ; }
}
