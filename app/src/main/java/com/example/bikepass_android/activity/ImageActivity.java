package com.example.bikepass_android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.bikepass_android.R;

import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    public void getPhoto(){

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

       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String [] {Manifest.permission.READ_EXTERNAL_STORAGE},1);

            }else{

                getPhoto();
            }
        //}


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data != null){

            Uri selectedImage =data.getData();

            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);

                ImageView imageView=(ImageView)findViewById(R.id.photo);

                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
