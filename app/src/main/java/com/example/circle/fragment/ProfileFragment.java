package com.example.circle.fragment;

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
                .into(binding.profileUi.profileImgIcon);

        binding.profileUi.userNameTxt.setText(user.getName().trim());

        getListItemForSettings();
        settingsAdapter = new SettingsAdapter(getActivity(), listItemModels);
        binding.rvSettings.setAdapter(settingsAdapter);

        // instagram and youtube btn - START
        if (!sessionManager.getInstagramId().isEmpty())
            binding.profileUi.instagramBtn.setVisibility(View.VISIBLE);
        else
            binding.profileUi.instagramBtn.setVisibility(View.GONE);

        if (!sessionManager.getYoutubeId().isEmpty())
            binding.profileUi.youtubeBtn.setVisibility(View.VISIBLE);
        else
            binding.profileUi.youtubeBtn.setVisibility(View.GONE);

        binding.profileUi.instagramBtn.setOnClickListener(v -> {
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

        if (!sessionManager.getAboutmeDesc().isEmpty())
            binding.profileUi.aboutMeTxt.setText(sessionManager.getAboutmeDesc());

        binding.profileUi.youtubeBtn.setOnClickListener(v -> {
            Uri url = Uri.parse(sessionManager.getYoutubeId());
            Intent likeIng = new Intent(Intent.ACTION_VIEW, url);
            likeIng.setPackage("com.google.android.youtube");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
               /* startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/x__optimistic_creature__x")));*/
                Toast.makeText(getActivity(), "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
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
        listItemModels.add(new ListItemModel("Terms & Conditions"));
        listItemModels.add(new ListItemModel("Feedback"));

      /*  listItemModels.add(new ListItemModel("Instagram Profile"));
        listItemModels.add(new ListItemModel("YouTube Channel"));*/
    }


}