package com.example.bikepass_android.activity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.bikepass_android.R;

/**
 * Created by MustafaSaid on 30.01.2020
 */
public class BikeUsingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bikeId;
    private Button stopAndPay;
    private Chronometer chronometer;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_using);

        bikeId = (TextView) findViewById(R.id.bikeId);
        stopAndPay = (Button) findViewById(R.id.stopAndPay);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.start();
    }


    @Override
    public void onClick(View view) {

    }
}
