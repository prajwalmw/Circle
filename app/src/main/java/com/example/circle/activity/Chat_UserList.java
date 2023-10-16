package com.example.circle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.circle.R;
import com.example.circle.adapter.ContentAdapter;
import com.example.circle.adapter.TopStatusAdapter;
import com.example.circle.adapter.UsersAdapter;
import com.example.circle.databinding.ActivityChatUserListBinding;
import com.example.circle.model.ContentModel;
import com.example.circle.model.Status;
import com.example.circle.model.User;
import com.example.circle.model.UserStatus;
import com.example.circle.utilities.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Chat_UserList extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference reference;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    ProgressDialog dialog;
    ActivityChatUserListBinding binding;
    static final String currentId = FirebaseAuth.getInstance().getUid();
    public static final String TAG = Chat_UserList.class.getSimpleName();
    private Intent intent;
    private String category_value, grpchat_title;
    TopStatusAdapter statusAdapter;
    SessionManager sessionManager;
    
    // content
    private ContentAdapter contentAdapter;
    private ArrayList<ContentModel> contentList;
    
    ArrayList<UserStatus> userStatuses;
    private User user;
    public static final int STATUS_CAPTURE = 75;
    public static final int POST_CAPTURE = 99;
    public static final int SHARE_REQUEST_CODE = 98;
    private Context context;


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

        context = Chat_UserList.this;

        intent = getIntent();
        if (intent.getExtras() != null) {
            category_value = intent.getStringExtra("category");
            Log.v("Chat", "chatuserlist: " + category_value);
            grpchat_title = "#Circle - " + category_value;
            binding.groupChatRow.username.setText(grpchat_title);
        }

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        sessionManager = new SessionManager(this);

        users = new ArrayList<>();
        userStatuses = new ArrayList<>();
        contentList = new ArrayList<>();

        if (binding.recentChip.isChecked()) {
            binding.recentChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.color_primary_light)));
        }
        binding.chipGrp.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, int checkedId) {
                if (group.getCheckedChipId() == R.id.trendingChip) {
                    binding.recentChip.setChecked(false);
                    binding.trendingChip.setChecked(true);
                    if (binding.trendingChip.isChecked()) {
                        binding.trendingChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.color_primary_light)));
                        binding.recentChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        sortByTrending();
                        if (contentAdapter != null)
                            contentAdapter.notifyDataSetChanged();
                    }
                }
                else if (group.getCheckedChipId() == R.id.recentChip){
                    binding.recentChip.setChecked(true);
                    binding.trendingChip.setChecked(false);
                    if (binding.recentChip.isChecked()) {
                        binding.recentChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.color_primary_light)));
                        binding.trendingChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        sortByRecent();
                        if (contentAdapter != null)
                            contentAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    database.getReference()
                .child("post")
                .child(category_value)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    contentList.clear();
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                        /*UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));*/   // todo: 8th aug check later if requir uncomment.

//                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storySnapshot.child("imagesPath").getChildren()) {
                           // Status sampleStatus = statusSnapshot.getValue(Status.class);
                            ContentModel contentModel = statusSnapshot.getValue(ContentModel.class);
                            contentList.add(0, contentModel);
                        }

                      //  status.setStatuses(statuses);
                      //  userStatuses.add(status);
                    }

