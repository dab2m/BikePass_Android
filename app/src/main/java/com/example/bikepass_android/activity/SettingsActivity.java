package com.example.bikepass_android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bikepass_android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
 * Created by Berk on 11.02.2020
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private String username;

    Button bCardDetails;
    Button bLogout;
    Button bReject;
    Button bApprove;

    TextView totalTimeCount;
    TextView totalRecoveryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();

        totalTimeCount = (TextView) findViewById(R.id.totalTimeCount);
        totalRecoveryCount = (TextView) findViewById(R.id.totalRecoveryCount);

        bCardDetails = (Button) findViewById(R.id.bCard);
        bCardDetails.setOnClickListener(this);
        bLogout = findViewById(R.id.bLogout);
        bLogout.setOnClickListener(this);
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        username = prefs.getString("username", "UNKNOWN");
        final String password = prefs.getString("password", "UNKNOWN");
        final String email = prefs.getString("email", "UNKNOWN");


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

    public void logout(View view) {
        SharedPreferences sharedpreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bReject:
                break;
            case R.id.bApprove:
                break;
            case R.id.bCard:
                startActivity(new Intent(this, CardDetailsActivity.class));
                //Toast.makeText(getApplicationContext(), "You need to define credit card as well for payment", Toast.LENGTH_LONG).show();
                break;
            case R.id.bLogout:
                logout(v);
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

    public void restQueryForTimeAndCO() {

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
                jsonObject.put("username", username);
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