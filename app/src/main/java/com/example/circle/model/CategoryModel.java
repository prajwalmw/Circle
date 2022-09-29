package com.example.circle.model;

import android.graphics.drawable.Drawable;

public class CategoryModel {
    private Drawable icon;
    private String title;

    public CategoryModel(Drawable icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
