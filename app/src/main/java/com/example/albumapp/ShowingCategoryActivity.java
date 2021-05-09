package com.example.albumapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.albumapp.DateMixedItemFragment;
import com.example.albumapp.ItemsCategoryFragment;
import com.example.albumapp.MainActivity;
import com.example.albumapp.MixedItemFragment;
import com.example.albumapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowingCategoryActivity extends AppCompatActivity {
    private Fragment currentFragment;
    HashMap<String, String> img_list = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle args = ((Intent) intent).getBundleExtra("BUNDLE");
        img_list= (HashMap<String, String>) args.getSerializable("RESULT_HASHMAP");
        System.out.println(img_list);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_album_activity);
        currentFragment = ItemsCategoryFragment.newInstance(img_list);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder, currentFragment)
                .commit();
    }

}