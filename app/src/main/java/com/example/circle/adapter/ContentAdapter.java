package com.example.circle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.circle.R;
import com.example.circle.model.ContentModel;
import com.example.circle.model.User;
import com.example.circle.utilities.DoubleClickEvent;
import com.example.circle.utilities.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    Context context;
    ArrayList<ContentModel> contentList;
    String category_value;
    ContentAdapter.OnItemClick itemClick;
    SessionManager sessionManager;
    String fID = FirebaseAuth.getInstance().getUid();

    public ContentAdapter(Context context, ArrayList<ContentModel> contentList, ContentAdapter.OnItemClick itemClick) {
        this.context = context;
        this.contentList = contentList;
        this.itemClick = itemClick;
        sessionManager = new SessionManager(context);
     //   user = sessionManager.getUserModel("loggedIn_UserModel");
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_listitem, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        // heart count - contentList - sort as per desc of heart count.
/*
        Collections.sort(contentList, new Comparator<ContentModel>() {
            @Override
            public int compare(ContentModel model_1, ContentModel model_2) {
                return Integer.compare(model_2.getContentHeartCount(), model_1.getContentHeartCount());
            }
        });
*/
        // end

        ContentModel contentModel = contentList.get(position);

        holder.contentTitle.setText(contentModel.getContenTitle());
        holder.post_username.setText(contentModel.getUserName());
        holder.contentLikeCount.setText(String.valueOf(contentModel.getContentHeartCount()));

        Glide.with(context)
                .asBitmap()
                .load(contentModel.getContentImageUrl())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.content_imageview);

        Glide.with(context)
                .asBitmap()
                .load(contentModel.getUserProfile())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.profile_img_icon);

        if (contentList.get(position).getLikedBy() != null)
            holder.likedList = contentList.get(position).getLikedBy();
        else
            holder.likedList = new ArrayList<>();

        if (holder.likedList.contains(fID)) {
            holder.like_btn.setImageDrawable(context.getDrawable(R.drawable.like_heart_filled));
        }
        else {
            holder.like_btn.setImageDrawable(context.getDrawable(R.drawable.like_heart_unfilled));
        }

/*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("image", user.getProfileImage());
                intent.putExtra("uid", user.getUid());
                intent.putExtra("token", user.getToken());
                intent.putExtra("block", user.isIsblocked());
                intent.putExtra("category", category_value);
                context.startActivity(intent);
            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView contentTitle, contentLikeCount, post_username;
        ImageView content_imageview, profile_img_icon;
        ImageButton like_btn;
        List<String> likedList;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentTitle = itemView.findViewById(R.id.content_title);
            post_username = itemView.findViewById(R.id.post_username);
            contentLikeCount = itemView.findViewById(R.id.content_like_count);
            content_imageview = itemView.findViewById(R.id.content_imageview);
            profile_img_icon = itemView.findViewById(R.id.profile_img_icon);
            like_btn = itemView.findViewById(R.id.like_btn);

            like_btn.setOnClickListener(v -> {
                if (likedList.contains(fID)) {   // ie. already liked so here dislike.
                    itemClick.onclick(false, contentList.get(getAdapterPosition()));
                } else
                    itemClick.onclick(true, contentList.get(getAdapterPosition()));
            });

            content_imageview.setOnClickListener(new DoubleClickEvent() {
                @Override
                public void onSingleClick(View v) {

                }

                @Override
                public void onDoubleClick(View v) {
                    if (likedList.contains(fID)) {   // ie. already liked so here dislike.
                        itemClick.onclick(false, contentList.get(getAdapterPosition()));
                    } else
                        itemClick.onclick(true, contentList.get(getAdapterPosition()));
                }
            });

        }
    }

    public interface OnItemClick {
        public void onclick(boolean isLiked, ContentModel contentModel);
    }

}
