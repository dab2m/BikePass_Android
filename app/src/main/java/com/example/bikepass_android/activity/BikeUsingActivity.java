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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by MustafaSaid on 30.01.2020
 */
public class BikeUsingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bikeId;
    private Chronometer chronometer;
    private TextView totalPaymentCount;
    private Button stopAndPayButton;
    private Button mapButton;

    ChronometerHelper chronometerHelper;

    private int totalPayment = 600;

    private String username;
    private int bikeTime;
    private String total_credit;
    private String total_coin;
    private int new_total_credit;
    private int earnCredit;
    private String penaltyCredit = null;

    private float lat;
    private float lng;

    private List<Hotspots> hotpointList = new ArrayList<Hotspots>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_using);

        bikeId = (TextView) findViewById(R.id.bikeId);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        totalPaymentCount = (TextView) findViewById(R.id.totalPaymentCount);
        stopAndPayButton = (Button) findViewById(R.id.stopAndPayButton);
        mapButton = (Button) findViewById(R.id.map_button);

        stopAndPayButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);

        SharedPreferences preferences = getSharedPreferences("total_credit", getApplicationContext().MODE_PRIVATE);
        total_credit = preferences.getString("total_credit", null);

        SharedPreferences prefs = getSharedPreferences("LOCATION", MODE_PRIVATE);
        lat = prefs.getFloat("lat", 0);
        lng = prefs.getFloat("lng", 0);
        Log.i("LAT", String.valueOf(lat));
        Log.i("LONG", String.valueOf(lng));

        getRequestForHotpoints(); // store in hotpointList


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
        if (extras != null && extras.getString("penaltyCredit") != null) {
            penaltyCredit = extras.getString("penaltyCredit");
        }
        if (penaltyCredit != null) {
            setTotalPayment(Integer.parseInt(penaltyCredit));
            totalPaymentCount.setText(penaltyCredit + " Coin");
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

    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
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
            case R.id.map_button:
                startActivity(new Intent(BikeUsingActivity.this, MapRequests.class));
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

        if (penaltyCredit != null) {
            chronometer.setBase(SystemClock.elapsedRealtime() - (30 * 60000));
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

                getRequestForFinish();

                getRequestForUpdateCredit();

                Toast.makeText(getApplicationContext(), total_coin.toUpperCase() + " COINS ARE DEDUCTED FROM YOUR TOTAL COINS", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BikeUsingActivity.this, ReportsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("earnCredit", earnCredit);
                startActivity(intent);
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void getRequestForUpdateCredit() {
        MyAsyncForUpdateCredit creditUpdater = new MyAsyncForUpdateCredit();
        try {
            creditUpdater.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getRequestForFinish() {
        // REST API
        MyAsyncBikeId async = new MyAsyncBikeId();
        try {
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getRequestForHotpoints() {
        // REST API
        MyAsyncForGetHotpoints async = new MyAsyncForGetHotpoints();
        try {
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class MyAsyncBikeId extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", username);
                jsonObject.put("bike_id", bikeId.getText());
                jsonObject.put("lat", lat);
                jsonObject.put("long", lng);
                jsonObject.put("bike_time", bikeTime);
                //jsonObject.put("bike_km", 0);
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
                String message = jObj.getString("message"); // request sonucu donen bisikletin durum mesaji
                String status = jObj.getString("status"); // request sonucu donen bisikletin statusu
                earnCredit = jObj.getInt("credit"); // kiralama sonunda bisiklet hotpoints bolgesindeyse geri kazandigi kredi miktari

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

    class MyAsyncForGetHotpoints extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {
            String message;
            String status;

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("hotpoints", true);
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
                message = jObj.getString("message");
                status = jObj.getString("status");
                JSONArray jsonArray = jObj.getJSONArray("hotpoints");

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        double radius = obj.getDouble("radius");
                        String point_name = obj.getString("point_name");
                        int frequency = obj.getInt("frequency");
                        double latitude = obj.getDouble("lat");
                        double longitude = obj.getDouble("long");

                        Hotspots hotpoint = new Hotspots(radius, point_name, frequency, latitude, longitude);
                        hotpointList.add(hotpoint);
                    }
                }

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
