package com.example.albumapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ChildItemAdapter extends RecyclerView.Adapter<ChildItemAdapter.ChildViewHolder> {
    private List<ChildItem> ChildItemList;
    private Context context;

    ChildItemAdapter(Context context, List<ChildItem> childItemList) {
        this.context = context;
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
        Glide.with(context)
                .load(childItem.getChildItemPath())
                .apply(RequestOptions.centerCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(800, 800)
                .into(childViewHolder.ChildItemTitle);
        childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_img_date_header = new Intent(context, FullScreenImageActivity.class);
                intent_img_date_header.putExtra("id", childItem.getPos());
                intent_img_date_header.putExtra("path", childItem.getChildItemPath());
                String img_path = childItem.getChildItemPath();
                ImageInfo s = new ImageInfo(img_path);
                intent_img_date_header.putExtra("display_image_name", s.getFilename());
                context.startActivity(intent_img_date_header);
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