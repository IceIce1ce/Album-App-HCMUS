package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FullScreenVideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private Toolbar toolbar;
    private String pathVid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        videoView = findViewById(R.id.video_play);
        pathVid = getIntent().getStringExtra("pathVideo");
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(pathVid);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
        //hide toolbar
        toolbar = findViewById(R.id.nav_actionBarVideo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_details_video:
                VideoInfo s = new VideoInfo(pathVid);
                //convert lat and long of video location to address
                Geocoder geocoder = new Geocoder(FullScreenVideoActivity.this, Locale.getDefault());
                List<Address> addresses;
                String realAddressImg = "unknown";
                try{
                    addresses = geocoder.getFromLocation(s.getLatLocation(), s.getLongLocation(), 1);
                    realAddressImg = addresses.get(0).getAddressLine(0);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                String DetailsVideo = "Name\n" + s.getFilename() + "\n\nPath\n" + pathVid + "\n\nSize\n" + s.getSize() + "\n\nDuration\n" + s.getDuration()
                        + "\n\nResolution\n" + s.getResolution()
                        + "\n\nDate\n" + s.getDate()
                        + "\n\nLocation\n" + realAddressImg;
                TextView title = new TextView(FullScreenVideoActivity.this);
                title.setPadding(60, 30, 0, 0);
                title.setText("Properties");
                title.setTextSize(18.0f);
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.BLACK);
                AlertDialog dialog = new AlertDialog.Builder(FullScreenVideoActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert).create();
                dialog.setCustomTitle(title);
                dialog.setMessage(DetailsVideo);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            case R.id.action_move_video:
                Toast.makeText(this, "Move video", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default: return false;
        }
    }
}