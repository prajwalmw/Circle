package com.example.circle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.circle.R;
import com.example.circle.adapter.TopStatusAdapter;
import com.example.circle.adapter.UsersAdapter;
import com.example.circle.databinding.ActivityChatUserListBinding;
import com.example.circle.model.Status;
import com.example.circle.model.User;
import com.example.circle.model.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    private User user;



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
        if (intent.getExtras() != null) {
            category_value = intent.getStringExtra("category");
            Log.v("Chat", "chatuserlist: " + category_value);
            grpchat_title = "#Circle - " + category_value;
            binding.groupChatRow.username.setText(grpchat_title);
        }

        database = FirebaseDatabase.getInstance();

        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

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

        // status - start
        statusAdapter = new TopStatusAdapter(this, userStatuses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(statusAdapter);
        // status - end

        usersAdapter = new UsersAdapter(this, users, category_value);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyclerView.setAdapter(usersAdapter);

        if (users.size() <= 0)
            binding.noData.setVisibility(View.VISIBLE);
        else
            binding.noData.setVisibility(View.GONE);

        // search view - start
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchOperation(newText);
                return true;
            }
        });
        // search view - end

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
                            String cuuid = FirebaseAuth.getInstance().getUid();
                            if (!user.getUid().equals(cuuid)) {
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

        // status - start
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userStatuses.clear();
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.addStatusBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 75);
        });
        // status - end



    }

    private void searchOperation(String newText) {
        ArrayList<User> userList = new ArrayList<>();
        userList.addAll(users);

        if (!newText.isEmpty()) {
            userList.clear();
            for (User userdata : users) {
                if (userdata.getName().toLowerCase().contains(newText.toLowerCase())) {
                    userList.add(userdata);
                    usersAdapter = new UsersAdapter(Chat_UserList.this, userList, category_value);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Chat_UserList.this);
                    layoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    binding.recyclerView.setAdapter(usersAdapter);
                }
                else {
                }
            }

            if (users.size() <= 0) {
                binding.noData.setVisibility(View.VISIBLE);
                binding.noData.setText("No user found with this name.");
            }
            else
                binding.noData.setVisibility(View.GONE);

        }
        else {
            usersAdapter = new UsersAdapter(Chat_UserList.this, users, category_value);
            LinearLayoutManager layoutManager = new LinearLayoutManager(Chat_UserList.this);
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            binding.recyclerView.setAdapter(usersAdapter);
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getName());
                                    obj.put("profileImage", userStatus.getProfileImage());
                                    obj.put("lastUpdated", userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl, userStatus.getLastUpdated());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}