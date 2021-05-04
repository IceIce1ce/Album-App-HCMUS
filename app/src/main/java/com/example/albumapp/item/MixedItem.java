package com.example.albumapp.item;

public interface MixedItem{
    int TYPE_IMAGE = 100;
    int TYPE_VIDEO = 101;
    int getType();
    String getPath();
    int getPos();
    void setPos(int pos);
    long getDate();
    boolean isChecked();
    void setChecked();
    void setUnChecked();
}
