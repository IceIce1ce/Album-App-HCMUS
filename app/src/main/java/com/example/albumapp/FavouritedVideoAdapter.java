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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


public class FavouritedVideoAdapter extends BaseAdapter {
    DataPrefs myPrefs;
    private Activity context;
    private ImageView picturesView;
    ArrayList<String> FavouriteList;

    FavouritedVideoAdapter(Activity applicationContext, ArrayList<String> favouriteList){
        context = applicationContext;
        FavouriteList = favouriteList;
    }

    public int getCount() {
        return FavouriteList.size();
    }

    public Object getItem(int position) {
        return FavouriteList.get(position);
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
                .load(FavouriteList.get(position))
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().centerCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(800, 800)
                .into(picturesView);

        return picturesView;
    }

}