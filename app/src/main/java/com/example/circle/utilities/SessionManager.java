package com.example.circle.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.circle.model.CategoryModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

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

    public void saveArrayList(List<CategoryModel> list, String key){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    public List<CategoryModel> getArrayList(String key){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Type type = new TypeToken<List<CategoryModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
