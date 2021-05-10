package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolBar;
    FragmentTransaction ft;
    //display image fragment
    static PicturesActivity pictures;
    //FAB for camera and record
    FloatingActionButton fabRecord, fabCamera, fabOpenClose;
    boolean flagFAB = true;
    //capture image and record video
    private static final int MY_CAMERA_REQUEST_CODE = 7070, MY_VIDEO_REQUEST_CODE = 7078;
    private Uri imageUriCapture, videoUriCapture;
    //refresh current gridview image
    public static SwipeRefreshLayout swipeImg; //default disable in nested recyclerview
    //display video fragment
    videoActivity videos;
    static String sort_order_date_header = "DATE_MODIFIED DESC";
    DateMixedItemFragment date_mix_frag;
    MixedItemFragment mix_frag;
    int currentFragment;
    Switch dark_mode_switch;
    boolean dark_mode = false;
    public static boolean view_mode_has_date = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getApplication().setTheme(R.style.Theme_AlbumApp);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sort_order_date_header = sharedPref.getString("ITEM_SORT_ORDER", "DATE_MODIFIED DESC");
        view_mode_has_date = sharedPref.getBoolean("ITEM_HAS_DATE", false);
        dark_mode = sharedPref.getBoolean("APP_DARK_MODE", false);
        if(dark_mode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolBar = findViewById(R.id.nav_actionBar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_dark_mode_switch).setActionView(new Switch(this));
        dark_mode_switch = (Switch) navigationView.getMenu().findItem(R.id.nav_dark_mode_switch).getActionView();
        dark_mode_switch.setChecked(dark_mode);
        dark_mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(dark_mode_switch.isChecked())
                    dark_mode = true;
                else
                    dark_mode = false;

                if(dark_mode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    recreate();
                    Toast.makeText(getBaseContext(), "Dark mode: ON", Toast.LENGTH_SHORT).show();
                }
               else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    recreate();
                    Toast.makeText(getBaseContext(), "Dark mode: OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //set default state when app start
        if(savedInstanceState == null){
            /*
            pictures = PicturesActivity.newInstance();
            refresh_all();
            toolBar.setTitle(R.string.all);*/
            ft = getSupportFragmentManager().beginTransaction();
            pictures = PicturesActivity.newInstance();
            ft.replace(R.id.content_frame, pictures);
            ft.commit();
            refresh_all();
            toolBar.setTitle(R.string.all);
        }
        //FAB for camera and record
        fabRecord = findViewById(R.id.fabRecord);
        fabCamera = findViewById(R.id.fabCamera);
        fabOpenClose = findViewById(R.id.fabOpenClose);
        fabOpenClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFAB) {
                    fabRecord.show();
                    fabCamera.show();
                    fabRecord.animate().translationY(-(fabCamera.getCustomSize() + fabOpenClose.getCustomSize() + 20));
                    fabCamera.animate().translationY(-(fabOpenClose.getCustomSize()));
                    fabRecord.animate().translationX(-12);
                    fabCamera.animate().translationX(-12);
                    fabOpenClose.setImageResource(R.drawable.ic_baseline_close_24);
                    flagFAB = false;
                }
                else {
                    fabRecord.hide();
                    fabCamera.hide();
                    fabRecord.animate().translationY(0);
                    fabCamera.animate().translationY(0);
                    fabOpenClose.setImageResource(R.drawable.ic_baseline_add_24);
                    flagFAB = true;
                }
            }
        });
        fabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecordVideo(v);
            }
        });
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCaptureImage(v);
            }
        });
        //refresh current gridview image
        swipeImg = findViewById(R.id.refresh_list_img);
        swipeImg.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);
        swipeImg.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(300);
                            if(currentFragment == 0){
                                refresh_all();
                                toolBar.setTitle(R.string.all);
                            }
                            if(currentFragment == 1){
                                ft = getSupportFragmentManager().beginTransaction();
                                pictures = PicturesActivity.newInstance();
                                ft.replace(R.id.content_frame, pictures);
                                ft.commit();
                                toolBar.setTitle(R.string.Pictures);
                            }
                            else if(currentFragment == 2){
                                ft = getSupportFragmentManager().beginTransaction();
                                videos = videoActivity.newInstance();
                                ft.replace(R.id.content_frame, videos);
                                ft.commit();
                                toolBar.setTitle(R.string.Videos);
                            }
                        }
                        catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeImg.setRefreshing(false); //avoid swipe infinity
                            }
                        });
                    }
                }).start();
            }
        });
        //save current language to sharepreference
        SharedPreferences settingsMultiLanguage = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        String lang = settingsMultiLanguage.getString("LANG", "");
        if (! "".equals(lang) && ! configuration.locale.getLanguage().equals(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            configuration.locale = locale;
            getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        }
        //get favourite images from sharedpreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String key_response = sharedPreferences.getString("savedFavoriteImages","");
        FavouriteActivity.favoriteImages = gson.fromJson(key_response, new TypeToken<ArrayList<String>>(){}.getType());
        //get favourite videos from sharedpreferences
        SharedPreferences sharedPreferencesVideo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gsonVideo = new Gson();
        String key_response_video = sharedPreferencesVideo.getString("savedFavoriteVideos","");
        FavouriteVideoActivity.favoriteVideos = gsonVideo.fromJson(key_response_video, new TypeToken<ArrayList<String>>(){}.getType());
    }

    @Override
    protected void onDestroy() {
        SharedPreferences p = getPreferences(Context.MODE_PRIVATE);
        p.edit().putBoolean("APP_DARK_MODE", dark_mode).commit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        SharedPreferences p = getPreferences(Context.MODE_PRIVATE);
        p.edit().putBoolean("APP_DARK_MODE", dark_mode).commit();
        super.onPause();
    }

    //capture image and record video
    void startRecordVideo(View view){
        Dexter.withContext(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if(multiplePermissionsReport.areAllPermissionsGranted()){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Video.Media.TITLE, "New Video");
                    contentValues.put(MediaStore.Video.Media.DESCRIPTION, "From your camera");
                    videoUriCapture = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                    Intent intent_video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent_video.putExtra(MediaStore.EXTRA_OUTPUT, videoUriCapture);
                    startActivityForResult(intent_video, MY_VIDEO_REQUEST_CODE);
                }
                else{
                    showSettingsPermission(); //show setting camera permission if fail
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest(); //keep settings dialog shown after permission denied
            }
        }).check();
    }

    void startCaptureImage(View view){
        Dexter.withContext(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if(multiplePermissionsReport.areAllPermissionsGranted()){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
                    contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From your camera");
                    imageUriCapture = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUriCapture);
                    startActivityForResult(intent_camera, MY_CAMERA_REQUEST_CODE);
                }
                else{
                    showSettingsPermission(); //show setting camera permission if fail
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest(); //keep settings dialog shown after permission denied
            }
        }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == MY_CAMERA_REQUEST_CODE){
                //update current grid view image after capturing image
                ft = getSupportFragmentManager().beginTransaction();
                pictures = PicturesActivity.newInstance();
                ft.replace(R.id.content_frame, pictures);
                ft.commit();
                Toast.makeText(this, "Capture image successfully", Toast.LENGTH_SHORT).show();
            }
            else if(requestCode == MY_VIDEO_REQUEST_CODE){
                ft = getSupportFragmentManager().beginTransaction();
                videos = videoActivity.newInstance();
                ft.replace(R.id.content_frame, videos);
                ft.commit();
                Toast.makeText(this, "Record video successfully", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Capture image or record video failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSettingsPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Camera Permission");
        builder.setMessage("This app needs camera permission to use this feature. You can grant them in app settings");
        builder.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettingsPhone();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettingsPhone() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.status_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Toast.makeText(this, "Search image", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.action_sort){
            //Toast.makeText(this, "Changed image order", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.sub_asc_order) {
            this.sort_order_date_header = "DATE_MODIFIED ASC";
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            sharedPref.edit().putString("ITEM_SORT_ORDER", this.sort_order_date_header).apply();
            refresh_all();
            return true;
        }
        else if (id == R.id.sub_desc_order) {
            this.sort_order_date_header = "DATE_MODIFIED DESC";
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            sharedPref.edit().putString("ITEM_SORT_ORDER", this.sort_order_date_header).apply();
            refresh_all();
            return true;
        }
        else if (id == R.id.action_layout_type) {
            Toast.makeText(this, "Change view type", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.sub_has_date){
            this.view_mode_has_date = true;
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            sharedPref.edit().putBoolean("ITEM_HAS_DATE", this.view_mode_has_date).apply();
            refresh_all();
            return true;
        }
        else if (id == R.id.sub_no_date){
            this.view_mode_has_date = false;
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            sharedPref.edit().putBoolean("ITEM_HAS_DATE", this.view_mode_has_date).apply();
            refresh_all();
            return true;
        }
        else if(id == R.id.action_more_vert){
            Toast.makeText(this, "More option", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.action_multi_language){
            AlertDialog.Builder builderMultiLanguage = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.language_dialog, null);
            builderMultiLanguage.setView(dialogView);
            final Spinner spinnerMultiLanguage = dialogView.findViewById(R.id.spinner_multi_language);
            builderMultiLanguage.setTitle(getResources().getString(R.string.lang_dialog_title));
            builderMultiLanguage.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    switch(spinnerMultiLanguage.getSelectedItemPosition()) {
                        case 0:
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").apply();
                            recreateLanguage("en");
                            break;
                        case 1:
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "vi").apply();
                            recreateLanguage("vi");
                            break;
                        case 2:
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "ko").apply();
                            recreateLanguage("ko");
                            break;
                        case 3:
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "zh").apply();
                            recreateLanguage("zh");
                            break;
                    }
                }
            });
            builderMultiLanguage.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builderMultiLanguage.create();
            alert.show();
            return true;
        }
        return false;
    }

    public void recreateLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        recreate();
        Toast.makeText(this, "Change language successfully", Toast.LENGTH_SHORT).show();
    }

    /*come back to previous activity without loss data
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    private void refresh_all(){
        ft = getSupportFragmentManager().beginTransaction();
        if(this.view_mode_has_date){
            date_mix_frag = DateMixedItemFragment.newInstance();
            ft.replace(R.id.content_frame, date_mix_frag);
        }
        else{
            mix_frag = MixedItemFragment.newInstance();
            ft.replace(R.id.content_frame, mix_frag);
        }
        ft.commit();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_mixed_item){
            toolBar.setTitle(R.string.all);
            refresh_all();
            currentFragment = 0;
        }
        else if (id == R.id.nav_pictures) {
            toolBar.setTitle(R.string.Pictures);
            ft = getSupportFragmentManager().beginTransaction();
            pictures = PicturesActivity.newInstance();
            ft.replace(R.id.content_frame, pictures);
            ft.commit();
            currentFragment = 1;
        }
        else if(id == R.id.nav_videos){
            toolBar.setTitle(R.string.Videos);
            ft = getSupportFragmentManager().beginTransaction();
            videos = videoActivity.newInstance();
            ft.replace(R.id.content_frame, videos);
            ft.commit();
            currentFragment = 2;
        }
        else if(id == R.id.nav_album){
            toolBar.setTitle(R.string.Album);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AlbumActivity());
            //Toast.makeText(this, "Album", Toast.LENGTH_SHORT).show();
            ft.commit();
        }
        else if(id == R.id.nav_favorite_images){
            toolBar.setTitle(R.string.Favourite_Image);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new FavouriteActivity());
            ft.commit();
        }
        else if(id == R.id.nav_favorite_videos){
            toolBar.setTitle(R.string.Favourite_Video);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new FavouriteVideoActivity());
            ft.commit();
        }
        else if(id == R.id.nav_category_image){
            //toolBar.setTitle(R.string.Category_Image);
            startActivity(new Intent(MainActivity.this, CategoryImageActivity.class));
        }
        else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }
        else if (id == R.id.nav_support) {
            Toast.makeText(this, "Need support", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}