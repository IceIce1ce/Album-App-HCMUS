package com.example.albumapp;
/*
    Student name: Phan Tan Dat
    Student ID: 18127078
    Created on 3/8/2021
*/

import android.graphics.BitmapFactory;

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;

public class ImageInfo {
    private Date lastModDate = null;
    private String img_folder = "unknown";
    private String img_filename = "unknown";
    private String img_path = "unknown";
    private int width = -1;
    private int height = -1;
    private long img_size = -1;

    ImageInfo(String path){
        if (path == "") return;
        img_path = path;
        File file = null;
        try{
            file = new File(img_path);
        }
        catch (NullPointerException e) {
            return;
        }
        //Parse dir
        String delims = "[/]";
        String[] tokens = path.trim().split(delims);
        this.img_folder = tokens[tokens.length - 2];
        this.img_filename = tokens[tokens.length - 1];

        // Get date
        this.lastModDate = new Date(file.lastModified());

        // Get size
        long length = file.length();
        length = length / 1024; //KB
        this.img_size = length;
        // Get resolution
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(img_path, options);
        this.width = options.outWidth;
        this.height = options.outHeight;
    }
    public String getDate(){
        return this.lastModDate.toString();
    }
    public String getSize(){
        if (this.img_size > 1000) {
            Double tmp = Double.valueOf(this.img_size) / 1024;
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1); // Decimal limit
            return nf.format(tmp) + "MB";
        }
        else
            return Long.toString(this.img_size) + "kB";
    }
    public String getDirectory(){
        return this.img_path;
    }
    public String getFolder(){
        return this.img_folder;
    }
    public String getFilename(){
        return this.img_filename;
    }
    public String getResolution(){
        return this.width + "×" + this.height;
    }
}

