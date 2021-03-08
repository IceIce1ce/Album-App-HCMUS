package com.example.albumapp;
/*
    Student name: Phan Tan Dat
    Student ID: 18127078
    Created on 3/8/2021
*/

import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;

public class ImageInfo {
    private Date lastModDate = null;
    private String img_folder = "unknown",
                img_filename = "unknown",
                img_path = "unknown",
                exif_ISO = "unknown",
                exif_WB = "unknown",
                exif_shutter_speed = "unknown",
                exif_camera_model = "unknown",
                exif_apeture = "unknown",
                exif_focal_length = "unknown",
                exif_full = "unknown";
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
        //Get EXIF interface
        try {
            ExifInterface exif = new ExifInterface(img_path);
            this.exif_ISO = exif.getAttribute(ExifInterface.TAG_ISO);
            this.exif_WB = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            this.exif_shutter_speed = exif.getAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE);
            this.exif_camera_model = exif.getAttribute(ExifInterface.TAG_MODEL);
            this.exif_apeture = exif.getAttribute(ExifInterface.TAG_APERTURE);
            this.exif_focal_length = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        } catch (IOException e) {

        }
        this.exif_full = "Camera: " + this.exif_camera_model
                +"\nApeture: " + this.exif_apeture
                + "\nFocal Length: " + this.exif_focal_length + "mm"
                + "\nWhite Balance: " + this.exif_WB
                + "\nISO: " + this.exif_ISO
                + "\nShutter speed: " + this.exif_shutter_speed + "s";
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
    public String getExif() { return this.exif_full; }
}

