package com.example.albumapp;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
                //get details of img for displaying in dialog
                String img_path = images.get(position);
                ImageInfo s = new ImageInfo(img_path);
                //convert lat and long of img location to address
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
                String DetailsImg = "Name\n" + s.getFilename() + "\n\nPath\n" + img_path + "\n\nSize\n" + s.getSize() +
                        "\n\nResolution\n" + s.getResolution() + "\n\nDate\n" + s.getDate() + "\n\nEXIF\n" + s.getExif()
                        + "\n\nLocation\n" + realAddressImg;
                TextView title = new TextView(getContext());
                title.setPadding(60, 30, 0, 0);
                title.setText("Details");
                title.setTextSize(18.0f);
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.BLACK);
                AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).create();
                dialog.setCustomTitle(title);
                dialog.setMessage(DetailsImg);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
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