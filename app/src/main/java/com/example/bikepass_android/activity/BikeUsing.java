package com.example.bikepass_android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;

import com.example.bikepass_android.R;

public class BikeUsing extends AppCompatActivity {

    private Chronometer chronometer;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_using);

        chronometer = findViewById(R.id.chronometer);
    }

    public void startChronometer(View v){

    }
}
