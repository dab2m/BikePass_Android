package com.example.bikepass_android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.bikepass_android.R;

public class MainActivity extends AppCompatActivity {


    public void loginInput(View view){

     EditText username=findViewById(R.id.username);
     EditText password=findViewById(R.id.password);

     Log.i("info","username:"+username.getText().toString());
     Log.i("info","password:"+password.getText().toString());


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
