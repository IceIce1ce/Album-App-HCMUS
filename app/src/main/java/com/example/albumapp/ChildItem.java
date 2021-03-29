package com.example.albumapp;

public class ChildItem {

    // Declaration of the variable
    private String ChildItemPath;

    // Constructor of the class
    // to initialize the variable*
    public ChildItem(String childItemPath)
    {
        this.ChildItemPath = childItemPath;
    }

    // Getter and Setter method
    // for the parameter
    public String getChildItemPath()
    {
        return ChildItemPath;
    }

    public void setChildItemPath(String childItemPath) {
        ChildItemPath = childItemPath;
    }
}