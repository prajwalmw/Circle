package com.circle.community.model;

public class ListItemModel {
    private int icon;
    private String optionName;

    public ListItemModel(int icon, String optionName) {
        this.icon = icon;
        this.optionName = optionName;
    }

    public ListItemModel(String optionName) {
        this.optionName = optionName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
