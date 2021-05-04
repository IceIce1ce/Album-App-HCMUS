package com.example.albumapp.item;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.albumapp.FullScreenImageActivity;
import com.example.albumapp.FullScreenVideoActivity;
import com.example.albumapp.ImageInfo;
import com.example.albumapp.MainActivity;
import com.example.albumapp.R;

import java.util.ArrayList;
import java.util.List;

public class NewChildItemAdapter extends RecyclerView.Adapter {
    private List<MixedItem> ChildItemList;
    private Context context;
    private boolean multiSelect = false;
    private ArrayList<String> selectlist = new ArrayList<>();
    //private ActionModeCallback actionModeCallback = new ActionModeCallback();
    //private ActionMode actionMode;
    public NewChildItemAdapter(Context context, List<MixedItem> childItemList) {
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
        //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_child_item, viewGroup, false);
        //return new NewChildItemAdapter.NewChildViewHolder(view);
        View itemView;
        switch (viewType){
            case MixedItem.TYPE_IMAGE:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_image_item, viewGroup, false);
                return new ImageViewHolder(itemView);
            default: // MixedItem.TYPE_VIDEO:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_video_item, viewGroup, false);
                return new VideoViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)){
            case MixedItem.TYPE_IMAGE:
                ((ImageViewHolder) holder).bindView(position);
                ImageItem cur = ((ImageItem) ChildItemList.get(position));
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(!multiSelect){
                            multiSelect = true;
                            ((ImageViewHolder) holder).ImgCheckbox.setVisibility(View.VISIBLE);
                            ((ImageViewHolder) holder).ImgCheckbox.setChecked(true);
                            ((ImageViewHolder) holder).ImgTitle.setAlpha(0.3f);
                            selectlist.add(cur.getPath());
                        }
                        return true;
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixedItem childItem = ChildItemList.get(position);
                        Intent intent_img_date_header = new Intent(context, FullScreenImageActivity.class);
                        intent_img_date_header.putExtra("id", childItem.getPos());
                        intent_img_date_header.putExtra("path", childItem.getPath());
                        String img_path = childItem.getPath();
                        ImageInfo s = new ImageInfo(img_path);
                        intent_img_date_header.putExtra("display_image_name", s.getFilename());
                        context.startActivity(intent_img_date_header);
                        /*
                        if (multiSelect) {
                            if (!selectlist.contains(cur.getPath())) {
                                ((ImageViewHolder) holder).ImgCheckbox.setVisibility(View.VISIBLE);
                                ((ImageViewHolder) holder).ImgCheckbox.setChecked(true);
                                ((ImageViewHolder) holder).ImgTitle.setAlpha(0.3f);
                                selectlist.add(cur.getPath());
                            } else {
                                ((ImageViewHolder) holder).ImgCheckbox.setVisibility(View.INVISIBLE);
                                ((ImageViewHolder) holder).ImgCheckbox.setChecked(false);
                                ((ImageViewHolder) holder).ImgTitle.setAlpha(1f);
                                selectlist.remove(cur.getPath());
                            }
                        }
                        else {

                            Toast.makeText(context, cur.getPath() + "\nPos: "+ cur.getPos(),Toast.LENGTH_SHORT).show();
                        }*/
                    }
                });
                break;
            default:
                ((VideoViewHolder) holder).bindView(position);
                VideoItem cur_vid = ((VideoItem) ChildItemList.get(position));
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(!multiSelect){
                            multiSelect = true;
                            ((VideoViewHolder) holder).VideoCheckbox.setVisibility(View.VISIBLE);
                            ((VideoViewHolder) holder).VideoCheckbox.setChecked(true);
                            ((VideoViewHolder) holder).VideoTitle.setAlpha(0.3f);
                            selectlist.add(cur_vid.getPath());
                        }
                        return true;
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent video_intent = new Intent(context, FullScreenVideoActivity.class);
                        video_intent.putExtra("pathVideo", cur_vid.getPath());
                        context.startActivity(video_intent);
                        /*
                        if(multiSelect) {
                            if (!selectlist.contains(cur_vid.getPath())) {
                                ((VideoViewHolder) holder).VideoCheckbox.setVisibility(View.VISIBLE);
                                ((VideoViewHolder) holder).VideoCheckbox.setChecked(true);
                                ((VideoViewHolder) holder).VideoTitle.setAlpha(0.3f);
                                selectlist.add(cur_vid.getPath());
                            } else {
                                ((VideoViewHolder) holder).VideoCheckbox.setVisibility(View.INVISIBLE);
                                ((VideoViewHolder) holder).VideoCheckbox.setChecked(false);
                                ((VideoViewHolder) holder).VideoTitle.setAlpha(1f);
                                selectlist.remove(cur_vid.getPath());
                            }
                        }
                        else{
                            Toast.makeText(context, cur_vid.getThumbnail() + "\nPos: "+ cur_vid.getPos(),Toast.LENGTH_SHORT).show();
                        }

                         */
                    }
                });
                break;
        }

    }

    ArrayList<String> getSelectedList(){
        return this.selectlist;
    }
/*
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.multiSelect = false;
        this.selectlist.clear();
        notifyDataSetChanged();
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.new_multiselect_menu, menu);
            multiSelect = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.favorite:
                    // TODO: actually remove items
                    Log.d(TAG, "menu_remove");
                    mode.finish();
                    return true;

                default:
                    return false;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectlist.clear();
            notifyDataSetChanged();
        }
    }
    */

    @Override
    public int getItemCount() {
        return ChildItemList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ImgTitle;
        CheckBox ImgCheckbox;
        ImageViewHolder(View itemView) {
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
    class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView VideoTitle;
        CheckBox VideoCheckbox;
        VideoViewHolder(View itemView) {
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