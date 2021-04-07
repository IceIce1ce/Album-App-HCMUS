package com.example.albumapp;

import android.app.AlertDialog;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class videoActivity extends Fragment implements ClickListener{
    videoAdapter vid_adapter;
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
        MainActivity.swipeImg.setEnabled(true);
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
        vid_adapter = new videoAdapter(getContext(), all_videos);
        vid_adapter.setItemClickListener(this);
        recyclerView.setAdapter(vid_adapter);
    }

    @Override
    public void StartVideoClick(videoModel vid) {
        VideoInfo s = new VideoInfo(vid.getStrPath());
        //convert lat and long of video location to address
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        String realAddressImg = "unknown";
        try{
            addresses = geocoder.getFromLocation(s.getLatLocation(), s.getLongLocation(), 1);
            realAddressImg = addresses.get(0).getAddressLine(0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String DetailsVideo = "Name\n" + s.getFilename() + "\n\nPath\n" + vid.getStrPath() + "\n\nSize\n" + s.getSize() + "\n\nDuration\n" + s.getDuration()
                + "\n\nResolution\n" + s.getResolution()
                + "\n\nDate\n" + s.getDate()
                + "\n\nLocation\n" + realAddressImg;
        TextView title = new TextView(getContext());
        title.setPadding(60, 30, 0, 0);
        title.setText("Properties");
        title.setTextSize(18.0f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).create();
        dialog.setCustomTitle(title);
        dialog.setMessage(DetailsVideo);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
        /*
        Intent video_intent = new Intent(getContext(), FullScreenVideoActivity.class);
        video_intent.putExtra("pathVideo", vid.getStrPath());
        startActivity(video_intent);*/
    }

    @Override
    public void StartVideoLongClick(videoModel vid) {
        vid.setChecked(true);
        checkedCount = 1;
        vid_adapter.setMultiCheckMode(true);
        vid_adapter.setItemClickListener(new ClickListener() {
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
                vid_adapter.notifyDataSetChanged();
            }

            @Override
            public void StartVideoLongClick(videoModel vid) {

            }
        });
        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_favourite_video:
                        //add multiple videos to wishlist
                        ArrayList<Uri> videoUriArrayFavourite = new ArrayList<>();
                        ArrayList<videoModel> checkedVideoFavourite = vid_adapter.getCheckedVideos();
                        if(checkedVideoFavourite.size() != 0){
                            for(videoModel vidFavourite: checkedVideoFavourite){
                                videoUriArrayFavourite.add(Uri.parse(vidFavourite.getStrPath()));
                            }
                        }
                        boolean isAdded = false;
                        for(int i = 0; i < videoUriArrayFavourite.size(); i++){
                            if (FavouriteVideoActivity.favoriteVideos != null && !FavouriteVideoActivity.favoriteVideos.isEmpty()){
                                if(FavouriteVideoActivity.favoriteVideos.contains(videoUriArrayFavourite.get(i).toString())){
                                    isAdded = false;
                                }
                                else{
                                    FavouriteVideoActivity.favoriteVideos.add(videoUriArrayFavourite.get(i).toString());
                                    isAdded = true;
                                }
                            }
                            else{
                                FavouriteVideoActivity.favoriteVideos = new ArrayList<>();
                                FavouriteVideoActivity.favoriteVideos.add(videoUriArrayFavourite.get(i).toString());
                                isAdded = true;
                            }
                            //todo: uncomment if open full-screen video
                            //FullScreenImageActivity.isFavouriteImage = true;
                        }
                        if(isAdded){
                            if(videoUriArrayFavourite.size() == 1){
                                Toast.makeText(getContext(), "Add 1 video to wishlist successfully!!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getContext(), "Add " + videoUriArrayFavourite.size() + " videos to wishlist successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            if(videoUriArrayFavourite.size() == 1){
                                Toast.makeText(getContext(), "This video had already added to wishlist!!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getContext(), "These video had already added to wishlist!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        SharedPreferences sharedPreferencesVideo = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editorVideo = sharedPreferencesVideo.edit();
                        Gson gsonVideo = new Gson();
                        String jsonVideo = gsonVideo.toJson(FavouriteVideoActivity.favoriteVideos);
                        editorVideo.putString("savedFavoriteVideos", jsonVideo);
                        editorVideo.apply();
                        mode.finish();
                        return true;
                    case R.id.action_share_video:
                        //share multiple videos
                        ArrayList<Uri> videoUriArray = new ArrayList<>();
                        ArrayList<videoModel> checkedVideo = vid_adapter.getCheckedVideos();
                        if(checkedVideo.size() != 0){
                            for(videoModel vid: checkedVideo){
                                videoUriArray.add(Uri.parse(vid.getStrPath()));
                            }
                        }
                        Intent share_intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        ArrayList<Uri> uris = new ArrayList<>();
                        for(int i = 0; i < videoUriArray.size(); i++){
                            share_intent.setType("video/*"); //application/pdf/*|image|video/*
                            File mFile = new File(videoUriArray.get(i).toString());
                            Uri shareFileUri = FileProvider.getUriForFile(getContext(), "com.mydomain.fileprovider", mFile);
                            uris.add(shareFileUri);
                        }
                        share_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                        startActivity(Intent.createChooser(share_intent, "Select app"));
                        mode.finish();
                        return true;
                    case R.id.action_delete_video:
                        ArrayList<Uri> videoUriArrayDelete = new ArrayList<>();
                        ArrayList<videoModel> checkedVideoDelete = vid_adapter.getCheckedVideos();
                        if(checkedVideoDelete.size() != 0){
                            for(videoModel vidDelete: checkedVideoDelete){
                                videoUriArrayDelete.add(Uri.parse(vidDelete.getStrPath()));
                            }
                        }
                        AlertDialog dialogDeleteVideo = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).create();
                        dialogDeleteVideo.setTitle("Delete video");
                        if(checkedVideoDelete.size() == 1){
                            dialogDeleteVideo.setMessage("Do you want to delete this video?");
                        }
                        else{
                            dialogDeleteVideo.setMessage("Do you want to delete " + checkedVideoDelete.size() + " videos?");
                        }
                        dialogDeleteVideo.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //delete multi videos from storage
                                for(int i = 0; i < checkedVideoDelete.size(); i++) {
                                    File photoFile = new File(videoUriArrayDelete.get(i).toString());
                                    String selection = MediaStore.Video.Media.DATA + " = ?";
                                    String[] selectionArgs = new String[]{photoFile.getAbsolutePath()};
                                    ContentResolver contentResolver = getContext().getContentResolver();
                                    Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                            new String[]{MediaStore.Video.Media._ID}, selection, selectionArgs, null);
                                    if (cursor != null) {
                                        if (cursor.moveToFirst()) {
                                            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                                            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                                                contentResolver.delete(deleteUri, null, null);
                                            }
                                            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                                try{
                                                    contentResolver.delete(deleteUri, null, null);
                                                }
                                                catch(RecoverableSecurityException ex){
                                                    final IntentSender intentSender = ex.getUserAction().getActionIntent().getIntentSender();
                                                    try {
                                                        getActivity().startIntentSenderForResult(intentSender, 1111, null, 0, 0, 0, null);
                                                    }
                                                    catch (IntentSender.SendIntentException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                        cursor.close();
                                    }
                                }
                                //delete multi videos from current recyclerview
                                vid_adapter = new videoAdapter(getContext(), checkedVideoDelete);
                                vid_adapter.setItemClickListener(videoActivity.this);
                                recyclerView.setAdapter(vid_adapter);
                                //
                                //also delete multi videos from wishlist
                                for(int i = 0; i < videoUriArrayDelete.size(); i++){
                                    FavouriteVideoActivity.favoriteVideos.remove(videoUriArrayDelete.get(i).toString());
                                    SharedPreferences sharedPreferencesRemoveVideoWishList = PreferenceManager.getDefaultSharedPreferences(getContext());
                                    SharedPreferences.Editor editorRemoveVideoWishList = sharedPreferencesRemoveVideoWishList.edit();
                                    Gson gsonRemoveVideoWishList = new Gson();
                                    String jsonRemoveVideoWishList = gsonRemoveVideoWishList.toJson(FavouriteVideoActivity.favoriteVideos);
                                    editorRemoveVideoWishList.putString("savedFavoriteVideos", jsonRemoveVideoWishList);
                                    editorRemoveVideoWishList.apply();
                                }
                                //
                                Toast.makeText(getContext(), "Delete images successfully", Toast.LENGTH_SHORT).show();
                                mode.finish();
                            }
                        });
                        dialogDeleteVideo.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialogDeleteVideo.show();
                        //mode.finish();
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
                vid_adapter.setMultiCheckMode(false);
                vid_adapter.setItemClickListener(videoActivity.this);
                mode.finish();
            }
        };
        getActivity().startActionMode(actionModeCallback);
        actionModeCallback.setCount(checkedCount + "/" + all_videos.size());
    }
}