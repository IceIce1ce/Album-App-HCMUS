package com.example.albumapp;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PicturesActivity extends Fragment {
    View pictures;
    public static ArrayList<String> images;
    private DataPrefs myPrefs;
    private float x1, x2;
    private GridView gallery;
    //store list of multi image
    ArrayList<Uri> imageUriArray = new ArrayList<Uri>();
    //0: show toolbar, 1: hide toolbar
    public static int statusToolbar = 0;

    public static PicturesActivity newInstance() {
        return new PicturesActivity();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.swipeImg.setEnabled(true);
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
                Intent image_intent = new Intent(getContext(), FullScreenImageActivity.class);
                image_intent.putExtra("id", position);
                image_intent.putExtra("path", images.get(position));
                image_intent.putExtra("allPath", images);
                String img_path = images.get(position);
                ImageInfo s = new ImageInfo(img_path);
                image_intent.putExtra("display_image_name", s.getFilename());
                startActivity(image_intent);
            }
        });
        //select multi images to share and delete
        gallery.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gallery.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int selectCount = gallery.getCheckedItemCount();
                imageUriArray.add(Uri.parse(images.get(position)));
                switch (selectCount) {
                    case 1:
                        mode.setSubtitle("1 item selected");
                        break;
                    default:
                        mode.setSubtitle("" + selectCount + " items selected");
                        break;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater1 = mode.getMenuInflater();
                inflater1.inflate(R.menu.menu_multi_images, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.share_items:
                        //share multiple images
                        Intent share_intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        ArrayList<Uri> uris = new ArrayList<>();
                        for(int i = 0; i < imageUriArray.size(); i++){
                            share_intent.setType("image/*"); //application/pdf/*|image|video/*
                            File mFile = new File(imageUriArray.get(i).toString());
                            Uri shareFileUri = FileProvider.getUriForFile(getContext(), "com.mydomain.fileprovider", mFile);
                            uris.add(shareFileUri);
                        }
                        share_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                        startActivity(Intent.createChooser(share_intent, "Select app"));
                        mode.finish();
                        return true;
                    case R.id.delete_items:
                        AlertDialog dialogDelete = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).create();
                        dialogDelete.setTitle("Delete image");
                        if(imageUriArray.size() == 1){
                            dialogDelete.setMessage("Do you want to delete this image?");
                        }
                        else{
                            dialogDelete.setMessage("Do you want to delete " + imageUriArray.size() + " images?");
                        }
                        dialogDelete.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //delete multi images from storage
                                for(int i = 0; i < imageUriArray.size(); i++) {
                                    File photoFile = new File(imageUriArray.get(i).toString());
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
                                for(int i = 0; i < imageUriArray.size(); i++){
                                    images.remove(imageUriArray.get(i).toString());
                                }
                                Toast.makeText(getContext(), "Delete images successfully", Toast.LENGTH_SHORT).show();
                                mode.finish();
                            }
                        });
                        dialogDelete.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialogDelete.show();
                        return true;
                    default: return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                imageUriArray.clear();
            }
        });
        /*show info of 1 image when perform a long click
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
        });*/
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