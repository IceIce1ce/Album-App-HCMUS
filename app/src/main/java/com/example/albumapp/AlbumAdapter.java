package com.example.albumapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    ArrayList<AlbumItem> albumItemlist;
    Context context;
    public AlbumAdapter(Context context, ArrayList<AlbumItem> albumItemlist) {
        this.albumItemlist = albumItemlist;
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.AlbumViewHolder holder, int position) {
        AlbumItem item = albumItemlist.get(position);
        ArrayList<String> img_list = item.getAlbum_path_list();
        if (item.getIs_sd())
            holder.sdcard_img.setVisibility(View.VISIBLE);
        else
            holder.sdcard_img.setVisibility(View.INVISIBLE);
        holder.album_title.setText(item.getAlbum_name());
        holder.album_img_number.setText(String.valueOf(img_list.size()) + " " + context.getResources().getString(R.string.album_file_number));
        Glide.with(context)
                .load(img_list.get(0))
                .apply(RequestOptions.centerCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(800, 800)
                .into(holder.album_img);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, img_list.get(0),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, AlbumDateActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable)img_list);
                intent.putExtra("BUNDLE",args);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.albumItemlist.size();
    }
    class AlbumViewHolder extends RecyclerView.ViewHolder{
        TextView album_title, album_img_number;
        ImageView album_img, sdcard_img;
        AlbumViewHolder(View itemView){
            super(itemView);
            album_img_number = itemView.findViewById(R.id.album_img_number);
            album_img = itemView.findViewById(R.id.album_image);
            album_title = itemView.findViewById(R.id.album_title);
            sdcard_img = itemView.findViewById(R.id.album_is_sdcard);
        }
    }
}
