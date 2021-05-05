package com.example.albumapp;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlbumPickerAdapter extends RecyclerView.Adapter<AlbumPickerAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    ArrayList<AlbumItem> albumItemlist;
    public static String target_path;
    Context context;
    private String file_extension;
    public AlbumPickerAdapter(Context context, ArrayList<AlbumItem> albumItemlist, String target) {
        this.albumItemlist = albumItemlist;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.target_path = target;
        int index = target.lastIndexOf('.');
        this.file_extension = target.substring(index);
    }

    @Override
    public AlbumPickerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.album_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumPickerAdapter.MyViewHolder holder, int position) {
        AlbumItem item = albumItemlist.get(position);
        ArrayList<String> img_list = item.getAlbum_path_list();
        if (item.getIs_sd())
            holder.sdcard_img.setVisibility(View.VISIBLE);
        else
            holder.sdcard_img.setVisibility(View.INVISIBLE);
        holder.album_title.setText(item.getAlbum_name());
        holder.album_img_number.setText(String.valueOf(img_list.size()) + " " + context.getResources().getString(R.string.album_img_number));
        Glide.with(context)
                .load(img_list.get(0))
                .apply(RequestOptions.centerCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(800, 800)
                .into(holder.album_img);
        //holder.name.setText(myImageNameList[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity.textView.setText("You have selected : "+myImageNameList[getAdapterPosition()]);
                if (proceed(AlbumPickerAdapter.target_path, item.getAlbum_folder()))
                    Toast.makeText(context, "Copy file successfully!!!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Copy file fail!!!", Toast.LENGTH_SHORT).show();
                try{
                    FullScreenImageActivity.dialog.dismiss();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    OpenImageFileActivity.dialog.dismiss();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    OpenVideoFileActivity.dialog.dismiss();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    FullScreenVideoActivity.dialog.dismiss();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    FullScreenImageActivity2.dialog.dismiss();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArrayList<String> path_list_ = item.getAlbum_path_list();
                for (String i : path_list_){
                    System.out.println(i);
                }
                return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return this.albumItemlist.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView album_title, album_img_number;
        ImageView album_img, sdcard_img;
        MyViewHolder(View itemView){
            super(itemView);
            album_img_number = itemView.findViewById(R.id.album_img_number);
            album_img = itemView.findViewById(R.id.album_image);
            album_title = itemView.findViewById(R.id.album_title);
            sdcard_img = itemView.findViewById(R.id.album_is_sdcard);
        }
    }
    private File exportFile(File src, File dst) throws IOException {

        //if folder does not exist
        if (!dst.exists()) {
            if (!dst.mkdir()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = dst.getPath() + File.separator + "IMG_" + timeStamp + "_copied" + file_extension;
        System.out.println(path);
        File expFile = new File(path);
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(expFile).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }

        return expFile;
    }
    private boolean proceed(String file_to_copy, String destination_folder){
        System.out.println(file_to_copy);
        File source = new File(file_to_copy);
        //String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "TEST_FOLDER";

        File dest = new File(destination_folder);
        // Albumpath = /storage/emulated/0/Pictures
        try{
            //copyFileUsingStream(source, dest);
            File result = exportFile(source,dest);
            //
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                /*
                final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                final Uri contentUri = Uri.fromFile(dest);
                scanIntent.setData(contentUri);
                context.sendBroadcast(scanIntent);
                */
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(result)));

            } else {
                final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(result));
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newMediaFile)));

                context.sendBroadcast(intent);
            }
            return true;
            //Toast.makeText(this, "Move image successfully!!!", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
            //Toast.makeText(this, "Move image fail!!!", Toast.LENGTH_SHORT).show();
        }

    }
}
