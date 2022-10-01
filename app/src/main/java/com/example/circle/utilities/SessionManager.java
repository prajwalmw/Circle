package com.example.circle.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Circle";
    private static final String CATEGORY_SELECTED = "CATEGORY_SELECTED";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getCategorySelected() {
        return pref.getString(CATEGORY_SELECTED, "");
    }

    public void setCategorySelected(String categorySelected) {
        editor.putString(CATEGORY_SELECTED, categorySelected);
        editor.commit();
    }

}
