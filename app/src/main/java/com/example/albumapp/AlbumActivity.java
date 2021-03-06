package com.example.albumapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

public class AlbumActivity extends Fragment {
    private RecyclerView rv;
    public static ArrayList<AlbumItem> album_list = null;
    public static String album_sort_order = "DATE_MODIFIED DESC"; // DATE_MODIFIED ASC
    ArrayList<String> img_dir = null;

    public static AlbumActivity newInstance() { return new AlbumActivity();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.swipeImg.setEnabled(false);
        if(this.img_dir == null){
            this.img_dir = load_paths(this.getActivity());
        }
        this.album_list = loadAlbumItem(process_loaded_paths(this.img_dir));
        View rootView = inflater.inflate(R.layout.album_layout, container, false);
        rv = rootView.findViewById(R.id.album_recyclerview);
        rv.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
        AlbumAdapter albumAdapter = new AlbumAdapter(getContext() , this.album_list);
        rv.setAdapter(albumAdapter);
        return rootView;
    }

    public static ArrayList<AlbumItem> exportAlbumList(Activity activity){
        return loadAlbumItem(process_loaded_paths(load_paths(activity)));
    }
    private  static ArrayList<AlbumItem> loadAlbumItem(TreeMap<String, ArrayList<String>> input){
        Set<String> keys = input.keySet();
        ArrayList<AlbumItem> result = new ArrayList<>();
        for(String i : keys){
            ArrayList<String> list = input.get(i);
            if(list.isEmpty())
                continue;
            else {
                String item = list.get(0);
                String delims = "[/]";
                String[] tokens = item.trim().split(delims);
                String sec_dir = tokens[2];
                AlbumItem new_album = new AlbumItem(i, input.get(i));
                if (!sec_dir.equals("emulated"))
                    new_album.setIs_sd(true);
                result.add(new_album);
            }
        }
        return result;
    }


    private  static TreeMap<String, ArrayList<String>> process_loaded_paths(ArrayList<String> input){
        TreeMap<String, ArrayList<String>> result = new TreeMap<>();
        for(String i : input){
            String delims = "[/]";
            String[] tokens = i.trim().split(delims);
            String img_folder = tokens[tokens.length - 2];
            //String img_filename = tokens[tokens.length - 1];
            if (!result.containsKey(img_folder)) {
                ArrayList<String> n = new ArrayList<String>();
                n.add(i);
                result.put(img_folder, n);
            }
            else{
                ArrayList<String> n = result.get(img_folder);
                n.add(i);
                result.put(img_folder, n);
            }
        }
        return result;
    }
    private static ArrayList<String> load_paths(Activity activity) {
        /*
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, album_sort_order); //default: null
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                if (cursor.getString(0) != null) {
                    this.img_dir.add(cursor.getString(0));
                }
            }
        }
        assert cursor != null;
        cursor.close();
        */
        ArrayList<String> list = new ArrayList<>();
        //MixedItem[] list;

        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, null); //default: null
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
        cursor = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null,null);
        while(cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
        }
        cursor.close();
        return list;
    }
}
