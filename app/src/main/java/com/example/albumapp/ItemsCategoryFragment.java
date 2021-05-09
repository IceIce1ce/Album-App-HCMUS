package com.example.albumapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.item.ImageItem;
import com.example.albumapp.item.MixedItem;
import com.example.albumapp.item.NewParentItem;
import com.example.albumapp.item.NewParentItemAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsCategoryFragment extends Fragment {
    RecyclerView rv;
    //ArrayList <MixedItem> item_list;
    ArrayList<String> header_order;
    HashMap<String, ArrayList<MixedItem>> group;
    NewParentItemAdapter newparentItemAdapter;
    //public static String sort_order_date_header = "DATE_MODIFIED DESC";


    public static ItemsCategoryFragment newInstance(HashMap<String, String> img_list) {
        ItemsCategoryFragment f = new ItemsCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("PATH_HASHMAP", (Serializable) img_list);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        HashMap<String, String> _list = (HashMap<String, String>) args.getSerializable("PATH_HASHMAP");
        this.group = groupPhotos(_list);
        //MainActivity.swipeImg.setEnabled(false);
        View rootView = inflater.inflate(R.layout.date_fragment, container, false);
        rv = rootView.findViewById(R.id.parent_recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        newparentItemAdapter = new NewParentItemAdapter(getContext(), ParentItemList(this.group));
        rv.setAdapter(newparentItemAdapter);
        return rootView;
    }

    private List<NewParentItem> ParentItemList(HashMap<String, ArrayList<MixedItem>> g) {
        ArrayList<NewParentItem> itemList = new ArrayList<>();
        Collections.sort(this.header_order);
        for (String i : this.header_order) {
            if(i.equals("0 people found"))
                continue;
            ArrayList<MixedItem> paths = g.get(i);
            List<MixedItem> ChildItemList = new ArrayList<>();
            for (MixedItem j : paths) {
                ChildItemList.add(j);
            }
            itemList.add(new NewParentItem(i, ChildItemList));
        }
        return itemList;
    }


    private HashMap<String, ArrayList<MixedItem>> groupPhotos(HashMap<String, String> paths) {
        HashMap<String, ArrayList<MixedItem>> result = new HashMap<>();
        this.header_order = new ArrayList<>();
        for (Map.Entry<String, String> entry : paths.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!this.header_order.contains(value))
                this.header_order.add(value);
            if (result.isEmpty()) {
                ArrayList<MixedItem> new_list = new ArrayList<>();
                new_list.add(new ImageItem(key));
                result.put(value, new_list);
            } else {
                //ArrayList<String> cur_header_list = new ArrayList<>(result.keySet());
                ArrayList<MixedItem> mixed_item_list = result.get(value);
                if (mixed_item_list == null) {
                    ArrayList<MixedItem> newList = new ArrayList<>();
                    newList.add(new ImageItem(key));
                    result.put(value, newList);
                } else { //cur_header_list.contains(value) == true
                    mixed_item_list.add(new ImageItem(key));
                    result.put(value, mixed_item_list);
                }
            }
        }
        return result;
    }
}


