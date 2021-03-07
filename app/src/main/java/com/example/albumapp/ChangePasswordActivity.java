package com.example.albumapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    private DataPrefs myPrefs;
    private Button cancel_change_password_btn, confirm_change_password_btn;
    private TextInputLayout txtOldPassword, txtNewPassword;
    String oldPassword, newPassword;
    final Pattern patternNewPassword = Pattern.compile(".*[a-zA-Z]+.*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        myPrefs = new DataPrefs(this);
        cancel_change_password_btn = findViewById(R.id.cancel_change_password_btn);
        confirm_change_password_btn = findViewById(R.id.confirm_change_password_btn);
        txtOldPassword = findViewById(R.id.txt_old_password);
        txtNewPassword = findViewById(R.id.txt_new_password);
        confirm_change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmChangePassword()){
                    startActivity(new Intent(ChangePasswordActivity.this, SettingsActivity.class));
                    finish();
                }
            }
        });
        cancel_change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this, SettingsActivity.class));
                finish();
            }
        });
    }

    private boolean isContainOneCharacterNewPassword(String str){
        Matcher matcherNewPassword = patternNewPassword.matcher(str);
        return matcherNewPassword.matches();
    }

    boolean confirmChangePassword(){
        oldPassword = txtOldPassword.getEditText().getText().toString().trim();
        newPassword = txtNewPassword.getEditText().getText().toString().trim();
        if(oldPassword.isEmpty()){
            txtOldPassword.setError("Old password cannot be blank");
            return false;
        }
        else if(!oldPassword.equals(myPrefs.getPassword())){
            txtOldPassword.setError("Your old input password doesn't match your old password");
            return false;
        }
        else if(newPassword.isEmpty()){
            txtNewPassword.setError("New password cannot be blank");
            return false;
        }
        else if(newPassword.length() < 4){
            txtNewPassword.setError("New password must be at least 4 characters");
            return false;
        }
        else if(!isContainOneCharacterNewPassword(newPassword)){
            txtNewPassword.setError("Password need contain at least one alphabet");
            return false;
        }
        else if(newPassword.equals(oldPassword)){
            txtNewPassword.setError("Your new password must be different from your old password");
            return false;
        }
        else{
            myPrefs.setPassword(newPassword);
            Toast.makeText(this, "Change password successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}