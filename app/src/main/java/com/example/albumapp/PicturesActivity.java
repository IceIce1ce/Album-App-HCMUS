package com.example.albumapp;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class PicturesActivity extends Fragment {
    View pictures;
    public static ArrayList<String> images;
    private DataPrefs myPrefs;
    private float x1, x2;
    private GridView gallery;

    public static PicturesActivity newInstance() {
        return new PicturesActivity();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pictures = inflater.inflate(R.layout.activity_pictures, container, false);
        myPrefs = new DataPrefs(getContext());
        gallery = pictures.findViewById(R.id.galleryGridView);
        Integer[] columns = myPrefs.getNumberOfColumns();
        //get width and height of screen phone
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        //display 6 pictures if phone is landscape
        if (screenWidth > screenHeight) {
            gallery.setNumColumns(columns[1]);
        }
        //display 3 pictures if phone is portrait
        else {
            gallery.setNumColumns(columns[0]);
        }
        gallery.setAdapter(new ImageAdapter(this.getActivity()));
        //todo: open image in full-screen when click
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Image " + position + " is clicked", Toast.LENGTH_SHORT).show();
            }
        });
        //show info of 1 image when use a long click
        gallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String img_path = images.get(position);
                ImageInfo s = new ImageInfo(img_path);
                Toast.makeText(getActivity(), "Info of image: " + img_path
                        + "\nFolder: " + s.getFolder()
                        + "\nFilename: " + s.getFilename()
                        + "\nDate: " + s.getDate()
                        + "\nSize: " + s.getSize()
                        + "\nResolution: " + s.getResolution()
                        + "\n" + s.getExif(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        //todo: use two fingers zoom in and zoom out to change layout of image
        //change layout of image when touch left right and right to left
        int MIN_DISTANCE = 150;
        gallery.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if(Math.abs(deltaX) > MIN_DISTANCE){
                            //swipe left to right
                            if(x2 > x1){
                                gallery.setNumColumns(gallery.getNumColumns() - 1);
                                if(screenWidth > screenHeight){
                                    columns[1]--;
                                }
                                else{
                                    columns[0]--;
                                }
                                if(columns[1] == 1 || columns[0] == 1){
                                    //todo: disable swipe left to right to avoid app crash
                                    Toast.makeText(getContext(), "You reached maximum swipe", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //swipe right to left
                            else{
                                gallery.setNumColumns(gallery.getNumColumns() + 1);
                                if(screenWidth > screenHeight){
                                    columns[1]++;
                                }
                                else{
                                    columns[0]++;
                                }
                            }
                            gallery.setAdapter(null);
                            gallery.setAdapter(new ImageAdapter(PicturesActivity.this.getActivity()));
                            myPrefs.SetNumberOfColumns(columns);
                            break;
                        }
                }
                return false;
            }
        });
        return pictures;
    }
}