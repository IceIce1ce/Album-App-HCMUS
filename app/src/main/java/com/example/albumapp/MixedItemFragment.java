package com.example.albumapp;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.item.ImageItem;
import com.example.albumapp.item.MixedItem;
import com.example.albumapp.item.VideoItem;
import com.google.gson.Gson;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MixedItemFragment extends Fragment implements MixedItemClickListener{
    RecyclerView rv;
    ArrayList <MixedItem> item_list;
    ArrayList<String> date_order;
    MixedItemAdapter itemAdapter;
    private int checkedCount = 0;
    private MainActionModeCallback actionModeCallback;
    //public static String sort_order_date_header = "DATE_MODIFIED DESC";
    public static MixedItemFragment newInstance() {
        return new MixedItemFragment();
    }
    public static MixedItemFragment newInstance(ArrayList<String> img_list){
        System.out.println(img_list);
        MixedItemFragment f = new MixedItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("PATH_LIST", (Serializable)img_list);
        f.setArguments(args);
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null)
            this.item_list = load_mixed_item(load_file_paths(this.getActivity()));
        else {
            ArrayList<String> _list = (ArrayList<String>) args.getSerializable("PATH_LIST");
            this.item_list = load_mixed_item(_list);
        }
        MainActivity.swipeImg.setEnabled(false);
        View rootView = inflater.inflate(R.layout.date_fragment, container, false);
        rv = rootView.findViewById(R.id.parent_recyclerview);
        int currentColumnLandscape = 6, currentColumnPortrait = 3;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        //display 6 videos if phone is landscape
        if (screenWidth > screenHeight) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), currentColumnLandscape));
        }
        //display 3 videos if phone is portrait
        else {
            rv.setLayoutManager(new GridLayoutManager(getContext(), currentColumnPortrait));
        }
        //rv.setLayoutManager(new GridLayoutManager(rootView.getContext(), 3));
        itemAdapter = new MixedItemAdapter(getContext(), this.item_list);
        itemAdapter.setItemClickListener(this);
        rv.setAdapter(itemAdapter);
        return rootView;
    }

    private ArrayList<String> load_file_paths(Activity activity) {
        ArrayList<String> list = new ArrayList<>();
        //MixedItem[] list;

        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, MainActivity.sort_order_date_header); //default: null
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                if (cursor.getString(0) != null) {
                    list.add(cursor.getString(0));
                }
            }
        }
        assert cursor != null;
        cursor.close();
        //
        cursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, MainActivity.sort_order_date_header);
        while(cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
        }
        cursor.close();
        return list;

    }
    private ArrayList<MixedItem> load_mixed_item(ArrayList<String> list){
        String[] listOfFiles=list.toArray(new String[list.size()]);
        //Arrays.sort(listOfFiles, Comparator.comparingLong(File::lastModified));
        Arrays.sort(listOfFiles, new Comparator<String>() {
            public int compare(String s1, String s2) {
                // Names beginning with 's' on top
                File f1 = new File(s1), f2 = new File(s2);
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });

        List<String> list1 = Arrays.asList(listOfFiles);
        ArrayList <MixedItem> result = new ArrayList<>();
        for (String i: list1){
            if(i.endsWith(".png")||i.endsWith(".jpg")||i.endsWith(".jpeg")||i.endsWith(".gif")||i.endsWith(".PNG")||i.endsWith(".JPG")||i.endsWith(".JPEG")||i.endsWith(".GIF")){
                result.add(new ImageItem(i));
            }
            else{
                result.add(new VideoItem(i, i));
            }
        }

        if (MainActivity.sort_order_date_header.equals("DATE_MODIFIED DESC"))
            Collections.reverse(result);
        return result;
    }

    @Override
    public void StartItemClick(MixedItem item) {
        switch (item.getType()){
            case MixedItem.TYPE_IMAGE:
                Intent intent_img_date_header = new Intent(getContext(), FullScreenImageActivity.class);
                intent_img_date_header.putExtra("id", 0);//childItem.getPos()
                intent_img_date_header.putExtra("path", item.getPath());
                String img_path = item.getPath();
                ImageInfo s = new ImageInfo(img_path);
                intent_img_date_header.putExtra("display_image_name", s.getFilename());
                startActivity(intent_img_date_header);
                break;
            default:
                Intent video_intent = new Intent(getContext(), FullScreenVideoActivity.class);
                video_intent.putExtra("pathVideo", item.getPath());
                startActivity(video_intent);
        }
    }

    @Override
    public void StartItemLongClick(MixedItem item) {
        item.setChecked();
        checkedCount = 1;
        itemAdapter.setMultiCheckMode(true);
        itemAdapter.setItemClickListener(new MixedItemClickListener(){
            @Override
            public void StartItemClick(MixedItem item) {
                if(!item.isChecked()){
                    item.setChecked();
                    checkedCount++;
                }
                else {
                    item.setUnChecked();
                    checkedCount--;
                }
                if(checkedCount == 0){
                    actionModeCallback.getAction().finish();
                }
                actionModeCallback.setCount(checkedCount + "/" + item_list.size());
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void StartItemLongClick(MixedItem item) {

            }
        } );
        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_favourite_video:
                        //add multiple mixed item to wishlist
                        ArrayList<Uri> UriArrayFavouriteVideo = new ArrayList<>(),
                                UriArrayFavouriteImage = new ArrayList<>();
                        ArrayList<MixedItem> checkedFavouriteItemList = itemAdapter.getCheckedMixedItems();
                        if(checkedFavouriteItemList.size() != 0){
                            for(MixedItem item_: checkedFavouriteItemList){
                                String i = item_.getPath();
                                if(i.endsWith(".png")||i.endsWith(".jpg")||i.endsWith(".jpeg")||i.endsWith(".gif")||i.endsWith(".PNG")||i.endsWith(".JPG")||i.endsWith(".JPEG")||i.endsWith(".GIF")) {
                                    UriArrayFavouriteImage.add(Uri.parse(i));
                                }
                                else
                                    UriArrayFavouriteVideo.add(Uri.parse(i));
                            }
                        }
                        boolean isAdded = false;
                        for(int i = 0; i < UriArrayFavouriteVideo.size(); i++){
                            if (FavouriteVideoActivity.favoriteVideos != null && !FavouriteVideoActivity.favoriteVideos.isEmpty()){
                                if(FavouriteVideoActivity.favoriteVideos.contains(UriArrayFavouriteVideo.get(i).toString())){
                                    isAdded = false;
                                }
                                else{
                                    FavouriteVideoActivity.favoriteVideos.add(UriArrayFavouriteVideo.get(i).toString());
                                    isAdded = true;
                                }
                            }
                            else{
                                FavouriteVideoActivity.favoriteVideos = new ArrayList<>();
                                FavouriteVideoActivity.favoriteVideos.add(UriArrayFavouriteVideo.get(i).toString());
                                isAdded = true;
                            }



                            //todo: uncomment if open full-screen video
                            //FullScreenImageActivity.isFavouriteImage = true;
                        }
                        if(isAdded){
                            if(UriArrayFavouriteVideo.size() == 1){
                                Toast.makeText(getContext(), "Add 1 video to wishlist successfully!!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getContext(), "Add " + UriArrayFavouriteVideo.size() + " videos to wishlist successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            if(UriArrayFavouriteVideo.size() == 1){
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
                        //mode.finish();
                        //return true;
                        //add multiple images to wishlist
                        boolean isAdded2 = false;
                        for(int i = 0; i < UriArrayFavouriteImage.size(); i++){
                            if (FavouriteActivity.favoriteImages != null && !FavouriteActivity.favoriteImages.isEmpty()){
                                if(FavouriteActivity.favoriteImages.contains(UriArrayFavouriteImage.get(i).toString())){
                                    isAdded2 = false;
                                }
                                else{
                                    FavouriteActivity.favoriteImages.add(UriArrayFavouriteImage.get(i).toString());
                                    isAdded2 = true;
                                }
                            }
                            else{
                                FavouriteActivity.favoriteImages = new ArrayList<>();
                                FavouriteActivity.favoriteImages.add(UriArrayFavouriteImage.get(i).toString());
                                isAdded2 = true;
                            }
                            FullScreenImageActivity.isFavouriteImage = true;
                        }
                        if(isAdded2){
                            if(UriArrayFavouriteImage.size() == 1){
                                Toast.makeText(getContext(), "Add 1 image to wishlist successfully!!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getContext(), "Add " + UriArrayFavouriteImage.size() + " images to wishlist successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            if(UriArrayFavouriteImage.size() == 1){
                                Toast.makeText(getContext(), "This image had already added to wishlist!!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getContext(), "These image had already added to wishlist!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(FavouriteActivity.favoriteImages);
                        editor.putString("savedFavoriteImages", json);
                        editor.apply();
                        mode.finish();
                        return true;
                    case R.id.action_share_video:
                        //share multiple mixedItem
                        ArrayList<Uri> mixedUriArray = new ArrayList<>();
                        ArrayList<MixedItem> checkedVideo = itemAdapter.getCheckedMixedItems();
                        if(checkedVideo.size() != 0){
                            for(MixedItem vid: checkedVideo){
                                mixedUriArray.add(Uri.parse(vid.getPath()));
                            }
                        }
                        Intent share_intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        ArrayList<Uri> uris = new ArrayList<>();
                        for(int i = 0; i < mixedUriArray.size(); i++){
                            share_intent.setType("image/*,video/*"); //application/pdf/*|image|video/*
                            File mFile = new File(mixedUriArray.get(i).toString());
                            Uri shareFileUri = FileProvider.getUriForFile(getContext(), "com.mydomain.fileprovider", mFile);
                            uris.add(shareFileUri);
                        }
                        share_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                        startActivity(Intent.createChooser(share_intent, "Select app"));
                        mode.finish();
                        return true;
                    case R.id.action_delete_video:
                        ArrayList<Uri> UriDeleteVideos = new ArrayList<>(),
                                UriDeleteImages = new ArrayList<>();
                        ArrayList<MixedItem> checkedItemDelete = itemAdapter.getCheckedMixedItems();
                        if(checkedItemDelete.size() != 0){
                            for(MixedItem item_: checkedItemDelete){
                                String i = item_.getPath();
                                if(i.endsWith(".png")||i.endsWith(".jpg")||i.endsWith(".jpeg")||i.endsWith(".gif")||i.endsWith(".PNG")||i.endsWith(".JPG")||i.endsWith(".JPEG")||i.endsWith(".GIF"))
                                    UriDeleteImages.add(Uri.parse(i));
                                else
                                    UriDeleteVideos.add(Uri.parse(i));
                            }
                        }
                        AlertDialog dialogDeleteMixedItem = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).create();
                        dialogDeleteMixedItem.setTitle("Delete items");
                        if(checkedItemDelete.size() == 1){
                            dialogDeleteMixedItem.setMessage("Do you want to delete selected file?");
                        }
                        else{
                            dialogDeleteMixedItem.setMessage("Do you want to delete " + checkedItemDelete.size() + " files?");
                        }
                        dialogDeleteMixedItem.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //delete multi Video items from storage
                                for(int i = 0; i < checkedItemDelete.size(); i++) {
                                    File photoFile = new File(UriDeleteVideos.get(i).toString());
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
                                /*itemAdapter = new MixedItemAdapter(getContext(),
                                        checkedItemDelete);
                                itemAdapter.setItemClickListener(MixedItemFragment.this);
                                rv.setAdapter(itemAdapter);*/
                                //
                                //also delete multi videos from wishlist
                                for(int i = 0; i < UriDeleteVideos.size(); i++){
                                    FavouriteVideoActivity.favoriteVideos.remove(UriDeleteVideos.get(i).toString());
                                    SharedPreferences sharedPreferencesRemoveVideoWishList = PreferenceManager.getDefaultSharedPreferences(getContext());
                                    SharedPreferences.Editor editorRemoveVideoWishList = sharedPreferencesRemoveVideoWishList.edit();
                                    Gson gsonRemoveVideoWishList = new Gson();
                                    String jsonRemoveVideoWishList = gsonRemoveVideoWishList.toJson(FavouriteVideoActivity.favoriteVideos);
                                    editorRemoveVideoWishList.putString("savedFavoriteVideos", jsonRemoveVideoWishList);
                                    editorRemoveVideoWishList.apply();
                                }
                                //
                                Toast.makeText(getContext(), "Delete images successfully", Toast.LENGTH_SHORT).show();


                                //delete multi images from storage
                                for(int i = 0; i < UriDeleteImages.size(); i++) {
                                    File photoFile = new File(UriDeleteImages.get(i).toString());
                                    String selection = MediaStore.Images.Media.DATA + " = ?";
                                    String[] selectionArgs = new String[]{photoFile.getAbsolutePath()};
                                    ContentResolver contentResolver = getContext().getContentResolver();
                                    Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            new String[]{MediaStore.Images.Media._ID}, selection, selectionArgs, null);
                                    if (cursor != null) {
                                        if (cursor.moveToFirst()) {
                                            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
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
                                //delete multi images from current grid view
                                /*
                                for(int i = 0; i < UriDeleteImages.size(); i++){
                                    images.remove(UriDeleteImages.get(i).toString());
                                }

                                 */
                                //also delete multi images from wishlist
                                for(int i = 0; i < UriDeleteImages.size(); i++){
                                    FavouriteActivity.favoriteImages.remove(UriDeleteImages.get(i).toString());
                                    SharedPreferences sharedPreferencesRemoveWishList = PreferenceManager.getDefaultSharedPreferences(getContext());
                                    SharedPreferences.Editor editorRemoveWishList = sharedPreferencesRemoveWishList.edit();
                                    Gson gsonRemoveWishList = new Gson();
                                    String jsonRemoveWishList = gsonRemoveWishList.toJson(FavouriteActivity.favoriteImages);
                                    editorRemoveWishList.putString("savedFavoriteImages", jsonRemoveWishList);
                                    editorRemoveWishList.apply();
                                }
                                Toast.makeText(getContext(), "Delete images successfully", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(MixedItemFragment.this.getId(), new MixedItemFragment()).commit();
//delete multi videos from current recyclerview
                                /*itemAdapter = new MixedItemAdapter(getContext(),
                                        checkedItemDelete);
                                itemAdapter.setItemClickListener(MixedItemFragment.this);
                                rv.setAdapter(itemAdapter);*/
                            }
                        });
                        dialogDeleteMixedItem.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialogDeleteMixedItem.show();
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
                itemAdapter.setMultiCheckMode(false);
                itemAdapter.setItemClickListener(MixedItemFragment.this);
                mode.finish();
            }
        };
        getActivity().startActionMode(actionModeCallback);
        actionModeCallback.setCount(checkedCount + "/" + item_list.size());
    }
}
