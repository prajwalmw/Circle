package com.example.circle.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.circle.adapter.MyCommunityAdapter;
import com.example.circle.databinding.ActivityMyCommunityBinding;
import com.example.circle.model.CategoryModel;

import java.util.List;

public class MyCommunity extends AppCompatActivity {
    private List<CategoryModel> categoryList;
    private Intent intent;
    private ActivityMyCommunityBinding binding;
    private MyCommunityAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // changing status bar color
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        intent = getIntent();
        if (intent != null) {
            Bundle args = intent.getBundleExtra("BUNDLE");
            categoryList = (List<CategoryModel>) args.getSerializable("category_list");
            Log.v("Category", "checkedvalues: " + categoryList.size());
        }

        adapter = new MyCommunityAdapter(this, categoryList);
        binding.recyclerviewCategory.setAdapter(adapter);

    }
}