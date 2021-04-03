package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.divyanshu.colorseekbar.ColorSeekBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileNotFoundException;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterActivity extends AppCompatActivity {
    private PhotoEditorView mPhotoEditorView;
    private BottomNavigationView navBottomFilterImage;
    private PhotoEditor mPhotoEditor;
    SeekBar seekBarBrushSize;
    ColorSeekBar colorSeekBarPicker;
    HorizontalScrollView horizontalScrollViewFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        //Objects.requireNonNull(getSupportActionBar()).setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        String pathFilter = getIntent().getStringExtra("pathFilter");
        Uri uriFilter = Uri.parse(pathFilter);
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPhotoEditorView.getSource().setImageURI(uriFilter);
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView).setPinchTextScalable(true)
                .setDefaultEmojiTypeface(mEmojiTypeFace).build();
        seekBarBrushSize = findViewById(R.id.seekbar_brush_size);
        colorSeekBarPicker = findViewById(R.id.color_picker_seekbar);
        horizontalScrollViewFilter = findViewById(R.id.horizontal_filter);
        navBottomFilterImage = findViewById(R.id.nav_bottom_filter_image);
        navBottomFilterImage.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_brush:
                        horizontalScrollViewFilter.setVisibility(View.INVISIBLE);
                        mPhotoEditor.setBrushDrawingMode(true);
                        seekBarBrushSize.setVisibility(View.VISIBLE);
                        colorSeekBarPicker.setVisibility(View.VISIBLE);
                        seekBarBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                mPhotoEditor.setBrushSize(progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        colorSeekBarPicker.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
                            @Override
                            public void onColorChangeListener(int color) {
                                mPhotoEditor.setBrushColor(color);
                            }
                        });
                        return true;
                    case R.id.nav_text_fields:
                        seekBarBrushSize.setVisibility(View.INVISIBLE);
                        colorSeekBarPicker.setVisibility(View.INVISIBLE);
                        horizontalScrollViewFilter.setVisibility(View.INVISIBLE);
                        Toast.makeText(FilterActivity.this, "Add Text field", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_eraser:
                        seekBarBrushSize.setVisibility(View.INVISIBLE);
                        colorSeekBarPicker.setVisibility(View.INVISIBLE);
                        horizontalScrollViewFilter.setVisibility(View.INVISIBLE);
                        mPhotoEditor.brushEraser();
                        return true;
                    case R.id.nav_emoji:
                        seekBarBrushSize.setVisibility(View.INVISIBLE);
                        colorSeekBarPicker.setVisibility(View.INVISIBLE);
                        horizontalScrollViewFilter.setVisibility(View.INVISIBLE);
                        Toast.makeText(FilterActivity.this, "Add Emoji", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_filter:
                        seekBarBrushSize.setVisibility(View.INVISIBLE);
                        colorSeekBarPicker.setVisibility(View.INVISIBLE);
                        horizontalScrollViewFilter.setVisibility(View.VISIBLE);
                        findViewById(R.id.filter_brightness).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS);
                            }
                        });
                        findViewById(R.id.filter_blackwhite).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.setFilterEffect(PhotoFilter.BLACK_WHITE);
                            }
                        });
                        findViewById(R.id.filter_contrast).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.setFilterEffect(PhotoFilter.CONTRAST);
                            }
                        });
                        findViewById(R.id.filter_filllight).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.setFilterEffect(PhotoFilter.FILL_LIGHT);
                            }
                        });
                        findViewById(R.id.filter_fisheye).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.setFilterEffect(PhotoFilter.FISH_EYE);
                            }
                        });
                        return true;
                    default:
                        return false;
                }
            }
        });
        findViewById(R.id.cancel_edit_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.save_edit_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getBaseContext().getFilesDir()
                        + File.separator + ""
                        + System.currentTimeMillis() + ".png"); //not use .jpg if android >= 10.0
                try{
                    try{
                        file.createNewFile();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(FilterActivity.this, "Create new file fail!!!", Toast.LENGTH_SHORT).show();
                    }
                    mPhotoEditor.saveAsFile(file.getAbsolutePath(), new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String imagePath) {
                            try {
                                MediaStore.Images.Media.insertImage(getContentResolver(), imagePath, "", "");
                                startActivity(new Intent(FilterActivity.this, MainActivity.class));
                                Toast.makeText(FilterActivity.this, "Edit image successfully!!!", Toast.LENGTH_SHORT).show();
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(FilterActivity.this, "Insert image fail!!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                            Toast.makeText(FilterActivity.this, "Edit image fail!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch(SecurityException e){
                    e.printStackTrace();
                    Toast.makeText(FilterActivity.this, "Permission fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.undo_edit_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarBrushSize.setVisibility(View.INVISIBLE);
                colorSeekBarPicker.setVisibility(View.INVISIBLE);
                mPhotoEditor.undo();
            }
        });
        findViewById(R.id.redo_edit_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarBrushSize.setVisibility(View.INVISIBLE);
                colorSeekBarPicker.setVisibility(View.INVISIBLE);
                mPhotoEditor.redo();
            }
        });
    }
}