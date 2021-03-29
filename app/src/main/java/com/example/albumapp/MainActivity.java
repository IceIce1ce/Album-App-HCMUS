package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolBar;
    FragmentTransaction ft;
    //display image fragment
    PicturesActivity pictures;
    //FAB for camera and record
    FloatingActionButton fabRecord, fabCamera, fabOpenClose;
    boolean flagFAB = true;
    //capture image and record video
    private static final int MY_CAMERA_REQUEST_CODE = 7070, MY_VIDEO_REQUEST_CODE = 7078;
    private Uri imageUriCapture, videoUriCapture;
    //refresh current gridview image
    private SwipeRefreshLayout swipeImg;
    //display video fragment
    videoActivity videos;
    int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        //set default state when app start
        if(savedInstanceState == null){
            ft = getSupportFragmentManager().beginTransaction();
            pictures = PicturesActivity.newInstance();
            ft.replace(R.id.content_frame, pictures);
            ft.commit();
            toolBar.setTitle("Image");
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
                            if(currentFragment == 1){
                                ft = getSupportFragmentManager().beginTransaction();
                                pictures = PicturesActivity.newInstance();
                                ft.replace(R.id.content_frame, pictures);
                                ft.commit();
                            }
                            else if(currentFragment == 2){
                                ft = getSupportFragmentManager().beginTransaction();
                                videos = videoActivity.newInstance();
                                ft.replace(R.id.content_frame, videos);
                                ft.commit();
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
            Toast.makeText(this, "Changed image order", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.sub_asc_order) {
            Toast.makeText(this, "ASCENDING", Toast.LENGTH_SHORT).show();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new Date_PictureActivity());
            ft.commit();
            return true;
        }
        else if (id == R.id.sub_desc_order) {
            Toast.makeText(this, "DESCENDING", Toast.LENGTH_SHORT).show();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new Date_PictureActivity());
            ft.commit();
            return true;
        }
        else if (id == R.id.action_layout_type) {
            Toast.makeText(this, "Change view type", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.sub_grid_big){
            Toast.makeText(this, "GRID", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.sub_list_big){
            Toast.makeText(this, "LIST", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.action_more_vert){
            Toast.makeText(this, "More option", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_pictures) {
            toolBar.setTitle("Image");
            ft = getSupportFragmentManager().beginTransaction();
            pictures = PicturesActivity.newInstance();
            ft.replace(R.id.content_frame, pictures);
            ft.commit();
            currentFragment = 1;
        }
        else if(id == R.id.nav_videos){
            toolBar.setTitle("Video");
            ft = getSupportFragmentManager().beginTransaction();
            videos = videoActivity.newInstance();
            ft.replace(R.id.content_frame, videos);
            ft.commit();
            currentFragment = 2;
        }
        else if(id == R.id.nav_album){
            toolBar.setTitle("Album");
        }
        else if(id == R.id.nav_favorite){
            toolBar.setTitle("Favourite");
        }
        else if(id == R.id.nav_category_image){
            toolBar.setTitle("Category image");
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