package com.example.bikepass_android.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikepass_android.R;

/**
 * Created by MustafaSaid on 30.01.2020
 */
public class BikeUsingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bikeId;
    private Chronometer chronometer;
    private TextView totalPaymentCount;
    private Button stopAndPayButton;

    ChronometerHelper chronometerHelper;

    private double totalPayment = 1.5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_using);

        bikeId = (TextView) findViewById(R.id.bikeId);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        totalPaymentCount = (TextView) findViewById(R.id.totalPaymentCount);
        stopAndPayButton = (Button) findViewById(R.id.stopAndPayButton);
        stopAndPayButton.setOnClickListener(this);

        /**
         * Asagidaki kod parcasi RentBikeActivity'den bikeId'yi almak icin yazilmistir.
         */
        String id = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("key") != null) {
            id = extras.getString("key");
        }
        if (extras != null && extras.getString("bikeId") != null) {
            id = extras.getString("bikeId");
        }
        bikeId.setText(id);


        chronometer.setFormat("00:%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometerHelper = new ChronometerHelper();
        chronometerHelper.setStartTime(null);
        startStopWatch();
    }

    public void totalPaymentUpdater() {
        /*long elapsedTimeInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        long elapsedSeconds = elapsedTimeInMillis / 1000;
        totalPayment = totalPayment + ((int) (elapsedSeconds / 60)) * 0.25; // activity'de geri gidip tekrar sayfa acildiginda kaldigi ucretten devam etmesi icin yazildi
        */
        totalPaymentCount.setText(totalPayment + " TL");

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                totalPayment = totalPayment + 0.25;
                totalPaymentCount.setText(totalPayment + " TL");

                ha.postDelayed(this, 60000);
            }
        }, 60000);
    }


    @Override
    protected void onResume() {
        super.onResume();
        totalPaymentUpdater();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stopAndPayButton:
                Toast.makeText(getApplicationContext(), "ODEME ALINDI", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BikeUsingActivity.this, ReportsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
    }

    private void startStopWatch() {
        if (chronometerHelper.getStartTime() == null) {
            long startTime = SystemClock.elapsedRealtime();
            chronometerHelper.setStartTime(startTime);
            chronometer.setBase(startTime);
        } else {
            chronometer.setBase(chronometerHelper.getStartTime());
        }

        chronometer.start();
        //TODO yeni bir bisiklet kiralanirken qr okutulunca kronometre eski degerden basliyor
    }

    public static class ChronometerHelper {

        @Nullable
        private static Long mStartTime;

        @Nullable
        public Long getStartTime() {
            return mStartTime;
        }

        public void setStartTime(final Long startTime) {
            this.mStartTime = startTime;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "You can't go back while the chronometer is running!", Toast.LENGTH_SHORT).show();

        /*Intent intent = new Intent(BikeUsingActivity.this, ReportsActivity.class);
        intent.putExtra("time", String.valueOf(SystemClock.elapsedRealtime() - chronometer.getBase()));
        intent.putExtra("bikeId", bikeId.getText());
        startActivity(intent);*/
    }
}
