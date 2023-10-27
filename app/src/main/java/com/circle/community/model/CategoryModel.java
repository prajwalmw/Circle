package com.circle.community.model;

import java.io.Serializable;

public class CategoryModel implements Serializable{
    private int icon;
    private String title;
    private boolean isChecked;
    public CategoryModel() {
    }

    public CategoryModel(int icon, String title, boolean isChecked) {
        this.icon = icon;
        this.title = title;
        this.isChecked = isChecked;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
