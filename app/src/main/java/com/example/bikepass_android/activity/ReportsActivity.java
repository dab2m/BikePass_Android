package com.example.bikepass_android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bikepass_android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by Berk on 03.02.2020
 */
public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLeaderboard;
    ImageButton bRentBike;
    ImageButton bGoMap;
    ImageButton bSettings;
    TextView view;
    GridLayout gl;

    private String time = null;
    private String bikeId = null;
    private String user_name;

    TextView totalTimeCount;
    TextView totalRecoveryCount;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Intent intent = getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bLeaderboard = (Button) findViewById(R.id.worldleaderboard);
        bRentBike = (ImageButton) findViewById(R.id.returnbikes);
        bGoMap = (ImageButton) findViewById(R.id.map);
        bSettings = (ImageButton) findViewById(R.id.settings);
        gl = (GridLayout) findViewById(R.id.grid_layout);


        totalTimeCount = (TextView) findViewById(R.id.totalTimeCount);
        totalRecoveryCount = (TextView) findViewById(R.id.totalRecoveryCount);

        bLeaderboard.setOnClickListener(this);
        bRentBike.setOnClickListener(this);
        bGoMap.setOnClickListener(this);
        bSettings.setOnClickListener(this);
        view = findViewById(R.id.textview);
        SharedPreferences sharedpreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        user_name = sharedpreferences.getString("username", "");
        view.setText("Welcome back, " + user_name);


        // REST API
        MyAsync async = new MyAsync();
        String time = null;
        try {
            time = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        totalTimeCount.setText(time + " sec");
        double _time = Double.parseDouble(time);
        double co2 = (_time / 180.0) * 0.271;
        DecimalFormat df = new DecimalFormat("#.###");
        String co2String = df.format(co2);
        totalRecoveryCount.setText(co2String + " kg");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.worldleaderboard:
                startActivity(new Intent(this, LeaderboardActivity.class));
                break;
            case R.id.returnbikes:
                SharedPreferences preferences = getSharedPreferences("username", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", user_name);
                editor.commit();
                Intent intent1 = new Intent(this, RentBikeActivity.class);
                Intent intent2 = new Intent(this, BikeUsingActivity.class);
                intent2.putExtra("bikeId", bikeId);
                intent2.putExtra("username", user_name);
                if (time == null)
                    startActivity(intent1);
                else
                    startActivity(intent2);
                break;
            case R.id.map:
                goToMapActivity(v);
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    public void postMethod(final String requestUrl) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(requestUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    JSONObject jsonParam = new JSONObject();
                    JSONArray tagsArray = new JSONArray(Arrays.asList());
                    //jsonParam.put("", );
                    Log.i("JSON", jsonParam.toString());
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    class MyAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {
            String time;

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", user_name);
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
                time = jObj.getString("bike using time"); // request sonucu donen bisikletin durum mesaji


                isr.close();
                reader.close();


                Log.i("time", time);

                return time;
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