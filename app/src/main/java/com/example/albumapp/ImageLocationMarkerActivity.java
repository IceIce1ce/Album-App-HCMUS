package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ImageLocationMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_location_marker);
        getSupportActionBar().setTitle("Location Tag");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.img_map_marker);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ImageInfo s = null;
        LatLng location_tag = null;
        for(int i = 0; i < PicturesActivity.images.size(); i++){
            String img_path = PicturesActivity.images.get(i);
            s = new ImageInfo(img_path);
            if(s.getLatLocation() != 0 && s.getLongLocation() != 0){
                location_tag = new LatLng(s.getLatLocation(), s.getLongLocation());
                Bitmap bitmap = BitmapFactory.decodeFile(img_path);
                Bitmap bmpResize = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                googleMap.addMarker(new MarkerOptions().position(location_tag).icon(BitmapDescriptorFactory.fromBitmap(bmpResize)).anchor(0.5f, 1).title(s.getFilename()));
            }
        }
        if(location_tag != null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location_tag));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default: return false;
        }
    }
}