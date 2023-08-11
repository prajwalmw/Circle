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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    Context context;
    ArrayList<ContentModel> contentList;
    String category_value;
    ContentAdapter.OnItemClick itemClick;

    public ContentAdapter(Context context, ArrayList<ContentModel> contentList, ContentAdapter.OnItemClick itemClick) {
        this.context = context;
        this.contentList = contentList;
        this.itemClick = itemClick;
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
        Collections.sort(contentList, new Comparator<ContentModel>() {
            @Override
            public int compare(ContentModel model_1, ContentModel model_2) {
                return Integer.compare(model_2.getContentHeartCount(), model_1.getContentHeartCount());
            }
        });
        // end

        ContentModel contentModel = contentList.get(position);
        if (contentModel.getContentHeartCount() > 0)
            holder.like_btn.setImageDrawable(context.getDrawable(R.drawable.like_heart_filled));

        holder.contentTitle.setText(contentModel.getContenTitle());
        holder.contentLikeCount.setText(String.valueOf(contentModel.getContentHeartCount()));
        Glide.with(context)
                .asBitmap()
                .load(contentModel.getContentImageUrl())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.content_imageview);

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
        TextView contentTitle, contentLikeCount;
        ImageView content_imageview;
        ImageButton like_btn;
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentTitle = itemView.findViewById(R.id.content_title);
            contentLikeCount = itemView.findViewById(R.id.content_like_count);
            content_imageview = itemView.findViewById(R.id.content_imageview);
            like_btn = itemView.findViewById(R.id.like_btn);

            like_btn.setOnClickListener(v -> {
                if (like_btn.getTag().equals("0")) {
                    like_btn.setImageDrawable(context.getDrawable(R.drawable.like_heart_filled));
                    like_btn.setTag("1");
                    itemClick.onclick(true, contentList.get(getAdapterPosition()));
                }
                else {
                    like_btn.setImageDrawable(context.getDrawable(R.drawable.like_heart_unfilled));
                    like_btn.setTag("0");
                    itemClick.onclick(false, contentList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClick {
        public void onclick(boolean isLiked, ContentModel contentModel);
    }

}
