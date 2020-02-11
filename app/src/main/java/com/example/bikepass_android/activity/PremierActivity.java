package com.example.bikepass_android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.bikepass_android.R;

/**
 * Created by Mustafa on 10.02.2020
 */
public class PremierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premier);

    }

    public void toSignUp(View view){
        Intent signUpIntent = new Intent(this,SignUpActivity.class);
        startActivity(signUpIntent);
    }

    public void toSignIn(View view ){
        Intent loginIntent=new Intent(this,LoginActivity.class);
        startActivity(loginIntent);
    }
}
