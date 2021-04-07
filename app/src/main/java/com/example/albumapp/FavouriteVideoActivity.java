package com.example.albumapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavouriteVideoActivity extends Fragment {
    View favoriteVid;
    public static ArrayList<String> favoriteVideos = new ArrayList<>();
    ArrayList<Uri> vidFavouriteUri = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        favoriteVid = inflater.inflate(R.layout.activity_favourite_video, container, false);
        if(favoriteVideos != null && !favoriteVideos.isEmpty()){
            GridView favoriteVideoGallery = favoriteVid.findViewById(R.id.favoriteVideoGridView);
            favoriteVideoGallery.setAdapter(new ImageAdapter(FavouriteVideoActivity.super.getActivity(), favoriteVideos));
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            if(screenWidth > screenHeight) {
                favoriteVideoGallery.setNumColumns(6);
            }
            else {
                favoriteVideoGallery.setNumColumns(5);
            }
            favoriteVideoGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    VideoInfo s = new VideoInfo(favoriteVideos.get(position));
                    //convert lat and long of video location to address
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addresses;
                    String realAddressImg = "unknown";
                    try{
                        addresses = geocoder.getFromLocation(s.getLatLocation(), s.getLongLocation(), 1);
                        realAddressImg = addresses.get(0).getAddressLine(0);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    String DetailsVideo = "Name\n" + s.getFilename() + "\n\nPath\n" + favoriteVideos.get(position) + "\n\nSize\n" + s.getSize()
                            + "\n\nDuration\n" + s.getDuration()
                            + "\n\nResolution\n" + s.getResolution()
                            + "\n\nDate\n" + s.getDate()
                            + "\n\nLocation\n" + realAddressImg;
                    TextView title = new TextView(getContext());
                    title.setPadding(60, 30, 0, 0);
                    title.setText("Properties");
                    title.setTextSize(18.0f);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setTextColor(Color.BLACK);
                    AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).create();
                    dialog.setCustomTitle(title);
                    dialog.setMessage(DetailsVideo);
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            //
            favoriteVideoGallery.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
            favoriteVideoGallery.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    int selectCount = favoriteVideoGallery.getCheckedItemCount();
                    vidFavouriteUri.add(Uri.parse(favoriteVideos.get(position)));
                    switch (selectCount) {
                        case 1:
                            mode.setSubtitle("1 item selected");
                            break;
                        default:
                            mode.setSubtitle("" + selectCount + " items selected");
                            break;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater1 = mode.getMenuInflater();
                    inflater1.inflate(R.menu.menu_remove_images_wishlist, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.remove_favorite_items:
                            for(int i = 0; i < vidFavouriteUri.size(); i++){
                                favoriteVideos.remove(vidFavouriteUri.get(i).toString());
                                //remove from sharedpreference after removing from wishlist
                                SharedPreferences sharedPreferencesExcludeWishList = PreferenceManager.getDefaultSharedPreferences(getContext());
                                SharedPreferences.Editor editorExcludeWishList = sharedPreferencesExcludeWishList.edit();
                                Gson gsonExcludeWishList = new Gson();
                                String jsonExcludeWishList = gsonExcludeWishList.toJson(favoriteVideos);
                                editorExcludeWishList.putString("savedFavoriteVideos", jsonExcludeWishList);
                                editorExcludeWishList.apply();
                            }
                            Toast.makeText(getContext(), "Remove video from wishlist successfully!!!", Toast.LENGTH_SHORT).show();
                            mode.finish();
                            return true;
                        default: return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    vidFavouriteUri.clear();
                }
            });
            //
        }
        return favoriteVid;
    }
}