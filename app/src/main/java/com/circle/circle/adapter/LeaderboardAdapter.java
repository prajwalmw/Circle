package com.circle.circle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.circle.circle.R;
import com.circle.circle.model.ContentModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyHolder> {
    private Context context;
    private List<ContentModel> contentModelArrayList = new ArrayList<>();
    List<ContentModel> top3ContentList;
    private LeaderboardAdapter.OnItemClickListener listener;

    public LeaderboardAdapter(Context context, List<ContentModel> contentModelArrayList,
                              List<ContentModel> top3ContentList,
                              LeaderboardAdapter.OnItemClickListener listener) {
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
            listener.onItemClick(model);

            /*if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

            Intent intent = new Intent(context, FullscreenImageActivity.class);
            intent.putExtra("model", model);
            context.startActivity(intent);
            */

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

    public interface OnItemClickListener {

        public void onItemClick(ContentModel model);
    }
}
