package com.example.circle.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.circle.R;
import com.example.circle.adapter.CategoryAdapter;
import com.example.circle.model.CategoryModel;
import com.example.circle.utilities.CheckboxSelected;
import com.example.circle.utilities.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements CheckboxSelected, Serializable {
    RecyclerView recyclerview_category;
    CategoryAdapter adapter;
    private List<CategoryModel> modelList;
    private FirebaseAuth mauth;
    private SessionManager sessionManager;
    private TextView join_txtview;
    private List<CategoryModel> checkedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // changing status bar color
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        setTitle("Select your Category");
        sessionManager = new SessionManager(this);
        // Checks if user is already logged in or not.
        mauth = FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();

        Log.v("user", "user_: " + user);
        if (user != null) { // TODO: user != null
            Intent intent = new Intent(this, MyCommunity.class);
          //  intent.putExtra("category", sessionManager.getCategorySelected());
            Bundle args = new Bundle();
            args.putSerializable("category_list", (Serializable) sessionManager.getArrayList("my_community"));
            intent.putExtra("BUNDLE",args);

            Log.v("Chat", "categoryactv: " + sessionManager.getCategorySelected());
            startActivity(intent);
            finish();
        }
        else {
            //  startActivity(new Intent(ProfileOTP_Login.this, UserSetupScreen.class));
        }

        modelList = new ArrayList<>();
        modelList.add(new CategoryModel(R.drawable.sport_large_icon, "Sports and Fitness"));
        modelList.add(new CategoryModel(R.drawable.travel_large_icon, "Travel and Adventure"));
        modelList.add(new CategoryModel(R.drawable.food_large_icon, "Food Enthusiast"));
        modelList.add(new CategoryModel(R.drawable.technology_large_icon, "Technology and Future"));
        modelList.add(new CategoryModel(R.drawable.business_large_icon, "Business and Startup"));
        modelList.add(new CategoryModel(R.drawable.stockmarket_large_icon, "Stock Market World"));
        modelList.add(new CategoryModel(R.drawable.senior_citizen_icon, "Senior Citizens Garden"));
        modelList.add(new CategoryModel(R.drawable.fun_large_icon, "Fun and Games"));
        modelList.add(new CategoryModel(R.drawable.dance_large_icon, "Dance Mania"));
        modelList.add(new CategoryModel(R.drawable.music_large_icon, "Music and Masti"));
        modelList.add(new CategoryModel(R.drawable.camera_large_icon, "Photography and Life"));

        recyclerview_category = findViewById(R.id.recyclerview_category);
        join_txtview = findViewById(R.id.join_txtview);
        adapter = new CategoryAdapter(this, modelList);
        recyclerview_category.setAdapter(adapter);

        join_txtview.setOnClickListener(v -> {
            if (adapter != null) {
                checkedValues = new ArrayList<>();
                checkedValues = adapter.getCheckedValues();
                Log.v("Category", "checkedvalues: " + checkedValues.size());

                Intent intent = new Intent(CategoryActivity.this, ProfileOTP_Login.class);
                Bundle args = new Bundle();
                args.putSerializable("category_list", (Serializable) checkedValues);
                intent.putExtra("BUNDLE",args);
                startActivity(intent);

            }

        });

    }

    @Override
    public void getSelectedCheckboxes(List<String> selectedList) {

    }
}