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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.albumapp.item.ImageItem;
import com.example.albumapp.item.MixedItem;
import com.example.albumapp.item.VideoItem;

import java.util.ArrayList;
import java.util.List;


public class MixedItemAdapter extends RecyclerView.Adapter{
    private List<MixedItem> ChildItemList;
    private Context context;
    private boolean multiSelectMode = false;
    private ArrayList<String> selectlist = new ArrayList<>();
    private MixedItemClickListener listener;

    //private ActionModeCallback actionModeCallback = new ActionModeCallback();
    //private ActionMode actionMode;
    public MixedItemAdapter(Context context, List<MixedItem> childItemList) {
        this.context = context;
        this.ChildItemList = childItemList;
    }
    @Override
    public int getItemViewType(int position) {
        return ChildItemList.get(position).getType();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        switch (viewType){
            case MixedItem.TYPE_IMAGE:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_image_item, viewGroup, false);
                return new ImageHolder(itemView);
            default: // MixedItem.TYPE_VIDEO:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_video_item, viewGroup, false);
                return new VideoHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.StartItemClick(ChildItemList.get(position));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.StartItemLongClick(ChildItemList.get(position));
                return false;
            }
        });

        switch (getItemViewType(position)){
            case MixedItem.TYPE_IMAGE:
                ((ImageHolder) holder).bindView(position);
                if(multiSelectMode){
                    ((ImageHolder) holder).ImgCheckbox.setVisibility(View.VISIBLE);
                    ((ImageHolder) holder).ImgCheckbox.setChecked(ChildItemList.get(position).isChecked());
                }
                else{
                    ((ImageHolder) holder).ImgCheckbox.setVisibility(View.GONE);
                }
                break;
            default:
                ((VideoHolder) holder).bindView(position);
                ((VideoHolder) holder).bindView(position);
                if(multiSelectMode){
                    ((VideoHolder) holder).VideoCheckbox.setVisibility(View.VISIBLE);
                    ((VideoHolder) holder).VideoCheckbox.setChecked(ChildItemList.get(position).isChecked());
                }
                else{
                    ((VideoHolder) holder).VideoCheckbox.setVisibility(View.GONE);
                }
        }
    }

    ArrayList<String> getSelectedList() {
        return this.selectlist;
    }
    public void setItemClickListener(MixedItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }
    @Override
    public int getItemCount() {
        return ChildItemList.size();
    }
    public ArrayList<MixedItem> getCheckedMixedItems() {
        ArrayList<MixedItem> result = new ArrayList<>();
        for (MixedItem i: this.ChildItemList){
            if (i.isChecked()){
                result.add(i);
            }
        }
        return result;
    }
    public void setMultiCheckMode(boolean isCheck) {
        this.multiSelectMode = isCheck;
        if (!isCheck) {
            for (MixedItem i: this.ChildItemList) {
                i.setUnChecked();
            }
        }
        notifyDataSetChanged();
    }
    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView ImgTitle;
        CheckBox ImgCheckbox;
        ImageHolder(View itemView) {
            super(itemView);
            ImgCheckbox = itemView.findViewById(R.id.img_checkBox);
            ImgTitle = itemView.findViewById(R.id.img_IV);
        }
        void bindView(int pos){
            ImageItem img = (ImageItem) ChildItemList.get(pos);
            Glide.with(context)
                    .load(img.getPath())
                    .apply(RequestOptions.centerCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(800, 800)
                    .into(ImgTitle);
        }
    }
    class VideoHolder extends RecyclerView.ViewHolder {
        ImageView VideoTitle;
        CheckBox VideoCheckbox;
        VideoHolder(View itemView) {
            super(itemView);
            VideoTitle = itemView.findViewById(R.id.vid_IV);
            VideoCheckbox = itemView.findViewById(R.id.vid_checkBox);
        }
        void bindView(int pos){
            VideoItem vid = (VideoItem) ChildItemList.get(pos);
            String vid_path = vid.getPath(),
                    thumbnail_path = vid.getThumbnail();
            Glide.with(context)
                    .load(thumbnail_path)
                    .apply(RequestOptions.centerCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(800, 800)
                    .into(VideoTitle);
        }
    }
}