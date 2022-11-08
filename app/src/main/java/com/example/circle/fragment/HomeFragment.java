package com.example.circle.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.circle.R;
import com.example.circle.adapter.MyCommunityAdapter;
import com.example.circle.databinding.FragmentHomeBinding;
import com.example.circle.model.CategoryModel;
import com.example.circle.utilities.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private MyCommunityAdapter adapter;
    private List<CategoryModel> categoryList;
    FirebaseDatabase database;
    private SessionManager sessionManager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getActivity());

        if (categoryList == null) {
            if (sessionManager.getArrayList("my_community") != null) {
                categoryList = sessionManager.getArrayList("my_community");
            }
        }

        Glide.with(getActivity()).load(sessionManager.getUserModel("loggedIn_UserModel").getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(binding.profileImgIcon);

        adapter = new MyCommunityAdapter(getActivity(), categoryList);
        binding.recyclerviewCategory.setAdapter(adapter);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
