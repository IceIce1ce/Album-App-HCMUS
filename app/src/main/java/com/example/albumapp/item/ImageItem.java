package com.example.albumapp.item;

import java.io.File;

public class ImageItem implements MixedItem {
    private String path;
    boolean is_checked = false;
    int pos = -1;

    public ImageItem(String path, int pos) {
        this.path = path;
        this.pos = pos;
    }

    public ImageItem(String path) {
        this.path = path;
    }


    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public long getDate() {
        return (new File(this.path)).lastModified();
    }

    @Override
    public int getType() {
        return MixedItem.TYPE_IMAGE;
    }

    public ImageItem() {
        super();
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
