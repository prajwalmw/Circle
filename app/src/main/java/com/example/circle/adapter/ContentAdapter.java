package com.example.circle.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.circle.R;
import com.example.circle.activity.ChatActivity;
import com.example.circle.databinding.RowConversationBinding;
import com.example.circle.model.ContentModel;
import com.example.circle.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

/*
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);
*/

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
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentTitle = itemView.findViewById(R.id.content_title);
            contentLikeCount = itemView.findViewById(R.id.content_like_count);
        }
    }

}
