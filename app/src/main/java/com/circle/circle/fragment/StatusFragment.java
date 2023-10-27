package com.circle.circle.fragment;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.circle.circle.AppConstants;
import com.circle.circle.R;
import com.circle.circle.activity.FullscreenImageActivity;
import com.circle.circle.adapter.LeaderboardAdapter;
import com.circle.circle.databinding.FragmentStatusBinding;
import com.circle.circle.model.ContentModel;
import com.circle.circle.utilities.SessionManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

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

        initAds();
        adRequest = new AdRequest.Builder().build();
        loadFullScreenAd();

        // Admob - Start
//        final Handler handelay = new Handler();
//        handelay.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initAds();
//
//                adRequest = new AdRequest.Builder().build();
//              /*  binding.adView.loadAd(adRequest);
//                binding.adView.setAdListener(new AdListener() {
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        super.onAdFailedToLoad(loadAdError);
//                        binding.adView.loadAd(adRequest);
//                    }
//                });
//*/
//                loadFullScreenAd();
//            }
//        }, 5000);

        // Admob - End

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

        adapter = new LeaderboardAdapter(getActivity(), top10List, top3ContentList,
                new LeaderboardAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ContentModel model) {
                       // Toast.makeText(getActivity(), "Hi", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
                        intent.putExtra("model", model);
                        startActivity(intent);
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }

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

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(getActivity());
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
        });

        binding.imgvSecond.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("model", (Serializable) top3ContentList.get(1));
            startActivity(intent);

            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        });

        binding.imgvThird.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("model", (Serializable) top3ContentList.get(2));
            startActivity(intent);
            
            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
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

    // only once...
    private void initAds() {
        // Ads initialize only once.
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    public void loadFullScreenAd() {
        // Fullscreen ads.
        InterstitialAd.load(getActivity(), AppConstants.FULLSCREEN, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        mInterstitialAd = null;
                        loadFullScreenAd();
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                //   mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                mInterstitialAd = null;
                                loadFullScreenAd();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                            }
                        });
                    }
                });
    }

}
