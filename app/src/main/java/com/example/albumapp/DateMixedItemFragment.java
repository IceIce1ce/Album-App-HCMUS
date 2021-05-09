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

import com.example.albumapp.item.ImageItem;
import com.example.albumapp.item.MixedItem;
import com.example.albumapp.item.NewParentItem;
import com.example.albumapp.item.NewParentItemAdapter;
import com.example.albumapp.item.VideoItem;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DateMixedItemFragment extends Fragment {
    RecyclerView rv;
    ArrayList <MixedItem> item_list;
    ArrayList<String> date_order;
    HashMap<String, ArrayList<MixedItem>> group;
    NewParentItemAdapter newparentItemAdapter;
    //public static String sort_order_date_header = "DATE_MODIFIED DESC";



    public static DateMixedItemFragment newInstance() {
        return new DateMixedItemFragment();
    }
    public static DateMixedItemFragment newInstance(ArrayList<String> img_list){
        //System.out.println(img_list);
        DateMixedItemFragment f = new DateMixedItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("PATH_LIST", (Serializable)img_list);
        f.setArguments(args);
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null)
            this.item_list = load_mixed_item(load_file_paths(this.getActivity()));
        else {
            ArrayList<String> _list = (ArrayList<String>) args.getSerializable("PATH_LIST");
            this.item_list = load_mixed_item(_list);
        }
        //System.out.println(path_list);
        //----
        this.group = groupPhotos(this.item_list);
        //----
        //MainActivity.swipeImg.setEnabled(false);
        View rootView = inflater.inflate(R.layout.date_fragment, container, false);
        rv = rootView.findViewById(R.id.parent_recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        newparentItemAdapter = new NewParentItemAdapter(getContext() , ParentItemList(this.group));
        rv.setAdapter(newparentItemAdapter);
        return rootView;
    }

    private List<NewParentItem> ParentItemList(HashMap<String, ArrayList<MixedItem>> g) {
        ArrayList<NewParentItem> itemList = new ArrayList<>();
        //System.out.println(g);
        int cnt1 = 0, cnt2 = this.item_list.size() - 1;
        for(String i: this.date_order){
            ArrayList<MixedItem> paths = g.get(i);
            List<MixedItem> ChildItemList = new ArrayList<>();
            for(MixedItem j: paths){
                if(MainActivity.sort_order_date_header.equals("DATE_MODIFIED DESC")){
                    j.setPos(cnt1);
                    ChildItemList.add(j);
                    cnt1++;
                }
                else if(MainActivity.sort_order_date_header.equals("DATE_MODIFIED ASC")){
                    j.setPos(cnt2);
                    ChildItemList.add(j);
                    cnt2--;
                }
            }
            itemList.add(new NewParentItem(i, ChildItemList));
        }
        return itemList;
    }


    public String date_string(Date time) {
        SimpleDateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);  //'at' hh:mm aaa"
        return f.format(time);
    }

    private HashMap<String, ArrayList<MixedItem>> groupPhotos(ArrayList<MixedItem> paths){
        HashMap<String, ArrayList<MixedItem>> g = new HashMap<>();
        this.date_order = new ArrayList<>();
        for (MixedItem item : paths){
            String item_path = item.getPath();
            if (item_path != null) {
                try {
                    Date item_date = new Date((new File(item_path)).lastModified());
                    String item_date_str = date_string(item_date);
                    if (!this.date_order.contains(item_date_str))
                        this.date_order.add(item_date_str);
                    if (g.isEmpty()) {
                        ArrayList<MixedItem> new_list = new ArrayList<>();
                        new_list.add(item);
                        g.put(item_date_str, new_list);
                    }
                    else {
                        ArrayList<String> cur_date_list = new ArrayList<>(g.keySet());
                        if (cur_date_list.contains(item_date_str)) {
                            for (String this_date : cur_date_list) {
                                if (item_date_str.equals(this_date)) {
                                    ArrayList<MixedItem> mixed_item_list = g.get(this_date);
                                    mixed_item_list.add(item);
                                    g.put(item_date_str, mixed_item_list);
                                    break;
                                }
                            }
                        }
                        else {
                            ArrayList<MixedItem> newList = new ArrayList<>();
                            newList.add(item);
                            g.put(item_date_str, newList);
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

    private ArrayList<String> load_file_paths(Activity activity) {
        ArrayList<String> list = new ArrayList<>();
        //MixedItem[] list;

        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, MainActivity.sort_order_date_header); //default: null
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                if (cursor.getString(0) != null) {
                    list.add(cursor.getString(0));
                }
            }
        }
        assert cursor != null;
        cursor.close();
        //
        cursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, MainActivity.sort_order_date_header);
        while(cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
        }
        cursor.close();
        return list;

    }
    private ArrayList<MixedItem> load_mixed_item(ArrayList<String> list){
        String[] listOfFiles=list.toArray(new String[list.size()]);
        //Arrays.sort(listOfFiles, Comparator.comparingLong(File::lastModified));
        Arrays.sort(listOfFiles, new Comparator<String>() {
            public int compare(String s1, String s2) {
                // Names beginning with 's' on top
                File f1 = new File(s1), f2 = new File(s2);
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });

        List<String> list1 = Arrays.asList(listOfFiles);
        ArrayList <MixedItem> result = new ArrayList<>();
        for (String i: list1){
            if(i.endsWith(".webp")||i.endsWith(".WEBP")||i.endsWith(".png")||i.endsWith(".jpg")||i.endsWith(".jpeg")||i.endsWith(".gif")||i.endsWith(".PNG")||i.endsWith(".JPG")||i.endsWith(".JPEG")||i.endsWith(".GIF")){
                result.add(new ImageItem(i));
            }
            else{
                result.add(new VideoItem(i, i));
            }
        }

        if (MainActivity.sort_order_date_header.equals("DATE_MODIFIED DESC"))
            Collections.reverse(result);
        return result;
    }
}
