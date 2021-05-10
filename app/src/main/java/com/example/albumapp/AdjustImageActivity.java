package com.example.albumapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdjustImageActivity extends AppCompatActivity {
    String pathAdjust;
    SeekBar sb_brightness, sb_contrast;
    ImageView img_to_adjust;
    TextView tv_brightness, tv_contrast;
    ColorFilter cur_filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);
        pathAdjust = getIntent().getStringExtra("pathAdjust");
        img_to_adjust = findViewById(R.id.adjust_imgview);
        Glide.with(getApplicationContext()).asBitmap().load(pathAdjust)
                .apply(new RequestOptions().placeholder(null).fitCenter())
                .into(img_to_adjust);
        tv_brightness = findViewById(R.id.tv_brightness);
        tv_contrast = findViewById(R.id.tv_constrast);
        sb_brightness = findViewById(R.id.seekbar_brightness);
        sb_contrast = findViewById(R.id.seekbar_contrast);
        cur_filter = adjustImage(img_to_adjust);
        sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cur_filter = adjustImage(img_to_adjust);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        sb_contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cur_filter = adjustImage(img_to_adjust);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        findViewById(R.id.cancel_adjust_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.save_adjust_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Apply filter to bitmap
                BitmapDrawable draw = (BitmapDrawable) img_to_adjust.getDrawable();
                Bitmap bitmap = draw.getBitmap();
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColorFilter(cur_filter);
                canvas.drawBitmap(bitmap,0,0, paint);

                //-------------------------------
                FileOutputStream outStream = null;
                String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "AdjustedImages";
                File dir = new File(folder);
                dir.mkdirs();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = "IMG_" + timeStamp + "_adjusted.jpg";
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outFile)));
                    } else {
                        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(outFile)));
                    }
                    Toast.makeText(AdjustImageActivity.this, "Save adjusted image successfully as\n" + folder + File.separator + fileName , Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AdjustImageActivity.this, "Save adjusted image failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private ColorFilter adjustImage(ImageView iv){
        float brightness = (float)(sb_brightness.getProgress());
        float contrast = (float)(sb_contrast.getProgress());
        tv_contrast.setText("Contrast: " + String.valueOf((sb_contrast.getProgress())));
        tv_brightness.setText("Brightness: " + String.valueOf(sb_brightness.getProgress()));
        float[] colorMatrix = {
                contrast, 0, 0, 0, brightness, //red
                0, contrast, 0, 0, brightness, //green
                0, 0, contrast, 0, brightness, //blue
                0, 0, 0, 1, 0    //alpha
        };
        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        iv.setColorFilter(colorFilter);
        return colorFilter;
    }
}
