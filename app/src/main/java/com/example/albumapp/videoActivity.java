package com.example.albumapp;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class videoActivity extends Fragment implements ClickListener{
    videoAdapter obj_adapter;
    ArrayList<videoModel> all_videos = new ArrayList<>();
    RecyclerView recyclerView;
    View videos;
    private MainActionModeCallback actionModeCallback;
    private int checkedCount = 0;

    public static videoActivity newInstance() {
        return new videoActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        videos = inflater.inflate(R.layout.content_video, container, false);
        recyclerView = videos.findViewById(R.id.video_recyclerview);
        //change layout video in landscape and portrait
        int currentColumnLandscape = 6, currentColumnPortrait = 3;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        //display 6 videos if phone is landscape
        if (screenWidth > screenHeight) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), currentColumnLandscape));
        }
        //display 3 videos if phone is portrait
        else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), currentColumnPortrait));
        }
        recyclerView.setHasFixedSize(true);
        loadVideo();
        return videos;
    }

    public void loadVideo() {
        Cursor cursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null,"DATE_MODIFIED DESC");
        while(cursor.moveToNext()) {
            videoModel myVideoModel = new videoModel();
            myVideoModel.setStrPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
            myVideoModel.setStrThumb(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)));
            all_videos.add(myVideoModel);
        }
        cursor.close();
        obj_adapter = new videoAdapter(getContext(), all_videos);
        obj_adapter.setItemClickListener(this);
        recyclerView.setAdapter(obj_adapter);
    }

    @Override
    public void StartVideoClick(videoModel vid) {
        Intent video_intent = new Intent(getContext(), FullScreenVideoActivity.class);
        video_intent.putExtra("pathVideo", vid.getStrPath());
        startActivity(video_intent);
    }

    @Override
    public void StartVideoLongClick(videoModel vid) {
        vid.setChecked(true);
        checkedCount = 1;
        obj_adapter.setMultiCheckMode(true);
        obj_adapter.setItemClickListener(new ClickListener() {
            @Override
            public void StartVideoClick(videoModel vid) {
                vid.setChecked(!vid.isChecked());
                if(vid.isChecked()){
                    checkedCount++;
                }
                else{
                    checkedCount--;
                }
                if(checkedCount == 0){
                    actionModeCallback.getAction().finish();
                }
                actionModeCallback.setCount(checkedCount + "/" + all_videos.size());
                obj_adapter.notifyDataSetChanged();
            }

            @Override
            public void StartVideoLongClick(videoModel vid) {

            }
        });
        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_share_video:
                        Toast.makeText(getContext(), "Share multiple videos", Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                    case R.id.action_delete_video:
                        Toast.makeText(getContext(), "Delete multiple videos", Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                    default: return false;
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                obj_adapter.setMultiCheckMode(false);
                obj_adapter.setItemClickListener(videoActivity.this);
                mode.finish();
            }
        };
        getActivity().startActionMode(actionModeCallback);
        actionModeCallback.setCount(checkedCount + "/" + all_videos.size());
    }
}