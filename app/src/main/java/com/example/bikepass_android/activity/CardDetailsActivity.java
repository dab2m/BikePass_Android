package com.example.bikepass_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * Created by Berk on 03.02.2020
 */
public class CardDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etCardOwnerName;
    EditText etCardNumber;
    EditText etExpDate;
    EditText etCCV;
    Button bApprove;
    Button bReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        Intent intent=getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

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