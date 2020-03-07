package com.example.bikepass_android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.bikepass_android.R;

/**
 * Created by Berk on 11.02.2020
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button bCardDetails;
    Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent=getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bCardDetails = (Button)findViewById(R.id.bCard);
        bCardDetails.setOnClickListener(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
    }

        public  void logout(View view){
            SharedPreferences sharedpreferences = getSharedPreferences("loginPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bCard:
                startActivity(new Intent(this, CardDetailsActivity.class));
                //Toast.makeText(getApplicationContext(), "You need to define credit card as well for payment", Toast.LENGTH_LONG).show();
                break;
           case R.id.btnLogout:
                logout(v);
                break;
        }
    }
}