package com.example.bikepass_android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
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
 * Created by Berk on 27.02.2020
 */
public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton bRentBike;
    ImageButton bLocation;
    ImageButton bSettings;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Intent intent=getIntent();
        Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bRentBike = (ImageButton)findViewById(R.id.map);
        bSettings = (ImageButton)findViewById(R.id.b5);
        bRentBike.setOnClickListener(this);
        bSettings.setOnClickListener(this);

        LeaderBoard async = new LeaderBoard("Ankara");
        try {
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

     class LeaderBoard extends AsyncTask<String, String, String> {

        String parameter;

         public LeaderBoard(String parameter) {
             super();
             this.parameter=parameter;
         }

         @Override
         protected String doInBackground(String[]urls) {

             HttpURLConnection connection;
             OutputStreamWriter request = null;

             URL url = null;
             String response = null;
             JSONObject jsonLocData = new JSONObject();
             try {
                 jsonLocData.put("location",this.parameter);

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
                 request.write(String.valueOf(jsonLocData));
                 request.flush();
                 request.close();
                 String line = "";
                 InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                 BufferedReader reader = new BufferedReader(isr);
                 StringBuilder sb = new StringBuilder();
                 while ((line = reader.readLine()) != null) {
                     sb.append(line + "\n");
                 }
                 // Response from server after leaderboard process will be stored in response variable.
                 response = sb.toString().trim();
                 JSONObject jObj = new JSONObject(response);
                 isr.close();
                 reader.close();
                 return "Success";
             } catch (IOException e) {
                 // Error
                 e.printStackTrace();
             } catch (JSONException e) {
                 e.printStackTrace();
             }

             return null;
         }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map:
                startActivity(new Intent(this, RentBikeActivity.class));
                break;
            case R.id.b5:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }
}