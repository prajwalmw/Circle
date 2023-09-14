package com.example.circle.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.circle.R;
import com.example.circle.activity.FullscreenImageActivity;
import com.example.circle.model.ContentModel;
import com.example.circle.utilities.OnItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyHolder> {
    private Context context;
    private ArrayList<ContentModel> contentModelArrayList = new ArrayList<>();
    HashSet<ContentModel> top3ContentList;
    private OnItemClickListener listener;

    public LeaderboardAdapter(Context context, ArrayList<ContentModel> contentModelArrayList, HashSet<ContentModel> top3ContentList, OnItemClickListener listener) {
        this.context = context;
        this.contentModelArrayList = contentModelArrayList;
        this.top3ContentList = top3ContentList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public LeaderboardAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_listitem, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.MyHolder holder, int position) {
        ContentModel model = contentModelArrayList.get(holder.getAdapterPosition());
        holder.rank.setText(String.valueOf(holder.getAdapterPosition() + 1));
        holder.rank_name.setText(model.getUserName());

        Glide.with(context)
                .asBitmap()
                .load(model.getContentImageUrl())
                .placeholder(R.drawable.avatar)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .circleCrop()
                .into(holder.rank_image);

        holder.rank_likes_count.setText(model.getContentHeartCount() + "+");
        holder.rank_category.setText(model.getCategory_value());

        holder.rank_image.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullscreenImageActivity.class);
            intent.putExtra("model", model);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return contentModelArrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView rank, rank_name, rank_likes_count, rank_category;
        CircleImageView rank_image;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            rank = itemView.findViewById(R.id.rank);
            rank_name = itemView.findViewById(R.id.rank_name);
            rank_likes_count = itemView.findViewById(R.id.rank_likes_count);
            rank_image = itemView.findViewById(R.id.rank_image);
            rank_category = itemView.findViewById(R.id.rank_category);
        }
    }
}
