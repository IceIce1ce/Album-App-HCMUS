package com.example.albumapp.item;

import java.util.List;

public class ParentItem {
    private String ParentDateHeader;
    private List<ChildItem> ChildItemList;

    public ParentItem(String ParentDateHeader, List<ChildItem> ChildItemList) {
        this.ParentDateHeader = ParentDateHeader;
        this.ChildItemList = ChildItemList;
    }

    public ParentItem(String ParentDateHeader){
        this.ParentDateHeader = ParentDateHeader;
        this.ChildItemList = null;
    }

    public String getParentDateHeader() {
        return ParentDateHeader;
    }

    public void setParentDateHeader(String parentDateHeader) {
        ParentDateHeader = parentDateHeader;
    }

    public List<ChildItem> getChildItemList() {
        return ChildItemList;
    }

    public void setChildItemList(List<ChildItem> childItemList) {
        ChildItemList = childItemList;
    }
}