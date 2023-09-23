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
import java.util.List;

public class StatusFragment extends Fragment {
    private FragmentStatusBinding binding;
    private LeaderboardAdapter adapter;
    private SessionManager sessionManager;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ArrayList<String> categoryTitleList;
    List<ContentModel> contentModelArrayList, contentRemoveList;
    List<ContentModel> top3ContentList, top10List;

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

        sessionManager = new SessionManager(getActivity());
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        categoryTitleList = new ArrayList<>();
        top3ContentList = new ArrayList<>();
        top10List = new ArrayList<>();
        contentModelArrayList = new ArrayList<>();
        contentRemoveList = new ArrayList<>();

        rankImgViewClickListeners();

        binding.backbtn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });
        adapter = new LeaderboardAdapter(getActivity(), top10List, top3ContentList, new OnItemClickListener() {
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
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void rankImgViewClickListeners() {
        binding.imgvFirst.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("model", (Serializable) top3ContentList.get(0));
            startActivity(intent);
        });
        binding.imgvSecond.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("model", (Serializable) top3ContentList.get(1));
            startActivity(intent);
        });
        binding.imgvThird.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("model", (Serializable) top3ContentList.get(2));
            startActivity(intent);
        });
    }

    private void fetchPostValues(String category) {
        database.getReference("post")
                .child(category)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isExists = true;
                        for(DataSnapshot childSnapshot : snapshot.getChildren()) {

                            for (DataSnapshot innerChildSnapShot : childSnapshot.child("imagesPath").getChildren()) {
                                ContentModel contentModel = innerChildSnapShot.getValue(ContentModel.class);
                                contentModelArrayList.add(contentModel);
                            }
                        }

                        isExists = false;

                        // sort list in desc order of heart count.
                        if (!isExists)
                            setValues(contentModelArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setValues(List<ContentModel> contentModelArrayList) {
        contentModelArrayList = sortListInDescOrder(contentModelArrayList);
        if (adapter != null && contentModelArrayList.size() > 3) {
            top3ContentList.clear();
            top3ContentList.addAll(contentModelArrayList.subList(0, 3));    // TOP 3
            setTop3UIValues(top3ContentList);

            if (contentModelArrayList.size() > 10) {
                top10List.clear();
                top10List.addAll(contentModelArrayList.subList(3, 10));    // TOP 10
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void setTop3UIValues(List<ContentModel> top3ContentList) {
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

    private List<ContentModel> sortListInDescOrder(List<ContentModel> contentModelArrayList) {
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

}
