package com.example.albumapp;

import java.io.File;
import java.util.ArrayList;

public class AlbumItem {
    private ArrayList<String> album_path_list;
    private String album_name, album_folder;
    private boolean is_sd;
    public AlbumItem(String album_name, ArrayList<String> album_path_list) {
        this.album_path_list = album_path_list;
        this.album_name = album_name;
        this.is_sd = false;
        if (!this.album_path_list.isEmpty()) {
            File file = new File(this.album_path_list.get(0));
            this.album_folder = file.getParent();
        }
        else
            this.album_folder = "";
    }
    /*
    public AlbumItem(String album_name) {
        this.album_name = album_name;
        this.album_path_list = null;
        this.is_sd = false;
    }
*/

    public String getAlbum_folder() {
        return album_folder;
    }

    public boolean isIs_sd() {
        return is_sd;
    }

    public ArrayList<String> getAlbum_path_list() {
        return album_path_list;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_path_list(ArrayList<String> album_path_list) {
        this.album_path_list = album_path_list;
    }

    public void setIs_sd(boolean is_sd) {
        this.is_sd = is_sd;
    }

    public boolean getIs_sd() {
        return is_sd;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public void addAlbum_path(String new_path) throws Exception {
        if(this.album_path_list==null){
            this.album_path_list = new ArrayList<>();
        }
        if(this.album_path_list.contains(new_path))
            throw new Exception("Image: " + new_path + " already existed in " + this.album_name);
        this.album_path_list.add(new_path);
    }
}
