package com.example.albumapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Date_PictureFragment extends Fragment {
    private RecyclerView recyclerView = null;
    private ArrayList<String> path_list = null, date_order;
    private HashMap<String, ArrayList<String>> group = null;
    ParentItemAdapter parentItemAdapter;
    public static String sort_order_date_header = "DATE_MODIFIED DESC";



    public static Date_PictureFragment newInstance() {
        return new Date_PictureFragment();
    }
    public static Date_PictureFragment newInstance(ArrayList<String> img_list){
        System.out.println(img_list);
        Date_PictureFragment f = new Date_PictureFragment();
        Bundle args = new Bundle();
        args.putSerializable("STR_LIST", (Serializable)img_list);
        f.setArguments(args);
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null)
            this.path_list = loadHeader(this.getActivity());
        else {
            this.path_list = (ArrayList<String>) args.getSerializable("STR_LIST");
        }
        //System.out.println(path_list);
        //----
        this.group = groupPhotos(this.path_list);
        //----
        MainActivity.swipeImg.setEnabled(false);
        View rootView = inflater.inflate(R.layout.date_fragment, container, false);
        recyclerView = rootView.findViewById(R.id.parent_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        parentItemAdapter = new ParentItemAdapter(getContext() , ParentItemList(this.group));
        recyclerView.setAdapter(parentItemAdapter);
        return rootView;
    }

    private List<ParentItem> ParentItemList(HashMap<String, ArrayList<String>> g) {
        ArrayList<ParentItem> itemList = new ArrayList<>();
        System.out.println(g);
        int cnt1 = 0, cnt2 = PicturesActivity.images.size() - 1;
        for(String i: this.date_order){
            ArrayList<String> paths = g.get(i);
            List<ChildItem> ChildItemList = new ArrayList<>();
            for(String j: paths){
                if(sort_order_date_header.equals("DATE_MODIFIED DESC")){
                    ChildItemList.add(new ChildItem(j, cnt1));
                    cnt1++;
                }
                else if(sort_order_date_header.equals("DATE_MODIFIED ASC")){
                    ChildItemList.add(new ChildItem(j, cnt2));
                    cnt2--;
                }
            }
            itemList.add(new ParentItem(i, ChildItemList));
        }
        return itemList;
    }


    public String date_string(Date time) {
        SimpleDateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);  //'at' hh:mm aaa"
        return f.format(time);
    }

    private HashMap<String, ArrayList<String>> groupPhotos(ArrayList<String> paths){
        HashMap<String, ArrayList<String>> g = new HashMap<>();
        this.date_order = new ArrayList<>();
        for (String i : paths){
            if (i != null) {
                try {
                    Date i_date = new Date((new File(i)).lastModified());
                    String i_str = date_string(i_date);
                    if (!this.date_order.contains(i_str))
                        this.date_order.add(i_str);
                    if (g.isEmpty()) {
                        ArrayList<String> l = new ArrayList<>();
                        l.add(i);
                        g.put(i_str, l);
                    }
                    else {
                        ArrayList<String> d = new ArrayList<>(g.keySet());
                        boolean date_existed = false;
                        for (String j : d) {
                            if (i_str.equals(j)) {
                                ArrayList<String> l = g.get(j);
                                l.add(i);
                                g.put(i_str, l);
                                date_existed = true;
                                break;
                            }
                        }
                        if (!date_existed) {
                            ArrayList<String> l = new ArrayList<>();
                            l.add(i);
                            g.put(i_str, l);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return g;
    }

    private ArrayList<String> loadHeader(Activity activity) {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, sort_order_date_header); //default: null
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                if (cursor.getString(0) != null) {
                    list.add(cursor.getString(0));
                }
            }
        }
        assert cursor != null;
        cursor.close();
        return list;
    }
}