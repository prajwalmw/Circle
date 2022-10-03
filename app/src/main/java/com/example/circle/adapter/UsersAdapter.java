package com.example.circle.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.circle.R;
import com.example.circle.activity.ChatActivity;
import com.example.circle.databinding.RowConversationBinding;
import com.example.circle.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    Context context;
    ArrayList<User> users;
    String category_value;

    public UsersAdapter(Context context, ArrayList<User> users, String category_value) {
        this.context = context;
        this.users = users;
        this.category_value = category_value;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUid();

      //  for (int i = 0; i < category_value.size(); i++) {
      //      String category = category_value.get(i);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(category_value)
                    .child(senderRoom)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                long time = snapshot.child("lastMsgTime").getValue(Long.class);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                holder.binding.msgTime.setText(dateFormat.format(new Date(time)));
                                //  holder.binding.msgTime.setTextColor(context.getResources().getColor(R.color.black));

                                if (user.isIsblocked()) {
                                    holder.binding.lastMsg.setText(Html.fromHtml("<b>You have blocked this user!</b>"));
                                    holder.binding.lastMsg.setTextColor(context.getColor(R.color.red));
                                } else
                                    holder.binding.lastMsg.setText(lastMsg);

                            } else {
                                holder.binding.lastMsg.setText("Tap to chat");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
      //  }


        holder.binding.username.setText(user.getName());

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);


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

//                Bundle args = new Bundle();
//                args.putSerializable("category_list", (Serializable) category_value);
//                intent.putExtra("BUNDLE",args);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        RowConversationBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }

}
