package com.example.circle.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.circle.R;
import com.example.circle.adapter.MyCommunityAdapter;
import com.example.circle.adapter.SettingsAdapter;
import com.example.circle.databinding.FragmentHomeBinding;
import com.example.circle.databinding.FragmentProfileBinding;
import com.example.circle.model.ListItemModel;
import com.example.circle.model.User;
import com.example.circle.utilities.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SettingsAdapter settingsAdapter;
    private FragmentProfileBinding binding;
    private List<ListItemModel> listItemModels;
    private SessionManager sessionManager;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // changing status bar color
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
        }

        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserModel("loggedIn_UserModel");

        Glide.with(getActivity()).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .circleCrop()
                .into(binding.profileImgIcon);

        binding.userNameTxt.setText(user.getName().trim());

        getListItemForSettings();
        settingsAdapter = new SettingsAdapter(getActivity(), listItemModels);
        binding.rvSettings.setAdapter(settingsAdapter);

        return root;
    }

    private void getListItemForSettings() {
        listItemModels = new ArrayList<>();
      /*  listItemModels.add(new ListItemModel(R.drawable.avatar, "About Us"));
        listItemModels.add(new ListItemModel(R.drawable.avatar, "Privacy Policy"));
        listItemModels.add(new ListItemModel(R.drawable.avatar, "Terms & Conditions"));*/

        listItemModels.add(new ListItemModel("About Us"));
        listItemModels.add(new ListItemModel("Privacy Policy"));
        listItemModels.add(new ListItemModel("Terms & Conditions"));
        listItemModels.add(new ListItemModel("Feedback"));
    }


}