//                    Collections.sort(contentList, new Comparator<ContentModel>() {
//                        @Override
//                        public int compare(ContentModel model_1, ContentModel model_2) {
//                            return Integer.compare(model_2.getContentHeartCount(), model_1.getContentHeartCount());
//                        }
//                    });

                    sortByRecent();
                    if (contentAdapter != null)
                        contentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        // content - start

        contentAdapter = new ContentAdapter(this, contentList, new ContentAdapter.OnItemClick() {
            @Override
            public void onclick(String view, boolean isLiked, ContentModel contentModel, int position) {
                if (view.equalsIgnoreCase("like_btn") || view.equalsIgnoreCase("content_imageview"))
                    plus_minus_heartCount(isLiked, contentModel);

                if (view.equalsIgnoreCase("item")) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateViewsDBCount(contentModel);
                        }
                    }, 180000); // 5 mins.

                }
            }

            @Override
            public void onProfileClick(String profileImg, String username, String description, String instagram, String youtube) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.contentRecycler.setLayoutManager(layoutManager);
        binding.contentRecycler.setAdapter(contentAdapter);
        // content - end
        
        // status - start
        statusAdapter = new TopStatusAdapter(this, userStatuses);
        layoutManager = new LinearLayoutManager(this);
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
            startActivityForResult(intent, STATUS_CAPTURE);
        });
        // status - end

        binding.captureImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, POST_CAPTURE);
        });

    }

    private void viewCounter() {

        contentAdapter.notifyDataSetChanged();
    }

    private void sortByTrending() {
        Collections.sort(contentList, new Comparator<ContentModel>() {
            @Override
            public int compare(ContentModel model_1, ContentModel model_2) {
                return Integer.compare(model_2.getContentHeartCount(), model_1.getContentHeartCount());
            }
        });
    }

    private void sortByRecent() {
        Collections.sort(contentList, new Comparator<ContentModel>() {
            @Override
            public int compare(ContentModel model_1, ContentModel model_2) {
                return Long.compare(model_2.getLastUpdatedAt(), model_1.getLastUpdatedAt());
            }
        });
    }

    private void plus_minus_heartCount(boolean isLiked, ContentModel contentModel) {

        if (isLiked) {
            updateHeartDBCount(contentModel, isLiked);
        }
        else {
            updateHeartDBCount(contentModel, isLiked);
        }

        contentAdapter.notifyDataSetChanged();


    }

    private void updateHeartDBCount(ContentModel contentModel, boolean isLiked) {
        database.getReference("post")
                .child(category_value)
                .child(contentModel.getUserID())
                .child("imagesPath")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        for (DataSnapshot data : snapshot1.getChildren()) {
                            ContentModel mod = data.getValue(ContentModel.class);

                            if (mod.getUuid().equalsIgnoreCase(contentModel.getUuid())) {  // this makes sure that only the clicked item's heart count is updated.

                                // Automic Server-Side Increments for Like/Unlike post.
                                HashMap<String, Object> obj = new HashMap<>();
                                User user = sessionManager.getUserModel("loggedIn_UserModel");

                                List<String> likedList;
                                if (mod.getLikedBy() != null)
                                     likedList = mod.getLikedBy();
                                else
                                    likedList = new ArrayList<>();

                                if (likedList.contains(user.getUid())) { // ie. already liked by this user.
                                    obj.put("contentHeartCount", ServerValue.increment(-1));   // than decrement like count as already liked.
                                    likedList.remove(user.getUid());  // ie. since unliked than remove userId from list.
                                    obj.put("likedBy", likedList);
                                }
                                else {
                                    obj.put("contentHeartCount", ServerValue.increment(1));   // mod
                                    likedList.add(user.getUid()); // ie. since liked than add this userId in arraylist.
                                    obj.put("likedBy", likedList);
                                }

                                database.getReference()
                                        .child("post")
                                        .child(category_value)
                                        .child(contentModel.getUserID())
                                        .child("imagesPath")
                                        .child(data.getKey())
                                        .updateChildren(obj);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void updateViewsDBCount(ContentModel contentModel) {
        database.getReference("post")
                .child(category_value)
                .child(contentModel.getUserID())
                .child("imagesPath")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        for (DataSnapshot data : snapshot1.getChildren()) {
                            ContentModel mod = data.getValue(ContentModel.class);

                            if (mod.getUuid().equalsIgnoreCase(contentModel.getUuid())) {  // this makes sure that only the clicked item's heart count is updated.

                                // Automic Server-Side Increments for Like/Unlike post.
                                HashMap<String, Object> obj = new HashMap<>();
                                User user = sessionManager.getUserModel("loggedIn_UserModel");

                                obj.put("contentViewCount", ServerValue.increment(1));   // mod
                                database.getReference()
                                        .child("post")
                                        .child(category_value)
                                        .child(contentModel.getUserID())
                                        .child("imagesPath")
                                        .child(data.getKey())
                                        .updateChildren(obj);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
              //  dialog.show();

                if (requestCode == SHARE_REQUEST_CODE) {
                    boolean success = data.getBooleanExtra("SUCCESS", false);
                    if (success)
                        Toast.makeText(this, "Post uploaded successfully!", Toast.LENGTH_SHORT).show();
                }

                if (requestCode == STATUS_CAPTURE && resultCode == RESULT_OK) {
                    Uri d = data.getData();
                    Log.d(TAG, "onActivityResult: status capture " + d);
                    Intent i = new Intent(new Intent(Chat_UserList.this, PostDetailsActivity.class));
                    i.putExtra("URI", data.getData().toString());
                    i.putExtra("CAPTURE_REQUEST_CODE", STATUS_CAPTURE);
                    i.putExtra("category_value", category_value);
                    startActivityForResult(i, SHARE_REQUEST_CODE);
                }
                else if (requestCode == POST_CAPTURE && resultCode == RESULT_OK) {
                    Uri d = data.getData();
                    Log.d(TAG, "onActivityResult: status capture " + d);
                    Intent i = new Intent(new Intent(Chat_UserList.this, PostDetailsActivity.class));
                    i.putExtra("URI", data.getData().toString());
                    i.putExtra("CAPTURE_REQUEST_CODE", POST_CAPTURE);
                    i.putExtra("category_value", category_value);
                    startActivityForResult(i, SHARE_REQUEST_CODE);
                }

            }
            //
        }
    }

/*
    private void image_upload(@NonNull Intent data, int requestCode) {
      //  FirebaseStorage storage = FirebaseStorage.getInstance();
        Date date = new Date();

        if (requestCode == STATUS_CAPTURE)
            reference = storage.getReference().child("status").child(date.getTime() + "");
        else if (requestCode == POST_CAPTURE)
            reference = storage.getReference().child("post").child(category_value).child(date.getTime() + "");
        reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // userslist has all the userslist now check the current userid if it is present in the userslist.
                            User user = sessionManager.getUserModel("loggedIn_UserModel");
                            UserStatus userStatus = new UserStatus();
                            userStatus.setName(user.getName());
                            userStatus.setProfileImage(user.getProfileImage());
                            userStatus.setLastUpdated(date.getTime());

                            HashMap<String, Object> obj = new HashMap<>();
                            obj.put("name", userStatus.getName());
                            obj.put("profileImage", userStatus.getProfileImage());
                            obj.put("lastUpdated", userStatus.getLastUpdated());

                            String imageUrl = uri.toString();
                          //  Status status = new Status(imageUrl, userStatus.getLastUpdated());
                            ContentModel contentModel = new ContentModel(UUID.randomUUID().toString(), imageUrl, "This is first image", "100 Likes");

                            if (requestCode == STATUS_CAPTURE) {
                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(obj);

                                database.getReference().child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("imagesPath")
                                        .push()
                                        .setValue(contentModel);
                            }
                            else if (requestCode == POST_CAPTURE) {
                                database.getReference()
                                        .child("post")
                                        .child(category_value)
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(obj);

                                database.getReference()
                                        .child("post")
                                        .child(category_value)
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("imagesPath")
                                        .push()
                                        .setValue(contentModel);

                            }

                          //  dialog.dismiss();
                        }
                    });
                }
            }
        });
    }
*/
}