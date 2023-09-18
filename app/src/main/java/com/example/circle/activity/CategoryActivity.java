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
import com.example.circle.model.User;
import com.example.circle.utilities.CheckboxSelected;
import com.example.circle.utilities.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

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
    private List<CategoryModel> checkedValues = new ArrayList<>();;
    private boolean update = false;
    FirebaseDatabase database;


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

        database = FirebaseDatabase.getInstance();
        // Checks if user is already logged in or not.
      /*  mauth = FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();*/

        /*Log.v("user", "user_: " + user);
        if (user != null) { // TODO: user != null
            Intent i = getIntent();
            if (i.getExtras() == null) { // ie. user is already logged in and just opening up the app.
                Intent intent = new Intent(this, MyCommunity.class);
                //  intent.putExtra("category", sessionManager.getCategorySelected());
                Bundle args = new Bundle();
                args.putSerializable("category_list", (Serializable) sessionManager.getArrayList("my_community"));
                intent.putExtra("BUNDLE", args);

                Log.v("Chat", "categoryactv: " + sessionManager.getCategorySelected());
                startActivity(intent);
                finish();
            }
            else {  // ie. user is already logged in and wants to update the list of category again.
                update = i.getBooleanExtra("screen", false);
                Bundle args = i.getBundleExtra("BUNDLE");
                checkedValues = (List<CategoryModel>) args.getSerializable("category_list");
                Log.v("Category", "checkedvalues: " + checkedValues.size());
            }
        }*/

        Intent manage_intent = getIntent();
        update = manage_intent.getBooleanExtra("screen", false);
        Bundle manage_args = manage_intent.getBundleExtra("BUNDLE");
        if (manage_args != null)
            checkedValues = (List<CategoryModel>) manage_args.getSerializable("category_list");


        modelList = new ArrayList<>();
        modelList.add(new CategoryModel(R.drawable.sport_large_icon, "Sports and Fitness", false));
        modelList.add(new CategoryModel(R.drawable.travel_large_icon, "Travel and Adventure", false));
        modelList.add(new CategoryModel(R.drawable.friends_icon, "FRIENDS", false));
        modelList.add(new CategoryModel(R.drawable.food_new_icon, "Foodie Forever", false));
        modelList.add(new CategoryModel(R.drawable.career_icon, "Career", false));
        modelList.add(new CategoryModel(R.drawable.technology_large_icon, "Tech and Future", false));
        modelList.add(new CategoryModel(R.drawable.anime_icon, "Comics and Anime", false));
        modelList.add(new CategoryModel(R.drawable.business_large_icon, "Business and Startup", false));
        modelList.add(new CategoryModel(R.drawable.meme_icon, "Memes", false));
        modelList.add(new CategoryModel(R.drawable.stockmarket_large_icon, "Stock Market World", false));
        modelList.add(new CategoryModel(R.drawable.movies_icon, "Movies", false));
        modelList.add(new CategoryModel(R.drawable.crypto_icon, "Crypto", false));
        modelList.add(new CategoryModel(R.drawable.senior_citizen_icon, "Senior Citizens Garden", false));
        modelList.add(new CategoryModel(R.drawable.diy_icon, "DIY", false));
        modelList.add(new CategoryModel(R.drawable.fun_large_icon, "Gaming", false));
        modelList.add(new CategoryModel(R.drawable.dance_large_icon, "Dance Mania", false));
        modelList.add(new CategoryModel(R.drawable.fashion_icon, "Fashion and Lifestyle", false));
        modelList.add(new CategoryModel(R.drawable.music_large_icon, "Music and Masti", false));
        modelList.add(new CategoryModel(R.drawable.camera_large_icon, "Photography", false));

        sessionManager.saveArrayList(modelList, "modelList"); // store value

        recyclerview_category = findViewById(R.id.recyclerview_category);
        join_txtview = findViewById(R.id.join_txtview);

        if (checkedValues.size() > 0)
            adapter = new CategoryAdapter(this, modelList, checkedValues);
        else
            adapter = new CategoryAdapter(this, modelList);

        recyclerview_category.setAdapter(adapter);

        join_txtview.setOnClickListener(v -> {
            if (adapter != null) {
                checkedValues = adapter.getCheckedValues();
                Log.v("Category", "checkedvalues: " + checkedValues.size());

                if (update) { // ie. user wants to edit the category list.
                    setLoggedInUserDetails(checkedValues);
                }
                else { // ie. fresh user selecting category.
                    User user = sessionManager.getUserModel("loggedIn_UserModel");
                    sessionManager.saveArrayList(checkedValues, "my_community"); // store value

                    for (int i = 0; i < checkedValues.size(); i++) {
                        String category = checkedValues.get(i).getTitle();

                        database.getReference()
                                .child("users")
                                .child(category)
                                .child(user.getUid())
                                .setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sessionManager.setCategorySelected(category);

                                    }
                                });
                    }

                    Intent intent = new Intent(CategoryActivity.this, MyCommunity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("category_list", (Serializable) checkedValues);
                    intent.putExtra("BUNDLE", args);
                    intent.putExtra("FROM_CATEGORY_SCREEN", true);
                    startActivity(intent);
                }

            }
        });
    }

    private void setLoggedInUserDetails(List<CategoryModel> checkedValues) {
        for (int i = 0; i < checkedValues.size(); i++) {
            String category = checkedValues.get(i).getTitle();

            database.getReference()
                    .child("users")
                    .child(category)
                    .child(FirebaseAuth.getInstance().getUid())
                    .setValue(sessionManager.getUserModel("loggedIn_UserModel"))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sessionManager.saveArrayList(checkedValues, "my_community"); // store value

                            Intent intent = new Intent(CategoryActivity.this, MyCommunity.class);
                            Bundle args = new Bundle();
                            args.putSerializable("category_list", (Serializable) checkedValues);
                            intent.putExtra("BUNDLE", args);
                            startActivity(intent);
                            finish();
                        }
                    });
        }

    }

    @Override
    public void getSelectedCheckboxes(List<String> selectedList) {

    }
}