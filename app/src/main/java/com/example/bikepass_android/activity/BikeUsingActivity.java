package com.example.bikepass_android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
                showDialog(this, "TOTAL PAYMENT : " + totalPaymentCount.getText());
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

    public void showDialog(Activity activity, String msg) {
        final String myResult = msg;
        Log.d("QRCodeScanner", msg);
        Log.d("QRCodeScanner", msg.toString());

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogbox_for_finish);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        text.setText(msg);

        Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dialogBtn_finish = (Button) dialog.findViewById(R.id.btn_okay);
        dialogBtn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), totalPaymentCount.getText() + " ODEME ALINDI", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BikeUsingActivity.this, ReportsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                dialog.cancel();
            }
        });

        dialog.show();
    }
}
