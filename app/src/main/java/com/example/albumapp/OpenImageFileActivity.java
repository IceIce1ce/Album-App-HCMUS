package com.example.albumapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.RecoverableSecurityException;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.albumapp.OpenVideoFileActivity.getPathFromUri;

public class OpenImageFileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView showImageFullscreen;
    private TextView txtNameImage;
    //private int position;
    private BottomNavigationView navBottom;
    private String storeNameImageCrop;
    //check current image is favourite or not
    public static boolean isFavouriteImage = false;
    public static Dialog dialog;
    String current_file_path;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_image);
        toolbar = findViewById(R.id.nav_actionBarImage);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //hide all
        setFullScreen();
        //get image path
        current_file_path = getPathFromUri(this ,getIntent().getData());
        navBottom = findViewById(R.id.nav_bottom);
        txtNameImage = findViewById(R.id.txtNameImage);
        if (PicturesActivity.statusToolbar == 0) {
            LeaveFullScreenImage();
        }
        else {
            EnterFullScreenImage();
        }
        showImageFullscreen = findViewById(R.id.imageView);
        navBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_edit:
                        //edit image
                        String pathImageFilter = current_file_path;
                        Intent filter_intent = new Intent(OpenImageFileActivity.this, FilterActivity.class);
                        filter_intent.putExtra("pathFilter", pathImageFilter);
                        startActivity(filter_intent);
                        return true;
                    case R.id.nav_crop:
                        //crop image
                        String filePath = current_file_path;
                        storeNameImageCrop = new File(filePath).getName();
                        CropImage.activity(Uri.fromFile(new File(filePath))).setGuidelines(CropImageView.Guidelines.ON)
                                .setMultiTouchEnabled(true).start(OpenImageFileActivity.this);
                        return true;
                    case R.id.nav_share:
                        //share single image
                        Intent share_intent = new Intent(Intent.ACTION_SEND);
                        share_intent.setType("image/*"); //application/pdf/*|image|video/*
                        Uri single_uri = Uri.parse(current_file_path);
                        File mFile = new File(single_uri.toString());
                        Uri shareFileUri = FileProvider.getUriForFile(getApplicationContext(), "com.mydomain.fileprovider", mFile);
                        share_intent.putExtra(Intent.EXTRA_STREAM, shareFileUri);
                        startActivity(Intent.createChooser(share_intent, "Select app"));
                        return true;
                    case R.id.nav_delete:
                        //delete single image
                        android.app.AlertDialog dialogDeleteSingle = new android.app.AlertDialog.Builder(OpenImageFileActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert).create();
                        dialogDeleteSingle.setTitle("Delete image");
                        dialogDeleteSingle.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //delete multi images from storage
                                Uri single_uri_del = Uri.parse(current_file_path);
                                File photoFile = new File(single_uri_del.toString());
                                String selection = MediaStore.Images.Media.DATA + " = ?";
                                String[] selectionArgs = new String[]{photoFile.getAbsolutePath()};
                                ContentResolver contentResolver = getContentResolver();
                                Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        new String[]{MediaStore.Images.Media._ID}, selection, selectionArgs, null);
                                if (cursor != null) {
                                    if (cursor.moveToFirst()) {
                                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                                            contentResolver.delete(deleteUri, null, null);
                                            Toast.makeText(OpenImageFileActivity.this, "Delete image successfully!!!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(OpenImageFileActivity.this, MainActivity.class));
                                        }
                                        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                            try{
                                                contentResolver.delete(deleteUri, null, null);
                                                Toast.makeText(OpenImageFileActivity.this, "Delete image successfully!!!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(OpenImageFileActivity.this, MainActivity.class));
                                            }
                                            catch(RecoverableSecurityException ex){
                                                final IntentSender intentSender = ex.getUserAction().getActionIntent().getIntentSender();
                                                try {
                                                    startIntentSenderForResult(intentSender, 1111, null, 0, 0, 0, null);
                                                }
                                                catch (IntentSender.SendIntentException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                    cursor.close();
                                }
                                //also delete single images from wishlist
                                if(isFavouriteImage){
                                    FavouriteActivity.favoriteImages.remove(single_uri_del.toString());
                                    SharedPreferences sharedPreferencesRemoveSingleWishList = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editorRemoveSingleWishList = sharedPreferencesRemoveSingleWishList.edit();
                                    Gson gsonRemoveSingleWishList = new Gson();
                                    String jsonRemoveSingleWishList = gsonRemoveSingleWishList.toJson(FavouriteActivity.favoriteImages);
                                    editorRemoveSingleWishList.putString("savedFavoriteImages", jsonRemoveSingleWishList);
                                    editorRemoveSingleWishList.apply();
                                }
                            }
                        });
                        dialogDeleteSingle.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialogDeleteSingle.show();
                        return true;
                }
                return false;
            }
        });
        //position = Objects.requireNonNull(getIntent().getExtras()).getInt("id");
        /*
        Glide.with(getApplicationContext()).load(current_file_path)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().placeholder(null).fitCenter())
                .into(showImageFullscreen);*/
        //load image as bitmap to set wallpaper
        Glide.with(getApplicationContext()).asBitmap().load(current_file_path)
                .apply(new RequestOptions().placeholder(null).fitCenter())
                .into(showImageFullscreen);
        txtNameImage.setText(getIntent().getStringExtra("display_image_name"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri resultUri = Objects.requireNonNull(result).getUri();
            showImageFullscreen.setImageURI(resultUri);
            showImageFullscreen.setDrawingCacheEnabled(true);
            Bitmap bmpCrop = showImageFullscreen.getDrawingCache();
            //insert new image crop to storage
            MediaStore.Images.Media.insertImage(getContentResolver(), bmpCrop, storeNameImageCrop + "_crop", "");
            //update picture fragment
            PicturesActivity.images.add(storeNameImageCrop + "_crop");
            startActivity(new Intent(OpenImageFileActivity.this, MainActivity.class));
            Toast.makeText(this, "Crop image successfully!!!", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
            Exception error = Objects.requireNonNull(result).getError();
            error.printStackTrace();
            Toast.makeText(this, "Crop image fail!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void EnterFullScreenImage() {
        Objects.requireNonNull(getSupportActionBar()).hide();
        navBottom.setVisibility(View.GONE);
        txtNameImage.setVisibility(View.GONE);
        setFullScreen();
    }

    public void LeaveFullScreenImage(){
        Objects.requireNonNull(getSupportActionBar()).show();
        navBottom.setVisibility(View.VISIBLE);
        txtNameImage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_main, menu);
        isFavouriteImage = false;
        String pathImageFavourite = current_file_path;
        if(FavouriteActivity.favoriteImages != null && !FavouriteActivity.favoriteImages.isEmpty()) {
            if(FavouriteActivity.favoriteImages.contains(pathImageFavourite)){
                MenuItem menuItem = menu.findItem(R.id.action_image_favorite);
                menuItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24_clicked));
                isFavouriteImage = true;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setFullScreen();
        switch(item.getItemId()){
            case R.id.action_image_favorite:
                if(isFavouriteImage){
                    MenuView.ItemView favorite_btn;
                    favorite_btn = findViewById(R.id.action_image_favorite);
                    favorite_btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24));
                    FavouriteActivity.favoriteImages.remove(current_file_path);
                    isFavouriteImage = false;
                }
                else{
                    MenuView.ItemView favorite_btn;
                    favorite_btn = findViewById(R.id.action_image_favorite);
                    favorite_btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_favorite_24_clicked));
                    if(FavouriteActivity.favoriteImages != null && !FavouriteActivity.favoriteImages.isEmpty()) {
                        FavouriteActivity.favoriteImages.add(current_file_path);
                    }
                    else
                    {
                        FavouriteActivity.favoriteImages = new ArrayList<>();
                        FavouriteActivity.favoriteImages.add(current_file_path);
                    }
                    isFavouriteImage = true;
                }
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(FavouriteActivity.favoriteImages);
                editor.putString("savedFavoriteImages", json);
                editor.apply();
                return true;
            case R.id.action_image_slideshow:
                /*Intent slideshow_intent = new Intent(OpenImageFileActivity.this, SlideshowActivity.class);
                slideshow_intent.putExtra("position_slideshow", position);
                startActivity(slideshow_intent);*/
                Toast.makeText(this, "Slide show function is not a vailable for single file mode", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_rotate_image_left:
                showImageFullscreen.setRotation(showImageFullscreen.getRotation() + 90);
                return true;
            case R.id.action_rotate_image_right:
                showImageFullscreen.setRotation(showImageFullscreen.getRotation() - 90);
                return true;
            case R.id.action_set_image_background:
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                int heightScale = Resources.getSystem().getDisplayMetrics().heightPixels;
                int widthScale = Resources.getSystem().getDisplayMetrics().widthPixels;
                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Bitmap bmpOriginal = ((BitmapDrawable)(showImageFullscreen.getDrawable())).getBitmap();
                        Bitmap bmpScale = Bitmap.createScaledBitmap(bmpOriginal, widthScale, heightScale, true);
                        wallpaperManager.setBitmap(bmpScale, null, false,
                                WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
                        Toast.makeText(this, "Set wallpaper successfully!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e){
                    Toast.makeText(this, "Set wallpaper fail!!!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_details_image:
                //get details of img for displaying in dialog
                String img_path_detail = current_file_path;
                ImageInfo s = new ImageInfo(img_path_detail);
                //convert lat and long of img location to address
                Geocoder geocoder = new Geocoder(OpenImageFileActivity.this, Locale.getDefault());
                List<Address> addresses;
                String realAddressImg = "unknown";
                try{
                    addresses = geocoder.getFromLocation(s.getLatLocation(), s.getLongLocation(), 1);
                    realAddressImg = addresses.get(0).getAddressLine(0);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                String DetailsImg = "Name\n" + s.getFilename() + "\n\nPath\n" + img_path_detail + "\n\nSize\n" + s.getSize() +
                        "\n\nResolution\n" + s.getResolution() + "\n\nDate\n" + s.getDate() + "\n\nEXIF\n" + s.getExif()
                        + "\n\nLocation\n" + realAddressImg;
                TextView title = new TextView(OpenImageFileActivity.this);
                title.setPadding(60, 30, 0, 0);
                title.setText("Properties");
                title.setTextSize(18.0f);
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.BLACK);
                AlertDialog dialog = new AlertDialog.Builder(OpenImageFileActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert).create();
                dialog.setCustomTitle(title);
                dialog.setMessage(DetailsImg);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            case R.id.action_copy_image:
                String filePath = current_file_path;
                showCopyFileDialog(OpenImageFileActivity.this, filePath);
                return true;
            //
            case android.R.id.home:
                finish();
                return true;
            default: return false;
        }
    }

    private void showCopyFileDialog(Activity activity, String target){
        dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_recycler);

        Button btndialog = (Button) dialog.findViewById(R.id.btndialog);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler);
        AlbumPickerAdapter albumPickerAdapter = new AlbumPickerAdapter(this,AlbumActivity.exportAlbumList(this), target);
        recyclerView.setAdapter(albumPickerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();

    }

    private void setFullScreen(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }
        else{
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }
}