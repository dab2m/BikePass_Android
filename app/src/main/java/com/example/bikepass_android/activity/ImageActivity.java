package com.example.bikepass_android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.bikepass_android.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ImageActivity extends AppCompatActivity {

    Button btnTakePic;
    ImageView imageView;
    String pathTofile;

    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK) {
            if (requestCode == 1) {
            Bitmap bitmap = BitmapFactory.decodeFile(pathTofile);
            imageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.photo);
        btnTakePic = findViewById(R.id.btnTakePic);
         if(Build.VERSION.SDK_INT>=23){
           requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
         }

        btnTakePic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                try {
                    dispatchPictureTakerAction();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void dispatchPictureTakerAction() throws IOException {

        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePic.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;

            photoFile = createPhotoFile();

            if(photoFile!=null) {
                pathTofile = photoFile.getAbsolutePath();
                Uri photoURI= FileProvider.getUriForFile(ImageActivity.this,"com.thecodecity.cameraandroid.fileprovider",photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePic,1);
            }

        }
    }

    private File createPhotoFile() throws IOException {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir=getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=null;
        try {
           image = File.createTempFile(name, ".jpg", storageDir);

        }catch(Exception e){
            Log.d("myLog","Excep:"+e.toString());
        }
        return image;
        }


}
   /* public void getPhoto(){

        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String [] permissions,@NonNull int [] grantResults){

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(requestCode==1){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                getPhoto();
            }

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String [] {Manifest.permission.READ_EXTERNAL_STORAGE},1);

            }else{

                getPhoto();
            }


    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && requestCode==1 && resultCode==RESULT_OK && data != null){

            Uri selectedImage =data.getData();

            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);

                ImageView imageView=(ImageView)findViewById(R.id.photo);

                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }  */

