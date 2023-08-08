package com.example.circle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    Context context;
    ArrayList<ContentModel> contentList;
    String category_value;

    public ContentAdapter(Context context, ArrayList<ContentModel> contentList) {
        this.context = context;
        this.contentList = contentList;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_listitem, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        ContentModel contentModel = contentList.get(position);

        holder.contentTitle.setText(contentModel.getContenTitle());
        holder.contentLikeCount.setText(contentModel.getContentHeartCount());
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
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentTitle = itemView.findViewById(R.id.content_title);
            contentLikeCount = itemView.findViewById(R.id.content_like_count);
            content_imageview = itemView.findViewById(R.id.content_imageview);
        }
    }

}
