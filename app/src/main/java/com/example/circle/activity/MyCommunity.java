package com.example.circle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.circle.R;
import com.example.circle.adapter.MyCommunityAdapter;
import com.example.circle.databinding.ActivityMyCommunityBinding;
import com.example.circle.model.CategoryModel;
import com.example.circle.utilities.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class MyCommunity extends AppCompatActivity {
    private List<CategoryModel> categoryList;
    private Intent intent;
    private ActivityMyCommunityBinding binding;
    private MyCommunityAdapter adapter;
    FirebaseDatabase database;
    private SessionManager sessionManager;


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
        sessionManager = new SessionManager(this);

        intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle args = intent.getBundleExtra("BUNDLE");
            categoryList = (List<CategoryModel>) args.getSerializable("category_list");
        }

        // bottom nav bar - start
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_status)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.bottomnavbar, navController);
        // bottom nav bar - end

       /* Glide.with(this).load(sessionManager.getUserModel("loggedIn_UserModel").getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(binding.profileImgIcon);*/

        if (categoryList == null) {
            startActivity(new Intent(this, CategoryActivity.class));

            if (sessionManager.getArrayList("my_community") != null) {
                categoryList = sessionManager.getArrayList("my_community");
            }
        }
        else
            adapter = new MyCommunityAdapter(this, categoryList);

     //   binding.recyclerviewCategory.setAdapter(adapter);

/*
        binding.manageList.setOnClickListener(v -> {
            Intent intent = new Intent(MyCommunity.this, CategoryActivity.class);
            intent.putExtra("screen", true);

            Bundle args = new Bundle();
            args.putSerializable("category_list", (Serializable) categoryList);
            intent.putExtra("BUNDLE", args);

            startActivity(intent);
        });
*/

      /*  binding.threedotsTxtview.setOnClickListener(v -> {
            if (binding.filterFramelayout.getVisibility() == View.VISIBLE)
                binding.filterFramelayout.setVisibility(View.GONE);
            else {
                binding.filterFramelayout.setVisibility(View.VISIBLE);
            }
        });

        binding.optionsMenu.subCategory.setOnClickListener(v -> {

        });

        binding.optionsMenu.createGroup.setOnClickListener(v -> {
            startActivity(new Intent(MyCommunity.this, GroupChatActivity.class));
        });
*/

        if (categoryList != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                String category_title = categoryList.get(i).getTitle();
                database = FirebaseDatabase.getInstance();
                /**
                 * need to upate token else notific wont show up as it requires token.
                 */
                FirebaseMessaging.getInstance()
                        .getToken()
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String token) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("token", token);
                                database.getReference()
                                        .child("users")
                                        .child(category_title)
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(map);
                                //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}