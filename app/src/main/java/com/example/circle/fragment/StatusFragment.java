package com.example.circle.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.circle.R;
import com.example.circle.adapter.LeaderboardAdapter;
import com.example.circle.databinding.FragmentStatusBinding;
import com.example.circle.model.ContentModel;
import com.example.circle.utilities.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StatusFragment extends Fragment {
    private FragmentStatusBinding binding;
    private LeaderboardAdapter adapter;
    private SessionManager sessionManager;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ArrayList<String> categoryTitleList;
    ArrayList<ContentModel> contentModelArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // changing status bar color
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.leader_head_color));
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getActivity());
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        categoryTitleList = new ArrayList<>();
        contentModelArrayList = new ArrayList<>();

        adapter = new LeaderboardAdapter(getActivity());
        binding.recyclerLeader.setAdapter(adapter);

        database.getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String categoryTitle = childSnapshot.getKey();
                            categoryTitleList.add(categoryTitle);
                            fetchPostValues(categoryTitle);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void fetchPostValues(String category) {
        database.getReference("post")
                .child(category)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot childSnapshot : snapshot.getChildren()) {

                            for (DataSnapshot innerChildSnapShot : childSnapshot.child("imagesPath").getChildren()) {
                                ContentModel contentModel = innerChildSnapShot.getValue(ContentModel.class);
                                contentModelArrayList.add(contentModel);
                            }
                        }

                        // sort list in desc order of heart count.
                        sortListInDescOrder(contentModelArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sortListInDescOrder(ArrayList<ContentModel> contentModelArrayList) {
        // heart count - contentList - sort as per desc of heart count.
        Collections.sort(contentModelArrayList, new Comparator<ContentModel>() {
            @Override
            public int compare(ContentModel model_1, ContentModel model_2) {
                return Integer.compare(model_2.getContentHeartCount(), model_1.getContentHeartCount());
            }
        });
        // end

        // set values on UI.
        binding.firstLikes.setText(contentModelArrayList.get(0).getContentHeartCount() + "+");
        binding.secondLikes.setText(contentModelArrayList.get(1).getContentHeartCount() + "+");
        binding.thirdLikes.setText(contentModelArrayList.get(2).getContentHeartCount() + "+");

        Glide.with(getActivity())
                .asBitmap()
                .load(contentModelArrayList.get(0).getContentImageUrl())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.img1);

        Glide.with(getActivity())
                .asBitmap()
                .load(contentModelArrayList.get(1).getContentImageUrl())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.img2);

        Glide.with(getActivity())
                .asBitmap()
                .load(contentModelArrayList.get(2).getContentImageUrl())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.img3);

    }


}
