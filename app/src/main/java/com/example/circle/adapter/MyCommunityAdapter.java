package com.example.circle.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.circle.R;
import com.example.circle.activity.Chat_UserList;
import com.example.circle.activity.MyCommunity;
import com.example.circle.model.CategoryModel;

import java.util.List;

public class MyCommunityAdapter extends RecyclerView.Adapter<MyCommunityAdapter.MyHolder> {
    private Context context;
    private List<CategoryModel> categoryList;

    public MyCommunityAdapter(Context context, List<CategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyCommunityAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mycommunity_list_item, parent, false);
        return new MyCommunityAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCommunityAdapter.MyHolder holder, int position) {
        CategoryModel model = categoryList.get(position);
        if (model != null) {
            int id = model.getIcon();
            holder.image.setImageDrawable(context.getDrawable(id));
            holder.title.setText(model.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;
        TextView title;
        ImageView image;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);

            relativeLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, Chat_UserList.class);
                intent.putExtra("category", categoryList.get(getAdapterPosition()).getTitle());
                context.startActivity(intent);
            });
        }

    }
}
