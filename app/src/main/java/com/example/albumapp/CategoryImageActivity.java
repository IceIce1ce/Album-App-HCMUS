package com.example.albumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CategoryImageActivity extends AppCompatActivity{
    HashMap<String, String> saveCategoryResult = new HashMap<>();
    ArrayList<String> categoryResult = new ArrayList<>();
    ArrayList<Uri> getAllImage = new ArrayList<>();
    //show progress dialog before categorizing image
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_image);
        findViewById(R.id.start_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageProcessing().execute();
            }
        });
        findViewById(R.id.print_category_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String result = readFileAsString("CATEGORY_IMAGE_DATA.txt");
                Type type = new TypeToken<HashMap<String, String>>() {}.getType();
                HashMap<String, String> backup_result = gson.fromJson(result, type);
                Toast.makeText(CategoryImageActivity.this, backup_result.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //https://stackoverflow.com/questions/5960247/convert-bitmap-array-to-yuv-ycbcr-nv21
    byte [] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {
        int [] argb = new int[inputWidth * inputHeight];
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        byte [] yuv = new byte[inputWidth*inputHeight*3/2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
        scaled.recycle();
        return yuv;
    }

    void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }
                index++;
            }
        }
    }

    private String readFileAsString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(getApplicationContext().getFilesDir(), fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void setUpFaceDetector(Uri selectedImage) throws FileNotFoundException{
        FaceDetectorOptions options = new FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST).build();
        InputImage image = null;
        try {
            InputStream ims = getContentResolver().openInputStream(selectedImage);
            Bitmap b = BitmapFactory.decodeStream(ims);
            Bitmap myBitmap = Bitmap.createScaledBitmap(b, 320, 240, false);
            byte[] myByte = getNV21(320, 240, myBitmap);
            image = InputImage.fromByteArray(myByte, 320, 240, 0, InputImage.IMAGE_FORMAT_NV21);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        FaceDetector detector = FaceDetection.getClient(options);
        Task<List<Face>> result = detector.process(Objects.requireNonNull(image)).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                if(faces.size() < 1){
                    categoryResult.add("0 people found");
                }
                else{
                    categoryResult.add(faces.size() + " people found");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private class ImageProcessing extends AsyncTask{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CategoryImageActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{
                for(int i = 0; i < PicturesActivity.images.size(); i++){
                    getAllImage.add(Uri.fromFile(new File(PicturesActivity.images.get(i))));
                }
                try{
                    for(int i = 0; i < getAllImage.size(); i++){
                        setUpFaceDetector(getAllImage.get(i));
                    }
                    /*use below code to show toast in thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < categoryResult.size(); i++){
                        saveCategoryResult.put(PicturesActivity.images.get(i), categoryResult.get(i));
                    }
                    for (Map.Entry<String,String> entry : saveCategoryResult.entrySet()){
                        Toast.makeText(CategoryImageActivity.this, entry.getKey() + " " + entry.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
            Toast.makeText(CategoryImageActivity.this, "Category images successfully!", Toast.LENGTH_SHORT).show();
            for(int i = 0; i < categoryResult.size(); i++){
                saveCategoryResult.put(PicturesActivity.images.get(i), categoryResult.get(i));
            }
            /*
            for(Map.Entry<String,String> entry : saveCategoryResult.entrySet()){
                Toast.makeText(CategoryImageActivity.this, entry.getKey() + " " + entry.getValue(), Toast.LENGTH_SHORT).show();
            }*/
            Gson gson = new Gson();
            String hashMapString = gson.toJson(saveCategoryResult);
            writeToFile(hashMapString,"CATEGORY_IMAGE_DATA.txt");
        }

        private void writeToFile(final String fileContents, String fileName) {
            System.out.println(fileName);
            System.out.println(fileContents);
            try {
                FileWriter out = new FileWriter(new File(getApplicationContext().getFilesDir(), fileName));
                out.write(fileContents);
                out.close();
                Toast.makeText(CategoryImageActivity.this, "File saved at " + fileName, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(CategoryImageActivity.this, "File save failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    //use code below with firebase ml
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_image);
        for(int i = 0; i < PicturesActivity.images.size(); i++){
            getAllImage.add(Uri.fromFile(new File(PicturesActivity.images.get(i))));
        }
        try{
            for(int i = 0; i < getAllImage.size(); i++){
                setUpFaceDetector(getAllImage.get(i));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setUpFaceDetector(Uri selectedImage) throws FileNotFoundException{
        FaceDetectorOptions options = new FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST).build();
        InputImage image = null;
        try {
            InputStream ims = getContentResolver().openInputStream(selectedImage);
            Bitmap b = BitmapFactory.decodeStream(ims);
            Bitmap myBitmap = Bitmap.createScaledBitmap(b, 480, 360, false); //320x240
            image = InputImage.fromBitmap(myBitmap, 0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        FaceDetector detector = FaceDetection.getClient(options);
        Task<List<Face>> result = detector.process(Objects.requireNonNull(image)).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                if(faces.size() < 1){
                    Toast.makeText(CategoryImageActivity.this, "0 people found", Toast.LENGTH_SHORT).show();
                }
                else if(faces.size() == 1){
                    Toast.makeText(CategoryImageActivity.this, "1 people found", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(CategoryImageActivity.this, faces.size() + " people found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }*/

    /*use code below with google vision
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_image);
        for(int i = 0; i < PicturesActivity.images.size(); i++){
            getAllImage.add(Uri.fromFile(new File(PicturesActivity.images.get(i))));
        }
        try{
            for(int i = 0; i < getAllImage.size(); i++){
                setUpFaceDetector(getAllImage.get(i));
            }
            for(int i = 0; i < categoryResult.size(); i++){
                saveCategoryResult.put(PicturesActivity.images.get(i), categoryResult.get(i));
            }
            for (Map.Entry<String,String> entry : saveCategoryResult.entrySet()){
                Toast.makeText(this, entry.getKey() + " " + entry.getValue(), Toast.LENGTH_SHORT).show();
            }
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setUpFaceDetector(Uri selectedImage) throws FileNotFoundException{
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false).setMode(FaceDetector.FAST_MODE)
                .setClassificationType(FaceDetector.NO_CLASSIFICATIONS).setLandmarkType(FaceDetector.NO_LANDMARKS).build();
        if(!faceDetector.isOperational()){
            Toast.makeText(this, "Could not set up the face detector!", Toast.LENGTH_SHORT).show();
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        InputStream ims = getContentResolver().openInputStream(selectedImage);
        Bitmap b = BitmapFactory.decodeStream(ims);
        Bitmap myBitmap = Bitmap.createScaledBitmap(b, 320, 240, false);
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        if(faces.size() < 1){
            categoryResult.add("0 people found");
        }
        else{
            categoryResult.add(faces.size() + " people found");
        }
        faceDetector.release();
    }*/
}