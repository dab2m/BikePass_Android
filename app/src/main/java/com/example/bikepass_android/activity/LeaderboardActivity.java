package com.example.bikepass_android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bikepass_android.R;

/**
 * Created by Berk on 27.02.2020
 */
public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton bRentBike;
    ImageButton bLocation;
    ImageButton bSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Intent intent=getIntent();
        Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bRentBike = (ImageButton)findViewById(R.id.map);
        bSettings = (ImageButton)findViewById(R.id.b5);
        bRentBike.setOnClickListener(this);
        bSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map:
                startActivity(new Intent(this, RentBikeActivity.class));
                break;
            case R.id.b5:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }
}