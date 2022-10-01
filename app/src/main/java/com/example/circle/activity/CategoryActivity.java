package com.example.circle.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.circle.R;
import com.example.circle.adapter.CategoryAdapter;
import com.example.circle.model.CategoryModel;
import com.example.circle.utilities.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView recyclerview_category;
    CategoryAdapter adapter;
    private List<CategoryModel> modelList;
    private FirebaseAuth mauth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        setTitle("Select your Category");
        sessionManager = new SessionManager(this);
        // Checks if user is already logged in or not.
        mauth = FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();
        Log.v("user", "user_: " + user);
        if (user != null) { // TODO: user != null
            Intent intent = new Intent(this, Chat_UserList.class);
            intent.putExtra("category", sessionManager.getCategorySelected());
            Log.v("Chat", "categoryactv: " + sessionManager.getCategorySelected());
            startActivity(intent);
            finish();
        }
        else {
            //  startActivity(new Intent(ProfileOTP_Login.this, UserSetupScreen.class));
        }

        modelList = new ArrayList<>();
        modelList.add(new CategoryModel(getDrawable(R.drawable.sport_large_icon), "Sports and Fitness"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.travel_large_icon), "Travel and Adventure"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.food_large_icon), "Food Enthusiast"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.technology_large_icon), "Technology and Future"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.business_large_icon), "Business and Startup"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.stockmarket_large_icon), "Stock Market World"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.fun_large_icon), "Fun and Games"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.dance_large_icon), "Dance Mania"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.music_large_icon), "Music and Masti"));
        modelList.add(new CategoryModel(getDrawable(R.drawable.camera_large_icon), "Photography and Life"));

        recyclerview_category = findViewById(R.id.recyclerview_category);
        adapter = new CategoryAdapter(this, modelList);
        recyclerview_category.setAdapter(adapter);
    }
}