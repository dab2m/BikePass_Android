package com.example.bikepass_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bikepass_android.R;

/**
 * Created by Berk on 11.02.2020
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button bCardDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent=getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bCardDetails = (Button)findViewById(R.id.bCard);
        bCardDetails.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bCard:
                startActivity(new Intent(this, CardDetailsActivity.class));
                //Toast.makeText(getApplicationContext(), "You need to define credit card as well for payment", Toast.LENGTH_LONG).show();
                break;
        }
    }
}