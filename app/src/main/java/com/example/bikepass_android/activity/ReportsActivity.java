package com.example.bikepass_android.activity;

import android.content.Intent;
import android.os.SystemClock;
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

    private String time = null;
    private String bikeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Intent intent = getIntent();
        //Toast.makeText(getApplicationContext(), "" + intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bLeaderboard = (Button) findViewById(R.id.b1);
        bRentBike = (ImageButton) findViewById(R.id.b2);
        bSettings = (ImageButton) findViewById(R.id.b4);
        bLeaderboard.setOnClickListener(this);
        bRentBike.setOnClickListener(this);
        bSettings.setOnClickListener(this);


    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            time = extras.getString("time");
            bikeId = extras.getString("bikeId");
        }
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(ReportsActivity.this, LoginActivity.class);
        startActivity(intent);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b1:
                startActivity(new Intent(this, LeaderboardActivity.class));
                break;
            case R.id.b2:
                //TODO eger kredi karti bilgileri girilmediyse bu sayfa acilmayacak once kart bilgilerini gir diye uyari cikacak

                Intent intent1 = new Intent(this, RentBikeActivity.class);
                Intent intent2 = new Intent(this, BikeUsingActivity.class);
                intent2.putExtra("bikeId", bikeId);

                if (time == null) {
                    startActivity(intent1);
                } else {
                    startActivity(intent2);
                }
                break;
            case R.id.b4:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }
}