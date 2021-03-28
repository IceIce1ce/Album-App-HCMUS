package com.example.albumapp;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

public abstract class MainActionModeCallback implements ActionMode.Callback {
    private ActionMode action;
    private MenuItem countItem;

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
        this.action = actionMode;
        this.countItem = menu.findItem(R.id.action_checked_count);
        return true;
    }

    public void setCount(String checkedCount) {
        if(countItem != null){
            this.countItem.setTitle(checkedCount);
        }
    }

    public ActionMode getAction() {
        return action;
    }
}