package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

public class GifIntroActivity extends AppCompatActivity {
    private DataPrefs myPrefs;
    String pin_unlock = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPrefs = new DataPrefs(this);
        pin_unlock = myPrefs.getPassword();
        setContentView(R.layout.activity_gif_intro);
        //hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //if get permission fail
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //if get permission successfully
        else{
            new SetupPin().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Get permission successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GifIntroActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private class SetupPin extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //if pin wasn't set, intent to main activity
            if(TextUtils.isEmpty(pin_unlock)){
                startActivity(new Intent(GifIntroActivity.this, MainActivity.class));
                finish();
            }
            else{
                myPrefs.setPinMode(0);
                startActivity(new Intent(GifIntroActivity.this, PasswordActivity.class));
                finish();
            }
        }
    }
}