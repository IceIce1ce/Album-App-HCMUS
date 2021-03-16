package com.example.albumapp;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    DataPrefs myPrefs;
    private Activity context;
    private ImageView picturesView;

    ImageAdapter(Activity applicationContext){
        this.context = applicationContext;
        PicturesActivity.images = loadAlbum(context);
    }

    public int getCount() {
        return PicturesActivity.images.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            myPrefs = new DataPrefs(context);
            Integer[] columns = myPrefs.getNumberOfColumns();
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            int column = (screenWidth < screenHeight) ? columns[0] : columns[1];
            int sizeOfImageDisplay = (screenWidth - (column * 12)) / column;
            picturesView = new ImageView(context);
            picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            picturesView.setLayoutParams(new GridView.LayoutParams(sizeOfImageDisplay, sizeOfImageDisplay));
        }
        else {
            picturesView = (ImageView) convertView;
        }
        Glide.with(context)
                .load(PicturesActivity.images.get(position))
                .transition(new DrawableTransitionOptions().crossFade()).apply(new RequestOptions().placeholder(null).centerCrop())
                .into(picturesView);
        return picturesView;
    }

    private ArrayList<String> loadAlbum(Activity activity) {
        ArrayList<String> albumList = new ArrayList<>();
        //todo: get MediaStore.Videos
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, "DATE_MODIFIED DESC"); //default: null
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                albumList.add(cursor.getString(0));
            }
        }
        assert cursor != null;
        cursor.close();
        return albumList;
    }
}