package com.example.circle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.circle.R;
import com.example.circle.model.ListItemModel;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {
    private Context context;
    private List<ListItemModel> listItemModelList;

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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);

            image.setVisibility(View.GONE);

        }
    }
}
