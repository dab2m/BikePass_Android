package com.example.bikepass_android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by MustafaSaid on 30.01.2020
 */
public class BikeUsingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bikeId;
    private Chronometer chronometer;
    private TextView totalPaymentCount;
    private Button stopAndPayButton;

    ChronometerHelper chronometerHelper;

    private int totalPayment = 600;

    private String username;
    private int bikeTime;
    private String total_credit;
    private String total_coin;
    private int new_total_credit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_using);

        bikeId = (TextView) findViewById(R.id.bikeId);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        totalPaymentCount = (TextView) findViewById(R.id.totalPaymentCount);
        stopAndPayButton = (Button) findViewById(R.id.stopAndPayButton);
        stopAndPayButton.setOnClickListener(this);

        SharedPreferences preferences = getSharedPreferences("total_credit", getApplicationContext().MODE_PRIVATE);
        total_credit = preferences.getString("total_credit", null);

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
        if (extras != null && extras.get("username") != null) {
            username = extras.getString("username");
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
        totalPaymentCount.setText(totalPayment + " coin");

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (chronometer.getText().toString().substring(6).equals("59")) // kronometre ekrani kapatilip acildiginda yanlis zamanda ucret eklemesi yapilmasin diye yazildi
                    totalPayment = totalPayment + 600;
                totalPaymentCount.setText(totalPayment + " coin");

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
                long elapsedTimeInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                long elapsedSeconds = elapsedTimeInMillis / 1000;
                bikeTime = (int) elapsedSeconds;
                total_coin = (String) totalPaymentCount.getText().subSequence(0, totalPaymentCount.getText().toString().indexOf(" "));
                showDialog(this, "TOTAL PAYMENT : " + total_coin);


                new_total_credit = Integer.parseInt(total_credit) - Integer.parseInt(total_coin);


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

        Button dialogBtn_finish = (Button) dialog.findViewById(R.id.btn_finish);
        dialogBtn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // REST API
                MyAsyncBikeId async = new MyAsyncBikeId();
                String bikeStatus = null;
                try {
                    bikeStatus = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //bikeStatusParser(bikeStatus);

                MyAsyncForUpdateCredit creditUpdater = new MyAsyncForUpdateCredit();
                try {
                    creditUpdater.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), total_coin.toUpperCase() + " COINS ARE DEDUCTED FROM YOUR TOTAL COINS", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BikeUsingActivity.this, ReportsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                dialog.cancel();
            }
        });

        dialog.show();
    }


    class MyAsyncBikeId extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {
            String message;

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", username);
                jsonObject.put("bike_id", bikeId.getText());
                jsonObject.put("bike_time", bikeTime);
                //jsonObject.put("bike_km", 0);
                //jsonObject.put("credit",25); //TODO: databasede creditten dusecek ve credit kullanilan sureye gore belirlenecek
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(String.valueOf(jsonObject));
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                response = sb.toString().trim();
                JSONObject jObj = new JSONObject(response);
                message = jObj.getString("message"); // request sonucu donen bisikletin durum mesaji
                String status = jObj.getString("status"); // request sonucu donen bisikletin statusu

                isr.close();
                reader.close();

                Log.i("status", status);
                Log.i("message", message);

                return status;
            } catch (IOException e) {
                // Error
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class MyAsyncForUpdateCredit extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {
            String message;

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("usernamecredit", username);
                jsonObject.put("credit", new_total_credit);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(String.valueOf(jsonObject));
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                response = sb.toString().trim();
                JSONObject jObj = new JSONObject(response);
                message = jObj.getString("message"); // request sonucu donen bisikletin durum mesaji
                String status = jObj.getString("status"); // request sonucu donen bisikletin statusu

                isr.close();
                reader.close();

                Log.i("status", status);
                Log.i("message", message);

                return status;
            } catch (IOException e) {
                // Error
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
