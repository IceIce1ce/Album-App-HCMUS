package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolBar;
    FragmentTransaction ft;
    PicturesActivity pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolBar = findViewById(R.id.nav_actionBar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //todo: add floating action btn for accessing camera
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
            Toast.makeText(this, "Sort image", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.action_more_vert){
            Toast.makeText(this, "More option", Toast.LENGTH_SHORT).show();
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
        else if (id == R.id.nav_feedback) {
            Toast.makeText(this, "Give your feedback", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_help) {
            Toast.makeText(this, "Need support", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}