package com.circle.circle.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.circle.circle.databinding.ActivityFullscreenImageBinding;
import com.circle.circle.R;
import com.circle.circle.model.ContentModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenImageActivity extends AppCompatActivity {
    private ContentModel contentModel;
    private Context context;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
/*
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
*/
    private View mControlsView;
/*
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
*/
    private boolean mVisible;
/*
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
*/
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
/*
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
*/
    private ActivityFullscreenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = FullscreenImageActivity.this;

        // changing status bar color
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.imgvFullScreen;

        Intent intent = getIntent();
        contentModel = (ContentModel) intent.getSerializableExtra("model");
        Log.d("TAG", "onCreate: " + contentModel);

        Glide.with(context)
                .asBitmap()
                .load(contentModel.getContentImageUrl())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.imgvFullScreen);

        Glide.with(context)
                .asBitmap()
                .load(contentModel.getUserProfile())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.profileImgIcon);

        binding.profileImgIcon.setOnClickListener(v -> {
            if (contentModel != null)
                showPreview();
        });

        binding.postUsername.setText(contentModel.getUserName());
        binding.postCategory.setText(contentModel.getCategory_value());

        if (contentModel.getContenTitle().length() > 70) {
            binding.txtShowMore.setVisibility(View.VISIBLE);
        }
        else {
            binding.txtShowMore.setVisibility(View.GONE);
        }

        binding.txtShowMore.setOnClickListener(v -> {
            if (binding.txtShowMore.getText().toString().equalsIgnoreCase(getString(R.string.show_more)))
            {
                binding.tvDescription.setMaxLines(Integer.MAX_VALUE);//your TextView
                binding.txtShowMore.setText(Html.fromHtml("<u>show less</u>"));
            }
            else
            {
                binding.tvDescription.setMaxLines(2);//your TextView
                binding.txtShowMore.setText(Html.fromHtml("<u>show more</u>"));
            }
        });

        binding.tvDescription.setText(contentModel.getContenTitle());
        if (!contentModel.getLink().equalsIgnoreCase("")) {
            binding.linkstxt.setVisibility(View.VISIBLE);
            String link = "<a href =" + contentModel.getLink().trim() + ">visit</a>";
            binding.linkstxt.setText(Html.fromHtml(link));
            binding.linkstxt.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            binding.linkstxt.setVisibility(View.GONE);
        }



        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    //    binding.dummyButton.setOnTouchListener(mDelayHideTouchListener);
    }

    private void showPreview() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View view = getLayoutInflater().inflate(R.layout.profile_ui, null);
        builder.setView(view);

        ImageButton manage_list = view.findViewById(R.id.manage_list);
        manage_list.setVisibility(View.GONE);

        ImageButton instaBtn = view.findViewById(R.id.instagramBtn);
        ImageButton youtubeBtn = view.findViewById(R.id.youtubeBtn);

        instaBtn.setOnClickListener(v -> {
            String url = "";
            if (contentModel.getInstagramURL() == null || contentModel.getInstagramURL().isEmpty())
                url = "https://instagram.com/_u/circlecommunity2023";
            else
                url = "https://instagram.com/_u/" +  contentModel.getInstagramURL();

            Uri uri = Uri.parse(url);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show();
            }
        });

        // youtube
        youtubeBtn.setOnClickListener(v -> {
            Uri url = Uri.parse(contentModel.getYoutubeURL());
            Intent likeIng = new Intent(Intent.ACTION_VIEW, url);
            likeIng.setPackage("com.google.android.youtube");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show();
            }
        });

        // others
        ImageView img = view.findViewById(R.id.profile_img_icon);
        Glide.with(context)
                .asBitmap()
                .load(contentModel.getUserProfile())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .circleCrop()
                .into(img);

        TextView userName = view.findViewById(R.id.userNameTxt);
        userName.setText(contentModel.getUserName());

        TextView desc = view.findViewById(R.id.aboutMeTxt);
        if (!contentModel.getProfileDescription().isEmpty())
            desc.setText(contentModel.getProfileDescription());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
     //   delayedHide(100);
    }

//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }

/*
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
*/

//    private void show() {
//        // Show the system bar
//        if (Build.VERSION.SDK_INT >= 30) {
//            mContentView.getWindowInsetsController().show(
//                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
//        } else {
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        }
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
/*
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
*/


}