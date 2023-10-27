package com.circle.circle.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.circle.circle.R;
import com.circle.circle.model.ListItemModel;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {
    private Context context;
    private List<ListItemModel> listItemModelList;
    public static final String ABOUT_US = "https://circlecommunityhey.wixsite.com/circle-community";
    public static final String PRIVACY_POLICY = "https://circlecommunityhey.wixsite.com/circle-community/privacy-policy";
    public static final String TERMS_USE = "https://circlecommunityhey.wixsite.com/circle-community/terms-conditions";
    public static final String FAQ = "https://circlecommunityhey.wixsite.com/circle-community/faq";


    public SettingsAdapter(Context context, List<ListItemModel> listItemModelList) {
        this.context = context;
        this.listItemModelList = listItemModelList;
    }

    @NonNull
    @Override
    public SettingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.MyViewHolder holder, int position) {
        ListItemModel model = listItemModelList.get(position);
        if (model != null) {
          //  holder.image.setImageDrawable(context.getDrawable(model.getIcon()));
            holder.title.setText(model.getOptionName());

            switch (position) {
                case 0: {
                    holder.relativeLayout.setOnClickListener(v -> {
                        Uri url = Uri.parse(ABOUT_US);
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, url);

                        try {
                            context.startActivity(likeIng);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
                case 1: {
                    holder.relativeLayout.setOnClickListener(v -> {
                        Uri url = Uri.parse(PRIVACY_POLICY);
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, url);

                        try {
                            context.startActivity(likeIng);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
                case 2: {
                    holder.relativeLayout.setOnClickListener(v -> {
                        Uri url = Uri.parse(TERMS_USE);
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, url);

                        try {
                            context.startActivity(likeIng);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
                case 3: {
                    holder.relativeLayout.setOnClickListener(v -> {
                        Uri url = Uri.parse(FAQ);
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, url);

                        try {
                            context.startActivity(likeIng);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return listItemModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;
        TextView title;
        ImageView image;
        ImageButton arrow_move;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            arrow_move = itemView.findViewById(R.id.arrow_move);
            arrow_move.setVisibility(View.GONE);

            image.setVisibility(View.GONE);

        }
    }
}
