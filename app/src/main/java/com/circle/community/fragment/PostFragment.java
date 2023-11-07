package com.circle.community.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.circle.community.R;
import com.circle.community.activity.Chat_UserList;
import com.circle.community.activity.PostDetailsActivity;
import com.circle.community.adapter.ContentAdapter;
import com.circle.community.adapter.TopStatusAdapter;
import com.circle.community.adapter.UsersAdapter;
import com.circle.community.databinding.FragmentPostBinding;
import com.circle.community.model.ContentModel;
import com.circle.community.model.Status;
import com.circle.community.model.User;
import com.circle.community.model.UserStatus;
import com.circle.community.utilities.SessionManager;
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


public class PostFragment extends Fragment {

   private static final String ARG_PARAM1 = "key";
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference reference;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    ProgressDialog dialog;
    private FragmentPostBinding binding;
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
    
    public PostFragment() {
        // Required empty public constructor
    }

/*
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        binding = FragmentPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setStatusBarColor();

        if (getArguments() != null) {
            category_value = getArguments().getString(ARG_PARAM1);
            codeLogic();
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void codeLogic() {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        sessionManager = new SessionManager(getActivity());

        users = new ArrayList<>();
        userStatuses = new ArrayList<>();
        contentList = new ArrayList<>();

        binding.captureImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, POST_CAPTURE);
        });

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
                                
                                for(DataSnapshot statusSnapshot : storySnapshot.child("imagesPath").getChildren()) {
                                    // Status sampleStatus = statusSnapshot.getValue(Status.class);
                                    ContentModel contentModel = statusSnapshot.getValue(ContentModel.class);
                                    contentList.add(0, contentModel);
                                }
                            }
                            
                            sortByRecent();
                            if (contentAdapter != null)
                                contentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error);
                    }
                });

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

        binding.toolbarTitle.setText(category_value + "(" + users.size() + ")");
        if (category_value.contains("Sports")) {
            binding.toolbarTitle.setTextColor(getActivity().getColor(R.color.theme_red_sports));
            binding.arrowBack.getDrawable().setTint(getActivity().getColor(R.color.theme_red_sports));

            binding.groupChatRow.username.setTextColor(getActivity().getColor(R.color.theme_red_sports));
            binding.groupChatRow.publicChatMsg.setTextColor(getActivity().getColor(R.color.theme_red_sports));

        }
        else {
            binding.toolbarTitle.setTextColor(getActivity().getColor(R.color.color_primary_dark));
            binding.arrowBack.getDrawable().setTint(getActivity().getColor(R.color.color_primary_dark));

            binding.groupChatRow.username.setTextColor(getActivity().getColor(R.color.color_primary_dark));
            binding.groupChatRow.publicChatMsg.setTextColor(getActivity().getColor(R.color.color_primary_dark));
        }
        
        //
        contentAdapter = new ContentAdapter(getActivity(), contentList, new ContentAdapter.OnItemClick() {
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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                View view = getLayoutInflater().inflate(R.layout.profile_ui, null);
                builder.setView(view);

                ImageButton manage_list = view.findViewById(R.id.manage_list);
                manage_list.setVisibility(View.GONE);

                ImageButton instaBtn = view.findViewById(R.id.instagramBtn);
                ImageButton youtubeBtn = view.findViewById(R.id.youtubeBtn);

                // instagram and youtube btn - START
               /* if (!instagram.isEmpty())
                    instaBtn.setVisibility(View.VISIBLE);
                else
                    instaBtn.setVisibility(View.GONE);

                if (!youtube.isEmpty())
                    youtubeBtn.setVisibility(View.VISIBLE);
                else
                    youtubeBtn.setVisibility(View.GONE);*/

                instaBtn.setOnClickListener(v -> {
                    String url = "";
                    if (instagram.isEmpty())
                        url = "https://instagram.com/_u/circlecommunity2023";
                    else
                        url = "https://instagram.com/_u/" +  instagram;

                    Uri uri = Uri.parse(url);
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                    likeIng.setPackage("com.instagram.android");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                    }
                });

                youtubeBtn.setOnClickListener(v -> {
                    Uri url = Uri.parse(youtube);
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, url);
                    likeIng.setPackage("com.google.android.youtube");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                    }
                });

                ImageView img = view.findViewById(R.id.profile_img_icon);
                Glide.with(getActivity())
                        .asBitmap()
                        .load(profileImg)
                        .placeholder(R.drawable.avatar)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .circleCrop()
                        .into(img);

                TextView userName = view.findViewById(R.id.userNameTxt);
                userName.setText(username);

                TextView desc = view.findViewById(R.id.aboutMeTxt);
                if (!description.isEmpty())
                    desc.setText(description);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.contentRecycler.setLayoutManager(layoutManager);
        binding.contentRecycler.setAdapter(contentAdapter);
        // content - end

        // status - start
        statusAdapter = new TopStatusAdapter(getActivity(), userStatuses);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(statusAdapter);
        // status - end

        usersAdapter = new UsersAdapter(getActivity(), users, category_value);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyclerView.setAdapter(usersAdapter);

