package com.example.albumapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class PasswordActivity extends AppCompatActivity {
    String password, inputPassword;
    private Button btnConfirm;
    private TextInputLayout txtPassword;
    private DataPrefs myPrefs;
    private Integer pinMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPrefs = new DataPrefs(this);
        password = myPrefs.getPassword();
        pinMode = myPrefs.getPinMode();
        setContentView(R.layout.activity_password);
        txtPassword = findViewById(R.id.txt_Password);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPasswordValid()){
                    //use pin to login
                    if(pinMode == 0){
                        startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                        finish();
                    }
                    //setup pin
                    else if(pinMode == 1){
                        startActivity(new Intent(PasswordActivity.this, SetPasswordActivity.class));
                        finish();
                    }
                    //delete pin
                    else if(pinMode == 2){
                        AlertDialog builder = new AlertDialog.Builder(PasswordActivity.this).create();
                        builder.setTitle("Confirm delete password");
                        builder.setMessage("Do you want to delete your password?");
                        builder.setButton(Dialog.BUTTON_POSITIVE,"Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                myPrefs.setPassword("");
                                Toast.makeText(PasswordActivity.this, "Your password has been deleted successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PasswordActivity.this, SettingsActivity.class));
                                finish();
                            }
                        });
                        builder.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                startActivity(new Intent(PasswordActivity.this, SettingsActivity.class));
                                finish();
                            }
                        });
                        builder.show();
                    }
                }
            }
        });
    }

    boolean checkPasswordValid(){
        inputPassword = txtPassword.getEditText().getText().toString();
        if(!inputPassword.equals(password)){
            txtPassword.setError("Password isn't correct");
            return false;
        }
        else{
            txtPassword.setError(null);
            txtPassword.setErrorEnabled(false);
            return true;
        }
    }
}