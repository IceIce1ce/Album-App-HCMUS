package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import java.io.File;

public class SlideshowActivity extends AppCompatActivity {
    private ViewFlipper viewFlipper;
    private Button btnSlideshowNext, btnSlideshowPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Slideshow");
        viewFlipper = findViewById(R.id.flipperSlideshow);
        int position = getIntent().getExtras().getInt("position_slideshow");
        //slideshow from current image to end image
        for(int i = position; i < PicturesActivity.images.size(); i++) {
            StartFlipImage(PicturesActivity.images.get(i));
        }
        viewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation){

            }
            public void onAnimationRepeat(Animation animation){

            }
            public void onAnimationEnd(Animation animation){
                //end viewFlipper when reach to the end image
                if(viewFlipper.getDisplayedChild() == viewFlipper.getChildCount() - 1){
                    viewFlipper.stopFlipping();
                    finish();
                }
            }
        });
        //
        btnSlideshowNext = findViewById(R.id.next_slideshow_btn);
        btnSlideshowPrevious = findViewById(R.id.previous_slideshow_btn);
        btnSlideshowNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewFlipper.isAutoStart()){
                    viewFlipper.stopFlipping();
                    viewFlipper.showNext();
                    viewFlipper.startFlipping();
                    viewFlipper.setAutoStart(true);
                }
            }
        });
        btnSlideshowPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewFlipper.isAutoStart()){
                    viewFlipper.stopFlipping();
                    viewFlipper.showPrevious();
                    viewFlipper.startFlipping();
                    viewFlipper.setAutoStart(true);
                }
            }
        });
    }

    public void StartFlipImage(String pathImg){
        ImageView ImageViewUri  = new ImageView(this);
        Uri pathUri = Uri.fromFile(new File(pathImg));
        Glide.with(getApplicationContext()).load(pathUri).into(ImageViewUri);
        viewFlipper.addView(ImageViewUri);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}