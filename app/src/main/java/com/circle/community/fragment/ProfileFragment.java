package com.circle.community.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.circle.community.activity.UserSetupScreen;
import com.circle.community.R;
import com.circle.community.adapter.SettingsAdapter;
import com.circle.community.databinding.FragmentHomeBinding;
import com.circle.community.databinding.FragmentProfileBinding;
import com.circle.community.model.ListItemModel;
import com.circle.community.model.User;
import com.circle.community.utilities.SessionManager;

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
                .into(binding.profileUi.profileImgIcon);

        binding.profileUi.userNameTxt.setText(user.getName().trim());

        getListItemForSettings();
        settingsAdapter = new SettingsAdapter(getActivity(), listItemModels);
        binding.rvSettings.setAdapter(settingsAdapter);

        binding.profileUi.manageList.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserSetupScreen.class);
            intent.putExtra("profile", user.getProfileImage());
            intent.putExtra("name", binding.profileUi.userNameTxt.getText().toString().trim());
            intent.putExtra("description", binding.profileUi.aboutMeTxt.getText().toString().trim());
            intent.putExtra("instagram", user.getInstagramID());
            intent.putExtra("youtube", user.getYoutubeID());
            startActivity(intent);
        });

        // instagram and youtube btn - START
       /* if (!sessionManager.getInstagramId().isEmpty())
            binding.profileUi.instagramBtn.setVisibility(View.VISIBLE);
        else
            binding.profileUi.instagramBtn.setVisibility(View.GONE);

        if (!sessionManager.getYoutubeId().isEmpty())
            binding.profileUi.youtubeBtn.setVisibility(View.VISIBLE);
        else
            binding.profileUi.youtubeBtn.setVisibility(View.GONE);*/

        if (!sessionManager.getAboutmeDesc().isEmpty())
            if (!user.getDescription().isEmpty())
                binding.profileUi.aboutMeTxt.setText(user.getDescription());

        binding.profileUi.instagramBtn.setOnClickListener(v -> {
            String url = "";
            if (user.getInstagramID().isEmpty())
                url = "https://instagram.com/_u/circlecommunity2023";
            else
                url = "https://instagram.com/_u/" +  user.getInstagramID();

            Uri uri = Uri.parse(url);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
            }
        });

        binding.profileUi.youtubeBtn.setOnClickListener(v -> {
            Uri url = Uri.parse(user.getYoutubeID());
            Intent likeIng = new Intent(Intent.ACTION_VIEW, url);
            likeIng.setPackage("com.google.android.youtube");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
            }
        });
        // instagram and youtube btn - END

      //  binding.instagTXT.setText("My Instagram");
/*
        binding.instagTXT.setOnClickListener(v -> {
            Uri uri = Uri.parse("http://instagram.com/_u/x__optimistic_creature__x");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/x__optimistic_creature__x")));
            }
        });
*/

        return root;
    }

    private void getListItemForSettings() {
        listItemModels = new ArrayList<>();
      /*  listItemModels.add(new ListItemModel(R.drawable.avatar, "About Us"));
        listItemModels.add(new ListItemModel(R.drawable.avatar, "Privacy Policy"));
        listItemModels.add(new ListItemModel(R.drawable.avatar, "Terms & Conditions")); */

        listItemModels.add(new ListItemModel("About Us"));
        listItemModels.add(new ListItemModel("Privacy Policy"));
        listItemModels.add(new ListItemModel("Terms of Use"));
        listItemModels.add(new ListItemModel("FAQ"));

      /*  listItemModels.add(new ListItemModel("Instagram Profile"));
        listItemModels.add(new ListItemModel("YouTube Channel"));*/
    }


}