package com.example.albumapp;

import android.content.Context;
import android.content.SharedPreferences;

public class DataPrefs {
    SharedPreferences myPrefs;

    public DataPrefs(Context context){
        myPrefs = context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public void setPassword(String password){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("Password", password);
        editor.apply();
    }

    public String getPassword(){
        String password = myPrefs.getString("Password","");
        return password;
    }

    public void setPinMode(Integer passMode){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("PassMode", passMode);
        editor.apply();
    }

    public Integer getPinMode(){
        //0: login, 1: change password, 2: delete password
        Integer passMode = myPrefs.getInt("PassMode",0);
        return passMode;
    }

    void SetNumberOfColumns(Integer[] columns) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("columnVertical", columns[0]);
        editor.putInt("columnHorizontal", columns[1]);
        editor.apply();
    }

    Integer[] getNumberOfColumns() {
        Integer[] columns = new Integer[2];
        columns[0] = myPrefs.getInt("columnVertical", 3);
        columns[1] = myPrefs.getInt("columnHorizontal", 6);
        return columns;
    }
}
