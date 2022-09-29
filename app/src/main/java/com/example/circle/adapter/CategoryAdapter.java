package com.example.circle.adapter;

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
import com.example.circle.activity.CategoryActivity;
import com.example.circle.activity.Chat_UserList;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyHolder> {
    private Context context;

    public CategoryAdapter(Context context) {
        this.context = context;

        List<String> iconlist = new ArrayList<>();
        List<String> titleList = new ArrayList<>();

    }

    @NonNull
    @Override
    public CategoryAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_list_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyHolder holder, int position) {

        holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icons8_romance_48));
        holder.title.setText("Dating");

        holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.movies));
        holder.title.setText("Movies");

        holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.barchart));
        holder.title.setText("Business");

        holder.relativeLayout.setOnClickListener(v -> {
            Intent i = new Intent(context, Chat_UserList.class);
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        RelativeLayout relativeLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
