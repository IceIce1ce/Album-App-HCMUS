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

public class SetPasswordActivity extends AppCompatActivity {
    private DataPrefs myPrefs;
    private Button btnConfirm, btnCancel;
    private TextInputLayout txtPassword, txtRetypePassword;
    String password, retypePassword;
    final Pattern pattern = Pattern.compile(".*[a-zA-Z]+.*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        myPrefs = new DataPrefs(this);
        txtPassword = findViewById(R.id.txt_Password);
        txtRetypePassword = findViewById(R.id.txt_RetypePassword);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmPassword()){
                    startActivity(new Intent(SetPasswordActivity.this, SettingsActivity.class));
                    finish();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetPasswordActivity.this, SettingsActivity.class));
                finish();
            }
        });
    }

    boolean validatePassword(){
        password = txtPassword.getEditText().getText().toString().trim();
        retypePassword = txtRetypePassword.getEditText().getText().toString().trim();
        if(password.isEmpty()){
            txtPassword.setError("Password cannot be blank");
            return false;
        }
        else if(password.length() < 4){
            txtPassword.setError("Password must be at least 4 characters");
            return false;
        }
        else if(!isContainOneCharacter(password)){
            txtPassword.setError("Password need contain at least one alphabet");
            return false;
        }
        else if(retypePassword.isEmpty()) {
            txtRetypePassword.setError("Confirm password cannot be blank");
            return false;
        }
        else if(!password.equals(retypePassword)){
            txtRetypePassword.setError("Your password doesn't match your confirm password");
            return false;
        }
        else{
            txtPassword.setError(null);
            txtPassword.setErrorEnabled(false);
            txtRetypePassword.setError(null);
            txtRetypePassword.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isContainOneCharacter(String str){
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    boolean confirmPassword(){
        if(validatePassword()){
            myPrefs.setPassword(password);
            Toast.makeText(this, "Set password successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}