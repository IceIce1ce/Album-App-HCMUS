package com.example.albumapp.item;

import java.io.File;

public class VideoItem implements MixedItem{
    private String path, thumbnail;
    int pos = -1;
    boolean is_checked = false;

    public VideoItem(String path, int pos) {
        this.path = path;
        this.pos = pos;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public VideoItem(String path, String thumbnail) {
        this.path = path;
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public VideoItem(String path) {
        this.path = path;
    }
    @Override
    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getType() {
        return MixedItem.TYPE_VIDEO;
    }

    @Override
    public long getDate() {
        return (new File(this.path)).lastModified();
    }
    @Override
    public boolean isChecked() {
        return this.is_checked;
    }

    @Override
    public void setChecked() {
        this.is_checked = true;
    }

    @Override
    public void setUnChecked() {
        this.is_checked = false;
    }
}
