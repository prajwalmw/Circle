package com.example.circle.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class CategoryModel implements Serializable{
    private int icon;
    private String title;
    private int items_count;

    public CategoryModel() {
    }

    public CategoryModel(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public CategoryModel(int icon, String title, int items_count) {
        this.icon = icon;
        this.title = title;
        this.items_count = items_count;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItems_count() {
        return items_count;
    }

    public void setItems_count(int items_count) {
        this.items_count = items_count;
    }
}
