package com.example.circle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.circle.databinding.ActivityUserSetupScreenBinding;
import com.example.circle.model.CategoryModel;
import com.example.circle.model.User;
import com.example.circle.utilities.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserSetupScreen extends AppCompatActivity {
    ActivityUserSetupScreenBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog dialog;
    User user;
    private Intent intent;
    private String category_value;
    private SessionManager sessionManager;
    private List<CategoryModel> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSetupScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        intent = getIntent();
        if (intent.getExtras() != null) {
          //  category_value = intent.getStringExtra("category");
            Bundle args = intent.getBundleExtra("BUNDLE");
            categoryList = (List<CategoryModel>) args.getSerializable("category_list");
            Log.v("Category", "checkedvalues: " + categoryList.size());
        }

        // changing status bar color
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        //  getSupportActionBar().hide();

//        database.getReference().child("users").child(auth.getCurrentUser().getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        user = snapshot.getValue(User.class);
//                        if(user != null) {
//                            Log.v("user", "userValue: "+ user + ", " + user.getProfileImage() + ", " + user.getName());
//                          //  binding.imageViewIcon.setImageURI(Uri.parse(user.getProfileImage()));
//                            Glide.with(UserSetupScreen.this)
//                                    .load(user.getProfileImage())
//                                    .placeholder(R.drawable.avatar)
//                                    .into(binding.imageViewIcon);
//                            binding.nameBox.setText(user.getName());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });



        binding.imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameBox.getText().toString();

                if(name.isEmpty()) {
                    binding.nameBox.setError("Please type a name");
                    return;
                }

                dialog.show();
                if(selectedImage != null) {
                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();

                                        String uid = auth.getUid();
                                        String phone = auth.getCurrentUser().getPhoneNumber();
                                        String name = binding.nameBox.getText().toString();
                                        String n = sessionManager.getLoggedInUsername();
                                        if (sessionManager.getLoggedInUsername().equalsIgnoreCase("")) // Adding username who logged-in into the session manager.
                                            sessionManager.setLoggedInUsername(name);

                                        User user = new User(uid, name, phone, imageUrl);
                                        sessionManager.setUserModel(user, "loggedIn_UserModel");
                                     //   sessionManager.saveArrayList(categoryList, "my_community"); // store value

/*
                                        if (categoryList != null) {
                                            for (int i = 0; i < categoryList.size(); i++) {
                                                String category = categoryList.get(i).getTitle();

                                                database.getReference()
                                                        .child("users")
                                                        .child(category)
                                                        .child(uid)
                                                        .setValue(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                dialog.dismiss();
                                                                sessionManager.setCategorySelected(category);

                                                            }
                                                        });
                                            }
                                        }
*/
                                      //  else {
                                            // todo: testing
                                          /*  database.getReference()
                                                    .child("users")
                                                    .child("circle_default")
                                                    .child(uid)
                                                    .setValue(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            dialog.dismiss();
                                                         //   sessionManager.setCategorySelected(category);

                                                        }
                                                    });*/

                                    //    }

                                        //outside of for-loop

                                     //   String s = sessionManager.getCategorySelected();
                                        Intent intent = new Intent(UserSetupScreen.this, CategoryActivity.class);
                                        Bundle args = new Bundle();

                                        //  Log.v("Chat", "usersetup_session: " + s);
                                        Log.v("Chat", "user_setup_value: " + categoryList);

                                        if (sessionManager.getArrayList("my_community") != null) {
                                            args.putSerializable("category_list", (Serializable) sessionManager.getArrayList("my_community"));
                                            intent.putExtra("BUNDLE",args);
                                        }
                                        else {
                                            args.putSerializable("category_list", (Serializable) categoryList);
                                            intent.putExtra("BUNDLE",args);
                                        }

                                     /*   if (sessionManager.getCategorySelected() != null) {
                                            //   intent.putExtra("category", s); TODO: uncomment
                                            Bundle args = new Bundle();
                                            args.putSerializable("category_list", (Serializable) categoryList);
                                            intent.putExtra("BUNDLE",args);
                                        }
                                        else {
                                          //  intent.putExtra("category", category);
                                            Bundle args = new Bundle();
                                            args.putSerializable("category_list", (Serializable) categoryList);
                                            intent.putExtra("BUNDLE",args);
                                        }*/

                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                } else { // here code for uploading data in firebase database...
                    String uid = auth.getUid();
                    String phone = auth.getCurrentUser().getPhoneNumber();

                    String n = sessionManager.getLoggedInUsername();
                    User user = new User(uid, name, phone, "No Image");
                    sessionManager.setUserModel(user, "loggedIn_UserModel");
                    if (sessionManager.getLoggedInUsername().equalsIgnoreCase(""))
                        sessionManager.setLoggedInUsername(name);   // Adding username who logged-in into the session manager.


                    if (categoryList != null) {
                        for (int i = 0; i < categoryList.size(); i++) {
                            String category = categoryList.get(i).getTitle();

                            database.getReference()
                                    .child("users")
                                    .child(category)
                                    .child(uid)
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(UserSetupScreen.this, CategoryActivity.class);
//                                        if (sessionManager.getCategorySelected() != null)
//                                            intent.putExtra("category", sessionManager.getCategorySelected());
//                                        else
//                                            intent.putExtra("category", category);

                                            if (sessionManager.getArrayList("my_community") != null) {
                                                Bundle args = new Bundle();
                                                args.putSerializable("category_list", (Serializable) sessionManager.getArrayList("my_community"));
                                                intent.putExtra("BUNDLE", args);
                                            } else {
                                                Bundle args = new Bundle();
                                                args.putSerializable("category_list", (Serializable) categoryList);
                                                intent.putExtra("BUNDLE", args);
                                            }


                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }
                    }
                    else {
                        // no category selected
                        dialog.dismiss();
                        Intent intent = new Intent(UserSetupScreen.this, CategoryActivity.class);
//                                        if (sessionManager.getCategorySelected() != null)
//                                            intent.putExtra("category", sessionManager.getCategorySelected());
//                                        else
//                                            intent.putExtra("category", category);

                        if (sessionManager.getArrayList("my_community") != null) {
                            Bundle args = new Bundle();
                            args.putSerializable("category_list", (Serializable) sessionManager.getArrayList("my_community"));
                            intent.putExtra("BUNDLE", args);
                        } else {
                            Bundle args = new Bundle();
                            args.putSerializable("category_list", (Serializable) categoryList);
                            intent.putExtra("BUNDLE", args);
                        }


                        startActivity(intent);
                        finish();
/*
                        database.getReference()
                                .child("users")
                                .child("circle_default")
                                .child(uid)
                                .setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
*/
                    }

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                Uri uri = data.getData(); // filepath
                FirebaseStorage storage = FirebaseStorage.getInstance();
                long time = new Date().getTime();
                StorageReference reference = storage.getReference().child("Profiles").child(time+"");
                reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("image", filePath);

                                    if (categoryList != null) {
                                        for (int i = 0; i < categoryList.size(); i++) {
                                            String category = categoryList.get(i).getTitle();

                                            database.getReference()
                                                    .child("users")
                                                    .child(category)
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                        }
                                    }
                                    else {
                                        // ie. no category selected yet. so add default category
/*
                                        database.getReference()
                                                .child("users")
                                                .child("circle_default")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
*/
                                    }

                                }
                            });
                        }
                    }
                });


                binding.imageViewIcon.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}