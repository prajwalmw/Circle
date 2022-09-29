package com.example.circle.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.circle.R;
import com.example.circle.adapter.CategoryAdapter;
import com.example.circle.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView recyclerview_category;
    CategoryAdapter adapter;
    private List<CategoryModel> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        modelList = new ArrayList<>();
        modelList.add(new CategoryModel(getDrawable(R.drawable.sport_icon), "Sports"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.travel_icon), "Travel"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.food_icon), "Food"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.movies), "Movies"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.movies), "Movies"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.movies), "Movies"));

        recyclerview_category = findViewById(R.id.recyclerview_category);
        adapter = new CategoryAdapter(this, modelList);
        recyclerview_category.setAdapter(adapter);
    }
}