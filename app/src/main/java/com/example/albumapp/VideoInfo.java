package com.example.albumapp;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class VideoInfo {
    private String location;
    private long duration = -1;
    private Date lastModDate = null;
    private String vid_folder = "unknown", vid_filename = "unknown", vid_path = "unknown";
    private String width = "-1";
    private String height = "-1";
    private long vid_size = -1;
    private float longImg = new Float(0), latImg = new Float(0);

    VideoInfo(String path) {
        if (path.equals("")) return;
        this.vid_path = path;
        File file = null;
        try {
            file = new File(vid_path);
        } catch (NullPointerException e) {
            return;
        }
        //Parse dir
        String delims = "[/]";
        String[] tokens = path.trim().split(delims);
        this.vid_folder = tokens[tokens.length - 2];
        this.vid_filename = tokens[tokens.length - 1];
        // Get date
        this.lastModDate = new Date(file.lastModified());
        // Get size
        long length = file.length();
        length = length / 1024; //KB
        this.vid_size = length;
        MediaMetadataRetriever retriever;

        try {
            // Get resolution
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this.vid_path); //String.valueOf(file.toURI())
            String mVideoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            //System.out.println(mVideoDuration);
            this.duration = Long.parseLong(mVideoDuration);
            this.width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            this.height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            this.location = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
            int second = -1;
            second = this.location.substring(1).indexOf("+");
            if (second == -1)
                second = this.location.substring(1).indexOf("-");
            //System.out.println(this.location.substring(0, second));
            //System.out.println(this.location.substring(second + 1, this.location.length() - 1));
            this.latImg = Float.valueOf(this.location.substring(0, second));
            this.longImg = Float.valueOf(this.location.substring(second + 1, this.location.length() - 1));
            retriever.release();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        return this.lastModDate.toString();
    }

    public String getSize() {
        if (this.vid_size > 1000) {
            Double tmp = Double.valueOf(this.vid_size) / 1024;
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1); // Decimal limit
            return nf.format(tmp) + "MB";
        }
        else {
            return Long.toString(this.vid_size) + "KB";
        }
    }

    public String getDirectory() {
        return this.vid_path;
    }

    public String getFolder() {
        return this.vid_folder;
    }

    public String getFilename() {
        return this.vid_filename;
    }

    public String getResolution() {
        return this.width + "×" + this.height;
    }

    @SuppressLint("DefaultLocale")
    public String getDuration() {
        if(TimeUnit.MILLISECONDS.toMinutes(this.duration) == 0){
            return String.format("%d sec", TimeUnit.MILLISECONDS.toSeconds(this.duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this.duration)));
        }
        else return String.format("%d min %d sec", TimeUnit.MILLISECONDS.toMinutes(this.duration),
                TimeUnit.MILLISECONDS.toSeconds(this.duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this.duration)));
    }

    public String getLocation(){
        return this.location;
    }

    public float getLatLocation(){
        return this.latImg;
    }

    public float getLongLocation(){
        return this.longImg;
    }
}