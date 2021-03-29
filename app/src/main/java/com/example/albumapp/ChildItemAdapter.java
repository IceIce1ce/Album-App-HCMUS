package com.example.albumapp;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ChildItemAdapter extends RecyclerView.Adapter<ChildItemAdapter.ChildViewHolder> {
    private List<ChildItem> ChildItemList;
    private Context mContext;
    // Constuctor
    ChildItemAdapter(Context context, List<ChildItem> childItemList)
    {
        this.mContext = context;
        this.ChildItemList = childItemList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // Here we inflate the corresponding
        // layout of the child item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_child_item, viewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int position) {

        // Create an instance of the ChildItem
        // class for the given position
        ChildItem childItem = ChildItemList.get(position);

        // For the created instance, set title.
        // No need to set the image for
        // the ImageViews because we have
        // provided the source for the images
        // in the layout file itself
        //childViewHolder.ChildItemTitle.setText(childItem.getChildItemPath());
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



    // This class is to initialize
    // the Views present
    // in the child RecyclerView

    class ChildViewHolder extends RecyclerView.ViewHolder {
        ImageView ChildItemTitle;
        ChildViewHolder(View itemView) {
            super(itemView);
            ChildItemTitle = itemView.findViewById(R.id.img_child_item);
        }
    }
}