package com.example.circle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.circle.R;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyHolder> {
    private Context context;

    public LeaderboardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LeaderboardAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_listitem, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
