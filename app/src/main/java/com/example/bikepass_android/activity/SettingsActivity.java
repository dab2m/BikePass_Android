package com.example.bikepass_android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bikepass_android.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by Berk on 11.02.2020
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button bCardDetails;
    Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent=getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bCardDetails = (Button)findViewById(R.id.bCard);
        bCardDetails.setOnClickListener(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
    }

        public  void logout(View view){
            SharedPreferences sharedpreferences = getSharedPreferences("loginPrefs",MODE_PRIVATE);
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
            case R.id.bCard:
                startActivity(new Intent(this, CardDetailsActivity.class));
                //Toast.makeText(getApplicationContext(), "You need to define credit card as well for payment", Toast.LENGTH_LONG).show();
                break;
           case R.id.btnLogout:
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
}