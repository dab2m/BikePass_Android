package com.example.bikepass_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.bikepass_android.R;

public class SignUpActivity extends AppCompatActivity {

    public void backToMainActivity(View view){

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void signUpInput(View view){


        EditText username=findViewById(R.id.usernamesignup);
        EditText password=findViewById(R.id.passwordsignup);
        EditText mail=findViewById(R.id.emailsignup);

        Log.i("info","username:"+username.getText().toString());
        Log.i("info","password:"+password.getText().toString());
        Log.i("info","mail:"+mail.getText().toString());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

}