//        if (users.size() <= 0)
//            binding.noData.setVisibility(View.VISIBLE);
//        else
//            binding.noData.setVisibility(View.GONE);


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
                            if (user != null && user.getUid() != null && !user.getUid().equals(cuuid)) {
                                // now logic of Display only those users that are not blocked and hv not blocked me as well.

                                //2. Other user has blocked me so now he should nt be seen in my lists ie. dont add that user in my list.
                                String otherBlockMe = user.getUid() + currentId; // suffix
                                Log.v(TAG, "otherBlockMe_ID: " + otherBlockMe);

                                // now check in chat branch if getActivity() ID is present or not.
                                // read chat branch for the ID is present than read block key for true value based on getActivity() make the users list.

                                database.getReference().child("chats").child(category_value)
                                        .child(otherBlockMe).child("block").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!snapshot.exists()) {
                                                    users.add(user);
                                                   /* if (users.size() <= 0)
                                                        binding.noData.setVisibility(View.VISIBLE);
                                                    else
                                                        binding.noData.setVisibility(View.GONE);*/

                                                    binding.toolbarTitle.setText(category_value + "(" + users.size() + ")");
                                                    usersAdapter.notifyDataSetChanged();

                                                    return;
                                                }

                                                Boolean block = snapshot.getValue(Boolean.class);
                                                Log.v(TAG, "other_block_value: " + block);
                                                if (block != null) {
                                                    if (block) {
                                                        user.setIsblocked(true); // ie. getActivity() user has blocked me on his end so dont add him.
                                                    } else {
                                                        user.setIsblocked(false);
                                                        users.add(user); // ie. getActivity() user has not blocked me on his end.
                                                      /*  if (users.size() <= 0)
                                                            binding.noData.setVisibility(View.VISIBLE);
                                                        else
                                                            binding.noData.setVisibility(View.GONE);*/

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

                                // now check in chat branch if getActivity() ID is present or not.
                                // read chat branch for the ID is present than read block key for true value based on getActivity() make the users list.
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

    }

    private void sortByTrending() {
        Collections.sort(contentList, new Comparator<ContentModel>() {
            @Override
            public int compare(ContentModel model_1, ContentModel model_2) {
                return Integer.compare(model_2.getContentHeartCount(), model_1.getContentHeartCount());
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

                            if (mod.getUuid().equalsIgnoreCase(contentModel.getUuid())) {  // getActivity() makes sure that only the clicked item's heart count is updated.

                                // Automic Server-Side Increments for Like/Unlike post.
                                HashMap<String, Object> obj = new HashMap<>();
                                User user = sessionManager.getUserModel("loggedIn_UserModel");

                                List<String> likedList;
                                if (mod.getLikedBy() != null)
                                    likedList = mod.getLikedBy();
                                else
                                    likedList = new ArrayList<>();

                                if (likedList.contains(user.getUid())) { // ie. already liked by getActivity() user.
                                    obj.put("contentHeartCount", ServerValue.increment(-1));   // than decrement like count as already liked.
                                    likedList.remove(user.getUid());  // ie. since unliked than remove userId from list.
                                    obj.put("likedBy", likedList);
                                }
                                else {
                                    obj.put("contentHeartCount", ServerValue.increment(1));   // mod
                                    likedList.add(user.getUid()); // ie. since liked than add getActivity() userId in arraylist.
                                    obj.put("likedBy", likedList);
                                }

                                database.getReference()
                                        .child("post")
                                        .child(category_value)
                                        .child(contentModel.getUserID())
                                        .child("imagesPath")
                                        .child(data.getKey())
                                        .updateChildren(obj);

                              /*  // Updating userIDs who like the post.
                                Map<String, Object> updates = new HashMap<>();
                                updates.put(mod.getUserID(), true);
                                database.getReference()
                                        .child("post")
                                        .child(category_value)
                                        .child(contentModel.getUserID())
                                        .child("imagesPath")
                                        .child(data.getKey())
                                        .child("likedBy")
                                        .updateChildren(updates);*/
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setStatusBarColor() {
        // changing status bar color
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                //  dialog.show();

                if (requestCode == SHARE_REQUEST_CODE) {
                    boolean success = data.getBooleanExtra("SUCCESS", false);
                    if (success)
                        Toast.makeText(this.getActivity(), "Post uploaded successfully!", Toast.LENGTH_SHORT).show();
                }

                if (requestCode == STATUS_CAPTURE && resultCode == RESULT_OK) {
                    Uri d = data.getData();
                    Log.d("TAG", "onActivityResult: status capture " + d);
                    Intent i = new Intent(new Intent(this.getActivity(), PostDetailsActivity.class));
                    i.putExtra("URI", data.getData().toString());
                    i.putExtra("CAPTURE_REQUEST_CODE", STATUS_CAPTURE);
                    i.putExtra("category_value", category_value);
                    startActivityForResult(i, SHARE_REQUEST_CODE);
                }
                else if (requestCode == POST_CAPTURE && resultCode == RESULT_OK) {
                    Uri d = data.getData();
                    Log.d("TAG", "onActivityResult: status capture " + d);
                    Intent i = new Intent(new Intent(this.getActivity(), PostDetailsActivity.class));
                    i.putExtra("URI", data.getData().toString());
                    i.putExtra("CAPTURE_REQUEST_CODE", POST_CAPTURE);
                    i.putExtra("category_value", category_value);
                    startActivityForResult(i, SHARE_REQUEST_CODE);
                }

            }
            //
        }
    }



}