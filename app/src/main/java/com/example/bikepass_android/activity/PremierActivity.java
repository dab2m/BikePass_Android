package com.example.bikepass_android.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.bikepass_android.R;

/**
 * Created by Mustafa on 10.02.2020
 */
public class PremierActivity extends AppCompatActivity {
    private static int CAMERA_REQUEST_CODE = 100;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premier);

        SharedPreferences sharedpreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (!sharedpreferences.getString("username", "").equals("")){
            //has login
            startActivity(new Intent(this, ReportsActivity.class));
            //must finish this activity (the premier activity will not be shown when click back in reports activity)
            finish();
        }

        // Request for camera permission from user when app is starting
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    public void toSignUp(View view){
        Intent signUpIntent = new Intent(this,SignUpActivity.class);
        startActivity(signUpIntent);
    }

    public void toSignIn(View view ){
        Intent loginIntent=new Intent(this,LoginActivity.class);
        startActivity(loginIntent);
    }

    public void toImage(View view ){
        Intent loginIntent=new Intent(this,ImageActivity.class);
        startActivity(loginIntent);
    }
}
