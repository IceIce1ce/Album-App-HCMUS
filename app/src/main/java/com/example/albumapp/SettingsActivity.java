package com.example.albumapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private DataPrefs myPrefs;
    Button btnSetPin, btnDeletePin, btnChangePin;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);
        myPrefs = new DataPrefs(this);
        password = myPrefs.getPassword();
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnSetPin = findViewById(R.id.btn_setPin);
        btnDeletePin = findViewById(R.id.btn_deletePin);
        btnSetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPrefs.setPinMode(1);
                if(password.equals("")) {
                    startActivity(new Intent(SettingsActivity.this, SetPasswordActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(SettingsActivity.this, "You have set your pin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDeletePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPrefs.setPinMode(2);
                if(password.equals("")) {
                    Toast.makeText(SettingsActivity.this, "Pin hasn't been set", Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivity(new Intent(SettingsActivity.this, PasswordActivity.class));
                    finish();
                }
            }
        });
        btnChangePin = findViewById(R.id.btn_changePin);
        btnChangePin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(password.equals("")){
                    Toast.makeText(SettingsActivity.this, "Pin hasn't been set", Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}