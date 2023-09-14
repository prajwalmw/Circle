package com.example.circle.fragment;

import static com.example.circle.activity.Chat_UserList.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.circle.R;
import com.example.circle.activity.FullscreenImageActivity;
import com.example.circle.adapter.LeaderboardAdapter;
import com.example.circle.databinding.FragmentStatusBinding;
import com.example.circle.model.ContentModel;
import com.example.circle.utilities.OnItemClickListener;
import com.example.circle.utilities.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class StatusFragment extends Fragment {
    private FragmentStatusBinding binding;
    private LeaderboardAdapter adapter;
    private SessionManager sessionManager;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ArrayList<String> categoryTitleList;
    ArrayList<ContentModel> contentModelArrayList, contentRemoveList;
    HashSet<ContentModel> top3ContentList;

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
        top3ContentList = new HashSet<>();
        contentModelArrayList = new ArrayList<>();
        contentRemoveList = new ArrayList<>();

        rankImgViewClickListeners();

        binding.backbtn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });
        adapter = new LeaderboardAdapter(getActivity(), contentModelArrayList, top3ContentList, new OnItemClickListener() {
            @Override
            public void onItemClick() {

            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerLeader.setLayoutManager(llm);
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

    private void rankImgViewClickListeners() {

        binding.imgvFirst.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("model", (Serializable) contentModelArrayList.get(0));
            startActivity(intent);
        });
        binding.imgvSecond.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("model", (Serializable) contentModelArrayList.get(1));
            startActivity(intent);
        });
        binding.imgvThird.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("model", (Serializable) contentModelArrayList.get(2));
            startActivity(intent);
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
                                boolean isAdded = contentModelArrayList.add(contentModel);

                                if (isAdded) {
                                    // sort list in desc order of heart count.
                                    contentModelArrayList = sortListInDescOrder(contentModelArrayList);
                                    if (adapter != null && contentModelArrayList.size() > 3) {
                                        top3ContentList.clear();
                                        top3ContentList.add(contentModelArrayList.get(0));
                                        top3ContentList.add(contentModelArrayList.get(1));
                                        top3ContentList.add(contentModelArrayList.get(2));

                                        ArrayList<ContentModel> topArrayList = new ArrayList<>();
                                        topArrayList.addAll(top3ContentList);
                                        topArrayList = sortListInDescOrder(topArrayList);
                                        setTop3UIValues(topArrayList);

                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setTop3UIValues(ArrayList<ContentModel> top3ContentList) {
        if (getActivity() == null || top3ContentList.size() == 0)
            return;

      //  if (rank == 0) {
            binding.firstLikes.setText(top3ContentList.get(0).getContentHeartCount() + "+");
            binding.firstNameTxt.setText(top3ContentList.get(0).getUserName());
            Glide.with(getActivity())
                    .asBitmap()
                    .load(top3ContentList.get(0).getContentImageUrl())
                    .placeholder(R.drawable.avatar)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(binding.imgvFirst);
     //   }
     //   else if (rank == 1) {
            binding.secondLikes.setText(top3ContentList.get(1).getContentHeartCount() + "+");
        binding.secondNameTxt2.setText(top3ContentList.get(1).getUserName());
        Glide.with(getActivity())
                    .asBitmap()
                    .load(top3ContentList.get(1).getContentImageUrl())
                    .placeholder(R.drawable.avatar)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(binding.imgvSecond);
     //   }
     //   else if (rank == 2) {
            binding.thirdLikes.setText(top3ContentList.get(2).getContentHeartCount() + "+");
        binding.thirdNameTxt3.setText(top3ContentList.get(2).getUserName());
        Glide.with(getActivity())
                    .asBitmap()
                    .load(top3ContentList.get(2).getContentImageUrl())
                    .placeholder(R.drawable.avatar)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(binding.imgvThird);
      //  }
    }

    private ArrayList<ContentModel> sortListInDescOrder(ArrayList<ContentModel> contentModelArrayList) {
        // heart count - contentList - sort as per desc of heart count.
        Collections.sort(contentModelArrayList, new Comparator<ContentModel>() {
            @Override
            public int compare(ContentModel model_1, ContentModel model_2) {
                return Integer.compare(model_2.getContentHeartCount(), model_1.getContentHeartCount());
            }
        });

        return contentModelArrayList;
    }
        // end

//        ArrayList<ContentModel> list2 = new ArrayList<>();
//        if (contentModelArrayList.size() > 3) {
//
//        }


}
