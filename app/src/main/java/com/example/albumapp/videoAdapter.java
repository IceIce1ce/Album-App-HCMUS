package com.example.albumapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class videoAdapter extends RecyclerView.Adapter<videoAdapter.ViewHolder> {
    ArrayList<videoModel> all_videos;
    Context mContext;
    private ClickListener listener;
    private boolean multiCheckMode = false;

    public videoAdapter(Context context, ArrayList<videoModel> all_video) {
        this.all_videos = all_video;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_image;
        private CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_image_video);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }

    @NonNull
    @Override
    public videoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull videoAdapter.ViewHolder holder, int position) {
        Glide.with(mContext).load("file://" + all_videos.get(position).getStrThumb()).skipMemoryCache(false).into(holder.iv_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.StartVideoClick(all_videos.get(position));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.StartVideoLongClick(all_videos.get(position));
                return false;
            }
        });
        if(multiCheckMode){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(all_videos.get(position).isChecked());
        }
        else{
            holder.checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return all_videos.size();
    }

    public void setItemClickListener(ClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    public ArrayList<videoModel> getCheckedVideos() {
        ArrayList<videoModel> videoListChecked = new ArrayList<>();
        for (videoModel vid: all_videos) {
            if (vid.isChecked()){
                videoListChecked.add(vid);
            }
        }
        return videoListChecked;
    }

    public void setMultiCheckMode(boolean isCheck) {
        this.multiCheckMode = isCheck;
        if (!isCheck) {
            for (videoModel vid: this.all_videos) {
                vid.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }
}