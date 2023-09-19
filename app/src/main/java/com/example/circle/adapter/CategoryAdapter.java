package com.example.circle.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.circle.R;
import com.example.circle.activity.Chat_UserList;
import com.example.circle.activity.ProfileOTP_Login;
import com.example.circle.model.CategoryModel;
import com.example.circle.model.ContentModel;
import com.example.circle.utilities.SessionManager;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyHolder> {
    private Context context;
    private List<CategoryModel> modelList;
    private List<CategoryModel> checkedValues;
    private SessionManager sessionManager;

    public CategoryAdapter(Context context, List<CategoryModel> modelList) {
        this.context = context;
        this.modelList = modelList;
        checkedValues = new ArrayList<>();
        sessionManager = new SessionManager(context);
    }

    public CategoryAdapter(Context context, List<CategoryModel> modelList, List<CategoryModel> checkedValues) {
        this.context = context;
        this.modelList = modelList;
        this.checkedValues = checkedValues;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public CategoryAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_list_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyHolder holder, int position) {
        CategoryModel model = modelList.get(position);
        if (model != null) {
            int id = model.getIcon();
            holder.image.setImageDrawable(context.getDrawable(id));
            holder.title.setText(model.getTitle());

            //in some cases, it will prevent unwanted situations
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(false);  // this was the issue as everytime load it will first deselect it and than since no data this will be deseldcted itself.
            holder.relativeLayout.setBackgroundColor(context.getColor(R.color.white));

            for (CategoryModel c : checkedValues) {
                if (c.getTitle().equalsIgnoreCase(model.getTitle())) {
                    if (c.isChecked())
                        holder.relativeLayout.setBackgroundColor(context.getColor(R.color.fade_color));
                    else
                        holder.relativeLayout.setBackgroundColor(context.getColor(R.color.white));

                    holder.checkBox.setChecked(c.isChecked());
                }

            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    model.setChecked(isChecked);

                    if (isChecked) {
                        checkedValues.add(model);
                        holder.relativeLayout.setBackgroundColor(context.getColor(R.color.fade_color));
                    } else {
//                        model.setChecked(true);
                        holder.relativeLayout.setBackgroundColor(context.getColor(R.color.white));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            checkedValues.removeIf(n -> n.getTitle().equalsIgnoreCase(model.getTitle()));
                        }
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements Serializable {
        TextView title;
        ImageView image;
        MaterialCheckBox checkBox;
        RelativeLayout relativeLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.checkboxItem);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }

    public List<CategoryModel> getCheckedValues() {
        return checkedValues;
    }

     /*  if (holder.checkBox.isChecked())
                holder.relativeLayout.setBackgroundColor(context.getColor(R.color.fade_color));
            else
                holder.relativeLayout.setBackgroundColor(context.getColor(R.color.white));*/


}
