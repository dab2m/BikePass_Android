package com.example.bikepass_android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bikepass_android.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonLogin;
    EditText username;
    EditText password;

    public void setView(String userName,String passWord){

     username=findViewById(R.id.username);
     password=findViewById(R.id.password);
     username.setText(userName);
     password.setText(passWord);
     Toast.makeText(getApplicationContext(), "Welcome BikePass!", Toast.LENGTH_SHORT).show();


    }

    public void goToSignupActivity(View view){

        Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = (Button) findViewById(R.id.backtologin);
        buttonLogin.setOnClickListener(this);

        Intent intent=getIntent();
        if(intent.getStringExtra("username")!=null && intent.getStringExtra("password")!=null)
            setView(intent.getStringExtra("username"),intent.getStringExtra("password"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backtologin:
                startActivity(new Intent(this, MainPageActivity.class));
                break;
        }
    }
}
