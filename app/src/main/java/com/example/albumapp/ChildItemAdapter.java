package com.example.albumapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ChildItemAdapter extends RecyclerView.Adapter<ChildItemAdapter.ChildViewHolder> {
    private List<ChildItem> ChildItemList;
    private Context mContext;

    ChildItemAdapter(Context context, List<ChildItem> childItemList) {
        this.mContext = context;
        this.ChildItemList = childItemList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_child_item, viewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int position) {
        ChildItem childItem = ChildItemList.get(position);
        Glide.with(mContext)
                .load(childItem.getChildItemPath())
                .apply(RequestOptions.centerCropTransform())
                .into(childViewHolder.ChildItemTitle);
        childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Image " + childItem.getChildItemPath() + " is clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ChildItemList.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        ImageView ChildItemTitle;

        ChildViewHolder(View itemView) {
            super(itemView);
            ChildItemTitle = itemView.findViewById(R.id.img_child_item);
        }
    }
}