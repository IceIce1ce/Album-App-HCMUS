package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class FavouriteActivity extends Fragment {
    View favorite;
    public static ArrayList<String> favoriteImages = new ArrayList<>();
    ArrayList<Uri> imgFavouriteUri = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        favorite = inflater.inflate(R.layout.activity_favourite, container, false);
        if(favoriteImages != null && !favoriteImages.isEmpty()){
            GridView favoriteGallery = favorite.findViewById(R.id.favoriteGalleryGridView);
            favoriteGallery.setAdapter(new ImageAdapter(FavouriteActivity.super.getActivity(), favoriteImages));
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            if(screenWidth > screenHeight) {
                favoriteGallery.setNumColumns(6);
            }
            else {
                favoriteGallery.setNumColumns(3);
            }
            favoriteGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent favourite_intent = new Intent(getContext(), FullScreenImageActivity.class);
                    favourite_intent.putExtra("id", position);
                    favourite_intent.putExtra("path", favoriteImages.get(position));
                    favourite_intent.putExtra("allPath", favoriteImages);
                    String img_path = favoriteImages.get(position);
                    ImageInfo s = new ImageInfo(img_path);
                    favourite_intent.putExtra("display_image_name", s.getFilename());
                    startActivity(favourite_intent);
                }
            });
            favoriteGallery.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
            favoriteGallery.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    int selectCount = favoriteGallery.getCheckedItemCount();
                    imgFavouriteUri.add(Uri.parse(favoriteImages.get(position)));
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
                            for(int i = 0; i < imgFavouriteUri.size(); i++){
                                favoriteImages.remove(imgFavouriteUri.get(i).toString());
                                //remove from sharedpreference after removing from wishlist
                                SharedPreferences sharedPreferencesExcludeWishList = PreferenceManager.getDefaultSharedPreferences(getContext());
                                SharedPreferences.Editor editorExcludeWishList = sharedPreferencesExcludeWishList.edit();
                                Gson gsonExcludeWishList = new Gson();
                                String jsonExcludeWishList = gsonExcludeWishList.toJson(favoriteImages);
                                editorExcludeWishList.putString("savedFavoriteImages", jsonExcludeWishList);
                                editorExcludeWishList.apply();
                            }
                            Toast.makeText(getContext(), "Remove image from favourite list successfully!!!", Toast.LENGTH_SHORT).show();
                            mode.finish();
                            return true;
                        default: return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    imgFavouriteUri.clear();
                }
            });
        }
        return favorite;
    }
}