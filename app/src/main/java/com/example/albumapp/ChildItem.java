package com.example.albumapp;

public class ChildItem {
    private String ChildItemPath;
    private int pos = -1;

    public int getPos() {
        return pos;
    }

    public ChildItem(String childItemPath) {
        this.ChildItemPath = childItemPath;
    }

    public ChildItem(String childItemPath, int pos) {
        ChildItemPath = childItemPath;
        this.pos = pos;
    }

    public String getChildItemPath() {
        return ChildItemPath;
    }

    public void setChildItemPath(String childItemPath) {
        ChildItemPath = childItemPath;
    }
}