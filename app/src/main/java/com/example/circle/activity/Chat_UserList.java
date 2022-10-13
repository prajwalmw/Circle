package com.example.circle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.circle.R;
import com.example.circle.adapter.UsersAdapter;
import com.example.circle.databinding.ActivityChatUserListBinding;
import com.example.circle.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

public class Chat_UserList extends AppCompatActivity {
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    ProgressDialog dialog;
    ActivityChatUserListBinding binding;
    static final String currentId = FirebaseAuth.getInstance().getUid();
    public static final String TAG = Chat_UserList.class.getSimpleName();
    private Intent intent;
    private String category_value, grpchat_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // changing status bar color
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        intent = getIntent();
        if (intent != null) {
            category_value = intent.getStringExtra("category");
            Log.v("Chat", "chatuserlist: " + category_value);
            grpchat_title = "#Circle - " + category_value;
            binding.groupChatRow.username.setText(grpchat_title);
        }

        database = FirebaseDatabase.getInstance();

        users = new ArrayList<>();
//        if (users.size() <= 0)
//            binding.noData.setVisibility(View.VISIBLE);
//        else
//            binding.noData.setVisibility(View.GONE);

        binding.toolbarTitle.setText(category_value + "(" + users.size() + ")");
        if (category_value.contains("Sports")) {
            binding.toolbarTitle.setTextColor(getColor(R.color.theme_red_sports));
            binding.arrowBack.getDrawable().setTint(getColor(R.color.theme_red_sports));

            binding.groupChatRow.username.setTextColor(getColor(R.color.theme_red_sports));
            binding.groupChatRow.publicChatMsg.setTextColor(getColor(R.color.theme_red_sports));

        }
        else {
            binding.toolbarTitle.setTextColor(getColor(R.color.color_primary_dark));
            binding.arrowBack.getDrawable().setTint(getColor(R.color.color_primary_dark));

            binding.groupChatRow.username.setTextColor(getColor(R.color.color_primary_dark));
            binding.groupChatRow.publicChatMsg.setTextColor(getColor(R.color.color_primary_dark));
        }

        // arrow back click
        binding.arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // options menu - start
        // Group Chat -- Public Chat
        binding.groupChatRow.grpchatParent.setOnClickListener(v -> {
            Intent intent = new Intent(Chat_UserList.this, GroupChatActivity.class);
//            intent.putExtra("name", user.getName());
//            intent.putExtra("image", user.getProfileImage());
//            intent.putExtra("uid", user.getUid());
//            intent.putExtra("token", user.getToken());
//            intent.putExtra("block", user.isIsblocked());

            intent.putExtra("name", grpchat_title);
            intent.putExtra("category", category_value);
            startActivity(intent);
        });

       /* binding.threedotsTxtview.setOnClickListener(v -> {
            if (binding.filterFramelayout.getVisibility() == View.VISIBLE)
                binding.filterFramelayout.setVisibility(View.GONE);
            else {
                binding.filterFramelayout.setVisibility(View.VISIBLE);
            }
        });

        binding.optionsMenu.subCategory.setOnClickListener(v -> {

        });

        binding.optionsMenu.createGroup.setOnClickListener(v -> {
            startActivity(new Intent(Chat_UserList.this, GroupChatActivity.class));
        });*/
        // options menu - end


        usersAdapter = new UsersAdapter(this, users, category_value);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyclerView.setAdapter(usersAdapter);

        if (users.size() <= 0)
            binding.noData.setVisibility(View.VISIBLE);
        else
            binding.noData.setVisibility(View.GONE);


        /**
         * Reading all the users that exists...and here itself checking if the user is blocked by
         * someone than that someone shouldnt show up here...
         */
        database.getReference()
                .child("users")
                .child(category_value)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            User user = snapshot1.getValue(User.class);
                            if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                                // now logic of Display only those users that are not blocked and hv not blocked me as well.

                                //2. Other user has blocked me so now he should nt be seen in my lists ie. dont add that user in my list.
                                String otherBlockMe = user.getUid() + currentId; // suffix
                                Log.v(TAG, "otherBlockMe_ID: " + otherBlockMe);

                                // now check in chat branch if this ID is present or not.
                                // read chat branch for the ID is present than read block key for true value based on this make the users list.

                                database.getReference().child("chats").child(category_value)
                                        .child(otherBlockMe).child("block").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!snapshot.exists()) {
                                                    users.add(user);
                                                    if (users.size() <= 0)
                                                        binding.noData.setVisibility(View.VISIBLE);
                                                    else
                                                        binding.noData.setVisibility(View.GONE);

                                                    binding.toolbarTitle.setText(category_value + "(" + users.size() + ")");
                                                    usersAdapter.notifyDataSetChanged();

                                                    return;
                                                }

                                                Boolean block = snapshot.getValue(Boolean.class);
                                                Log.v(TAG, "other_block_value: " + block);
                                                if (block != null) {
                                                    if (block) {
                                                        user.setIsblocked(true); // ie. this user has blocked me on his end so dont add him.
                                                    } else {
                                                        user.setIsblocked(false);
                                                        users.add(user); // ie. this user has not blocked me on his end.
                                                        if (users.size() <= 0)
                                                            binding.noData.setVisibility(View.VISIBLE);
                                                        else
                                                            binding.noData.setVisibility(View.GONE);

                                                        binding.toolbarTitle.setText(category_value + "(" + users.size() + ")");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.v(TAG, "Error of Chat is: " + error.getDetails());
                                            }
                                        });
                                //2. end


                                //1. I have blocked some user.
                                String meBlock = currentId + user.getUid(); // prefix
                                Log.v(TAG, "meBlockID: " + meBlock);

                                // now check in chat branch if this ID is present or not.
                                // read chat branch for the ID is present than read block key for true value based on this make the users list.
                                database.getReference().child("chats").child(category_value).child(meBlock).child("block").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Boolean block = snapshot.getValue(Boolean.class);
                                        Log.v(TAG, "block_value: " + block);
                                        if (block != null) {
                                            if (block) {
                                                user.setIsblocked(true);
                                            } else {
                                                user.setIsblocked(false);
                                            }
                                        }

                                        usersAdapter.notifyDataSetChanged(); // TODO: need to add later.
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.v(TAG, "Error of Chat is: " + error.getDetails());
                                    }
                                });
                                //1. end


//                        users.add(user); // TODO: need to add later.
//                        usersAdapter.notifyDataSetChanged(); // TODO: need to add later.
                            }
                        }
                        // binding.recyclerView.hideShimmerAdapter();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    public void onPause() {
        super.onPause();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}