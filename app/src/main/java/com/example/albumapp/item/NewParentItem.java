package com.example.albumapp.item;

import java.util.List;

public class NewParentItem {
    private String ParentDateHeader;
    private List<MixedItem> MixedItemList;

    public NewParentItem(String ParentDateHeader, List<MixedItem> MixedItemList) {
        this.ParentDateHeader = ParentDateHeader;
        this.MixedItemList = MixedItemList;
    }

    public NewParentItem(String ParentDateHeader){
        this.ParentDateHeader = ParentDateHeader;
        this.MixedItemList = null;
    }

    public String getParentDateHeader() {
        return ParentDateHeader;
    }

    public void setParentDateHeader(String parentDateHeader) {
        ParentDateHeader = parentDateHeader;
    }

    public List<MixedItem> getMixedItemList() {
        return MixedItemList;
    }

    public void setMixedItemList(List<MixedItem> mixedItemList) {
        MixedItemList = mixedItemList;
    }
}
