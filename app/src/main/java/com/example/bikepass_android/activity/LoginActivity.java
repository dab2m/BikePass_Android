package com.example.bikepass_android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bikepass_android.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonLogin;


    public void loginInput(View view){

     EditText username=findViewById(R.id.username);
     EditText password=findViewById(R.id.password);

     Log.i("info","username:"+username.getText().toString());
     Log.i("info","password:"+password.getText().toString());

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
