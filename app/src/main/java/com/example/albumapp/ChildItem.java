package com.example.albumapp;

public class ChildItem {
    private String ChildItemPath;

    public ChildItem(String childItemPath) {
        this.ChildItemPath = childItemPath;
    }

    public String getChildItemPath() {
        return ChildItemPath;
    }

    public void setChildItemPath(String childItemPath) {
        ChildItemPath = childItemPath;
    }
}