package com.example.bikepass_android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bikepass_android.R;

/**
 * Created by Berk on 03.02.2020
 */
public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLeaderboard;
    ImageButton goMap;
    ImageButton bRentBike;
    ImageButton bLocation;
    ImageButton bSettings;
    TextView view;


    private String time = null;
    private String bikeId = null;
    private String user_name;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Intent intent = getIntent();
        //Toast.makeText(getApplicationContext(), "" + intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bLeaderboard = (Button) findViewById(R.id.worldleaderboard);
        bRentBike = (ImageButton) findViewById(R.id.returnbikes);
        bSettings = (ImageButton) findViewById(R.id.map);
        goMap = (ImageButton) findViewById(R.id.bikes);
        goMap.setOnClickListener(this);
        bLeaderboard.setOnClickListener(this);
        bRentBike.setOnClickListener(this);
        bSettings.setOnClickListener(this);
        view = findViewById(R.id.textview);
        SharedPreferences sharedpreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        user_name = sharedpreferences.getString("username", "");
        view.setText("Welcome back," + user_name + "!");


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


    public void goToMapActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(intent);
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(ReportsActivity.this, LoginActivity.class);
        startActivity(intent);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.worldleaderboard:
                startActivity(new Intent(this, LeaderboardActivity.class));
                break;
            case R.id.returnbikes:
                //TODO eger kredi karti bilgileri girilmediyse bu sayfa acilmayacak once kart bilgilerini gir diye uyari cikacak

                SharedPreferences preferences = getSharedPreferences("username", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", user_name);
                editor.commit();


                Intent intent1 = new Intent(this, RentBikeActivity.class);
                Intent intent2 = new Intent(this, BikeUsingActivity.class);
                intent2.putExtra("bikeId", bikeId);
                intent2.putExtra("username", user_name);

                if (time == null) {
                    startActivity(intent1);
                } else {
                    startActivity(intent2);
                }
                break;
            case R.id.map:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.bikes:
                goToMapActivity(v);
                break;

        }
    }
}