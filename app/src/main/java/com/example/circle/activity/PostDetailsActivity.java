package com.example.circle.activity;

import static com.example.circle.activity.Chat_UserList.POST_CAPTURE;
import static com.example.circle.activity.Chat_UserList.SHARE_REQUEST_CODE;
import static com.example.circle.activity.Chat_UserList.STATUS_CAPTURE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.circle.R;
import com.example.circle.databinding.ActivityPostDetailsBinding;
import com.example.circle.model.ContentModel;
import com.example.circle.model.User;
import com.example.circle.model.UserStatus;
import com.example.circle.utilities.DoubleClickEvent;
import com.example.circle.utilities.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class PostDetailsActivity extends AppCompatActivity {
    ActivityPostDetailsBinding binding;
    int CAPTURE_REQUEST_CODE;
    String uri, category_value;
    SessionManager sessionManager;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference reference;
    public static final String SUCCESS = "SUCCESS";
    ProgressDialog dialog;
    boolean isImageCropped = true;
    private Context context = PostDetailsActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(this);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading hold on a sec ...");
        dialog.setCancelable(false);

        Intent intent = getIntent();
        if (intent != null) {
            CAPTURE_REQUEST_CODE = intent.getIntExtra("CAPTURE_REQUEST_CODE", 0);
            uri = intent.getStringExtra("URI");
            category_value = intent.getStringExtra("category_value");
        }

        Glide.with(this)
                .asBitmap()
                .load(uri)
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .centerCrop()
                .into(binding.imgCard);

        binding.shareBtn.setOnClickListener(v -> {
            dialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    image_upload(uri, CAPTURE_REQUEST_CODE);
                }
            }).start();

        });

        binding.imgCard.setOnClickListener(new DoubleClickEvent() {
            @Override
            public void onSingleClick(View v) {
                if (isImageCropped) {
                    isImageCropped = false;
                    Glide.with(context)
                            .asBitmap()
                            .load(uri)
                            .placeholder(R.drawable.avatar)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .fitCenter()
                            .into(binding.imgCard);
                }
                else {
                    isImageCropped = true;
                    Glide.with(context)
                            .asBitmap()
                            .load(uri)
                            .placeholder(R.drawable.avatar)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .centerCrop()
                            .into(binding.imgCard);
                }

            }

            @Override
            public void onDoubleClick(View v) {
//                if (likedList.contains(fID)) {   // ie. already liked so here dislike.
//                    itemClick.onclick(false, contentList.get(getAdapterPosition()));
//                } else
//                    itemClick.onclick(true, contentList.get(getAdapterPosition()));
            }
        });

    }

    private void image_upload(String data, int requestCode) {
        //  FirebaseStorage storage = FirebaseStorage.getInstance();
        Date date = new Date();

        if (requestCode == STATUS_CAPTURE)
            reference = storage.getReference().child("status").child(date.getTime() + "");
        else if (requestCode == POST_CAPTURE)
            reference = storage.getReference().child("post").child(category_value).child(date.getTime() + "");
        reference.putFile(Uri.parse(data)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                            ContentModel contentModel = new ContentModel(   // adding values...
                                    FirebaseAuth.getInstance().getUid(), userStatus.getName(),
                                    userStatus.getProfileImage(), UUID.randomUUID().toString(), imageUrl,
                                    binding.descriptionInput.getText().toString().trim(), 0,
                                    userStatus.getLastUpdated(), category_value,
                                    binding.linkInput.getText().toString().trim());

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

                            Intent intent = new Intent();
                            intent.putExtra(SUCCESS, true);
                            setResult(SHARE_REQUEST_CODE, intent);
                            finish();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            });

                        }
                    });
                }
            }
        });
    }

}