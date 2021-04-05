package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class FavouriteActivity extends Fragment {
    View favorite;
    public static ArrayList<String> favoriteImages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        favorite = inflater.inflate(R.layout.activity_favourite, container, false);
        if(favoriteImages != null && !favoriteImages.isEmpty()){
            GridView favoriteGallery = favorite.findViewById(R.id.favoriteGalleryGridView);
            favoriteGallery.setAdapter(new ImageAdapter(FavouriteActivity.super.getActivity(), favoriteImages));
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            if(screenWidth > screenHeight) {
                favoriteGallery.setNumColumns(6);
            }
            else {
                favoriteGallery.setNumColumns(5);
            }
            favoriteGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent favourite_intent = new Intent(getContext(), FullScreenImageActivity.class);
                    favourite_intent.putExtra("id", position);
                    favourite_intent.putExtra("path", favoriteImages.get(position));
                    favourite_intent.putExtra("allPath", favoriteImages);
                    String img_path = favoriteImages.get(position);
                    ImageInfo s = new ImageInfo(img_path);
                    favourite_intent.putExtra("display_image_name", s.getFilename());
                    startActivity(favourite_intent);
                }
            });
        }
        return favorite;
    }
}