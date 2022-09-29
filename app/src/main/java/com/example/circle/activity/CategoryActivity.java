package com.example.circle.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.circle.R;
import com.example.circle.adapter.CategoryAdapter;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView recyclerview_category;
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerview_category = findViewById(R.id.recyclerview_category);
        adapter = new CategoryAdapter(this);
        recyclerview_category.setAdapter(adapter);
    }
}