package com.example.bikepass_android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

        /**
         * Asagidaki kod parcasi RentBikeActivity'den bikeId'yi almak icin yazilmistir.
         */
        String id = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("key");

        }
        bikeId.setText(id);


        chronometer.setFormat("00:%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometerHelper = new ChronometerHelper();
        startStopWatch();
    }

    public void totalPaymentUpdater() {
        long elapsedTimeInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        long elapsedSeconds = elapsedTimeInMillis / 1000;
        totalPayment = totalPayment + ((int) (elapsedSeconds / 60)) * 0.25; // activity'de geri gidip tekrar sayfa acildiginda kaldigi ucretten devam etmesi icin yazildi
        totalPaymentCount.setText(totalPayment + " TL");

        final Handler ha = new Handler();

        System.out.println("ELAPSED SECOND:"+elapsedSeconds);
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                totalPayment = totalPayment + 0.25;
                totalPaymentCount.setText(totalPayment + " TL");

                ha.postDelayed(this, 60000);
            }
        }, 60000);

        /*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                totalPayment = totalPayment + 0.25;
                totalPaymentCount.setText(totalPayment + " TL");
            }
        };

        registerReceiver(minuteUpdateReceiver, intentFilter);*/
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
    public void onClick(View view) {

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

    }

    public static class ChronometerHelper {

        @Nullable
        private static Long mStartTime;

        @Nullable
        public Long getStartTime() {
            return mStartTime;
        }

        public void setStartTime(final long startTime) {
            this.mStartTime = startTime;
        }
    }
}
