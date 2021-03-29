package com.example.albumapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class Date_PictureActivity extends Fragment{
    // Add RecyclerView member
    //public static final long MILLIS_PER_DAY = 86400000L;
    private RecyclerView recyclerView = null;
    private ArrayList<String> path_list = null, date_order;
    private HashMap<String, ArrayList<String>> group = null;
    //private GridLayoutManager mGridLayoutManager1, mGridLayoutManager2, mGridLayoutManager3;
    ParentItemAdapter parentItemAdapter;
    private ScaleGestureDetector mScaleGestureDetector;

    public String date_string(Date time) {
        SimpleDateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);  // 'at' hh:mm aaa");
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
                                //System.out.println("Found same date img");
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
        //todo: get MediaStore.Videos
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA}, null, null, "DATE_MODIFIED DESC"); //default: null

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
    public static Date_PictureActivity newInstance() {return new Date_PictureActivity();}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.path_list = loadHeader(this.getActivity());
        this.group = groupPhotos(this.path_list);
        View rootView = inflater.inflate(R.layout.date_fragment, container, false);

        // REFERENCE
        recyclerView = (RecyclerView)rootView.findViewById(R.id.parent_recyclerview);
        //recyclerView.setHasFixedSize(true);

        //LAYOUT MANAGER
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        //LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

        //ADAPTER
        parentItemAdapter = new ParentItemAdapter(getContext() , ParentItemList(this.group));
        recyclerView.setAdapter(parentItemAdapter);
/*
        mScaleGestureDetector = new ScaleGestureDetector((rootView.getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (detector.getCurrentSpan() > 200 && detector.getTimeDelta() > 200) {
                    if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                        if (mCurrentLayoutManager == mGridLayoutManager1) {
                            mCurrentLayoutManager = mGridLayoutManager2;
                            mRvPhotos.setLayoutManager(mGridLayoutManager2);
                            return true;
                        } else if (mCurrentLayoutManager == mGridLayoutManager2) {
                            mCurrentLayoutManager = mGridLayoutManager3;
                            mRvPhotos.setLayoutManager(mGridLayoutManager3);
                            return true;
                        }
                    } else if(detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                        if (mCurrentLayoutManager == mGridLayoutManager3) {
                            mCurrentLayoutManager = mGridLayoutManager2;
                            mRvPhotos.setLayoutManager(mGridLayoutManager2);
                            return true;
                        } else if (mCurrentLayoutManager == mGridLayoutManager2) {
                            mCurrentLayoutManager = mGridLayoutManager1;
                            mRvPhotos.setLayoutManager(mGridLayoutManager1);
                            return true;
                        }
                    }
                }

            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return false;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
            }
        }));
*/

        return rootView;
    }


    private List<ParentItem> ParentItemList(HashMap<String, ArrayList<String>> g) {
        ArrayList<ParentItem> itemList = new ArrayList<>();
        ArrayList<String> dl = new ArrayList<>(g.keySet());
        System.out.println(g);
        for (String i : this.date_order){
            itemList.add(new ParentItem(i, ChildItemList(g.get(i))));
        }

        return itemList;
    }
    // Method to pass the arguments
    // for the elements
    // of child RecyclerView
    private List<ChildItem> ChildItemList(ArrayList<String> paths)
    {
        List<ChildItem> ChildItemList = new ArrayList<>();
        for (String i : paths){
            ChildItemList.add(new ChildItem(i));
        }
        return ChildItemList;
    }
}