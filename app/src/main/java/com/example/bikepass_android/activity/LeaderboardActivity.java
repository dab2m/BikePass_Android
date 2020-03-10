package com.example.bikepass_android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import com.example.bikepass_android.network.JSONParser;

import org.json.JSONArray;
import org.w3c.dom.Text;

/**
 * Created by Berk on 27.02.2020
 */
public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {
    JSONParser jsonParser;
    ImageButton bRentBike;
    ImageButton bLocation;
    ImageButton bSettings;
    Button bworldleaderboard;
    TextView content;
    LeaderBoard async=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Intent intent=getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bRentBike = (ImageButton)findViewById(R.id.bikes);
        bSettings = (ImageButton)findViewById(R.id.settings);
        content=(TextView)findViewById(R.id.textViewContent);
        content.setText("In Ankara");
        bworldleaderboard=findViewById(R.id.worldleaderboard);
        bworldleaderboard.setText("World Leaderboard");
        bworldleaderboard.setOnClickListener(this);
        bRentBike.setOnClickListener(this);
        bSettings.setOnClickListener(this);

        async = new LeaderBoard("Ankara");
        try {
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void getWorldRequest(View v) {

        //LeaderBoard async = new LeaderBoard("World");
      /*  if(async.getParameter().equals("Ankara")) {
            async.setParameter("World");
        }
        else {
            async.setParameter("Ankara");
        }
            try {
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get(); */
            if(content.getText().toString().equals("In Ankara")) {
                content.setText("In World");
            }
            else {
                content.setText("In Ankara");
            }
            if(bworldleaderboard.getText().toString().equals("World Leaderboard")) {
                bworldleaderboard.setText("City Leaderboard");
            }
            else {
                bworldleaderboard.setText("World Leaderboard");
            }
    /*    } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } */
    }
     class LeaderBoard extends AsyncTask<String, String, String> {

        String parameter;

         public LeaderBoard(String parameter) {
             super();
             this.parameter=parameter;
         }
         public void setParameter(String parameter){
             this.parameter=parameter;
         }
          public String getParameter(){
             return this.parameter;
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
                 Log.i("location::::::",this.parameter);

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

                 jsonParser = new JSONParser();
                 String jsonString = response;
                 ListView mlistView=findViewById(R.id.list_view);
                 ArrayList <UsageData> usage_data=new ArrayList<UsageData>();
                // Log.i("JSON_RESPONSE", jsonString);
                 if (jsonString != null && !jsonString.contains("Could not fetch recipes")) {
                     try {
                         JSONObject jsonObject = new JSONObject(jsonString);
                         JSONArray bike_users = jsonObject.getJSONArray("bike_users");
                         for (int i = 0; i < bike_users.length(); i++) {
                             JSONObject values = bike_users.getJSONObject(i);
                             String user_name = values.getString("user_name");
                             int bike_usage =Integer.parseInt( values.getString("bike_using_time"));
                             UsageData data=new UsageData(bike_usage,user_name);
                             usage_data.add(data);

                         }
                         UsageDataListAdapter adapter=new UsageDataListAdapter(LeaderboardActivity.this,R.layout.adapter_view_layout,usage_data);
                         mlistView.setAdapter(adapter);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 } else {
                     Log.d("JSON_RESPONSE", "Empty page resource!");
                 }



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
            case R.id.bikes:
                startActivity(new Intent(this, RentBikeActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.worldleaderboard:
                getWorldRequest(v);
                break;
        }
    }

}