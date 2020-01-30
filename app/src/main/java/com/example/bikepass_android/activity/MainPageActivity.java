package com.example.bikepass_android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bikepass_android.R;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonRentBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        buttonRentBike = (Button) findViewById(R.id.buttonRentBike);
        buttonRentBike.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRentBike:
                startActivity(new Intent(this, RentBikeActivity.class));
                break;
        }
    }
}
