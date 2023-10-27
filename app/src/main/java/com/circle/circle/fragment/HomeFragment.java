package com.circle.circle.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.circle.circle.R;
import com.circle.circle.activity.CategoryActivity;
import com.circle.circle.adapter.MyCommunityAdapter;
import com.circle.circle.databinding.FragmentHomeBinding;
import com.circle.circle.model.CategoryModel;
import com.circle.circle.utilities.SessionManager;
import com.google.firebase.database.FirebaseDatabase;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private MyCommunityAdapter adapter;
    private List<CategoryModel> categoryList;
    FirebaseDatabase database;
    private SessionManager sessionManager;
    public static final int STATUS_CAPTURE = 75;
    public static final int POST_CAPTURE = 99;
    public static final int SHARE_REQUEST_CODE = 98;
    private Intent intent;
    FragmentPagerItems pagerItems;
    List<FragmentPagerItem> itemList;
    FragmentPagerItemAdapter adapter1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // changing status bar color
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
        }

        intent = getActivity().getIntent();
        if (intent.getExtras() != null) {
            Bundle args = intent.getBundleExtra("BUNDLE");
            categoryList = (List<CategoryModel>) args.getSerializable("category_list");
        }

        // start
        sessionManager = new SessionManager(getActivity());

        if (categoryList == null) {
            //  startActivity(new Intent(this.getActivity(), CategoryActivity.class));

            if (sessionManager.getArrayList("my_community") != null) {
                categoryList = sessionManager.getArrayList("my_community");
            }
        }

        Glide.with(getActivity()).load(sessionManager.getUserModel("loggedIn_UserModel")
                        .getProfileImage())
                .placeholder(R.drawable.avatar)
                .circleCrop()
                .into(binding.profileImgIcon);


//        if (categoryList != null) {
//            adapter = new MyCommunityAdapter(getActivity(), categoryList);
//            binding.recyclerviewCategory.setAdapter(adapter);
//
//            for (int i = 0; i < categoryList.size(); i++) {
//                String category_title = categoryList.get(i).getTitle();
//                database = FirebaseDatabase.getInstance();
//                /**
//                 * need to upate token else notific wont show up as it requires token.
//                 */
//                FirebaseMessaging.getInstance()
//                        .getToken()
//                        .addOnSuccessListener(new OnSuccessListener<String>() {
//                            @Override
//                            public void onSuccess(String token) {
//                                HashMap<String, Object> map = new HashMap<>();
//                                map.put("token", token);
//                                database.getReference()
//                                        .child("users")
//                                        .child(category_title)
//                                        .child(FirebaseAuth.getInstance().getUid())
//                                        .updateChildren(map);
//                                //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        }

        // end

        FragmentPagerItems.Creator creator = null;
        itemList = new ArrayList<>();
        if (categoryList != null && categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                String split[] = categoryList.get(i).getTitle().split(" ");
                FragmentPagerItem fragmentPagerItem = FragmentPagerItem.of(StringUtils.capitalize(split[0]), PostFragment.class,
                        new Bundler().putString("key", categoryList.get(i).getTitle()).get());
                itemList.add(fragmentPagerItem);
            }
        }

        pagerItems = new FragmentPagerItems(getActivity());
        pagerItems.addAll(itemList);
        if (adapter1 == null) {
            adapter1 = new FragmentPagerItemAdapter(
                    getChildFragmentManager(), pagerItems);
        }
        binding.viewpager.setAdapter(adapter1);
     //   mPager.setAdapter(new BasePagerAdapter(getChildFragmentManager(), getResources()));


        binding.manageList.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CategoryActivity.class);
            intent.putExtra("screen", true);

            Bundle args = new Bundle();
            args.putSerializable("category_list", (Serializable) categoryList);
            intent.putExtra("BUNDLE", args);
            startActivity(intent);
        });

        binding.viewpagertab.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View itemView = inflater.inflate(R.layout.custom_tab_item, container, false);
                TextView text = (TextView) itemView.findViewById(R.id.tabTitle);
                text.setText(adapter.getPageTitle(position));
                ImageView icon = (ImageView) itemView.findViewById(R.id.tabIcon);

                if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Sports"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.sport_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Travel"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.travel_large_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("FRIENDS"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.friends_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Foodie"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.food_new_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Career"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.career_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Tech"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.technology_large_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Comics"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.anime_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Business"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.business_large_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Memes"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.meme_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Stock"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.stockmarket_large_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Movies"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.movies_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Crypto"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.crypto_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Senior"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.senior_citizen_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("DIY"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.diy_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Gaming"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.fun_large_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Dance"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.dance_large_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Fashion"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.fashion_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Music"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.music_large_icon));
                else if (adapter.getPageTitle(position).toString().equalsIgnoreCase("Photography"))
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.camera_large_icon));

                // todo: add rest

                return itemView;
            }
        });

        binding.viewpagertab.setViewPager(binding.viewpager);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



}
