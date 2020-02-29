package com.example.bikepass_android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bikepass_android.R;

/**
 * Created by Berk on 03.02.2020
 */
public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLeaderboard;
    ImageButton bRentBike;
    ImageButton bLocation;
    ImageButton bSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Intent intent=getIntent();
        Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bLeaderboard = (Button)findViewById(R.id.b1);
        bRentBike = (ImageButton)findViewById(R.id.b2);
        bSettings = (ImageButton)findViewById(R.id.b4);
        bLeaderboard.setOnClickListener(this);
        bRentBike.setOnClickListener(this);
        bSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b1:
                startActivity(new Intent(this, LeaderboardActivity.class));
                break;
            case R.id.b2:
                startActivity(new Intent(this, RentBikeActivity.class));
                break;
            case R.id.b4:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }
}