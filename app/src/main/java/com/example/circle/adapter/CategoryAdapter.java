package com.example.circle.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyHolder> {
    private Context context;
    private List<CategoryModel> modelList;
    private List<String> checkedValues;

    public CategoryAdapter(Context context, List<CategoryModel> modelList) {
        this.context = context;
        this.modelList = modelList;
        checkedValues = new ArrayList<>();
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
            holder.image.setImageDrawable(model.getIcon());
            holder.title.setText(model.getTitle());

//            holder.relativeLayout.setOnClickListener(v -> {
////                Intent i = new Intent(context, Chat_UserList.class);
//                Intent i = new Intent(context, ProfileOTP_Login.class);
//                i.putExtra("category", model.getTitle());
//                Log.v("Chat", "category_adapter: " + model.getTitle());
//                context.startActivity(i);
//            });
        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
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

            relativeLayout.setOnClickListener(v -> {
                if (checkBox.isChecked())
                    checkBox.setChecked(false);
                else
                    checkBox.setChecked(true);
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        checkedValues.add(modelList.get(getAdapterPosition()).getTitle());
                        relativeLayout.setBackgroundColor(context.getColor(R.color.fade_color));
                    }
                    else {
                        checkedValues.remove(modelList.get(getAdapterPosition()).getTitle());
                        relativeLayout.setBackgroundColor(context.getColor(R.color.white));
                    }
                }
            });
        }
    }

    public List<String> getCheckedValues() {
        return checkedValues;
    }
}
