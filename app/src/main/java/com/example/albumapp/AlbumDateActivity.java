package com.example.albumapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class AlbumDateActivity extends AppCompatActivity {
    private Fragment currentFragment;
    ArrayList<String> img_list = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle args = ((Intent) intent).getBundleExtra("BUNDLE");
        img_list= (ArrayList<String>) args.getSerializable("ARRAYLIST");
        System.out.println(img_list);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_album_activity);

        currentFragment = Date_PictureFragment.newInstance(img_list);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder, currentFragment)
                .commit();
    }

}
