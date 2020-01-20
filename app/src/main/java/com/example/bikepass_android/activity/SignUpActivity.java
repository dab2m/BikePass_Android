package com.example.bikepass_android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bikepass_android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener {

    EditText username;
    EditText password;
    EditText mail;
    Button buton;
    String code="";
    public void backToMainActivity(View view){

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        buton = findViewById(R.id.signupButton);
        buton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        username = findViewById(R.id.usernamesignup);
        password = findViewById(R.id.passwordsignup);
        mail = findViewById(R.id.emailsignup);


        Log.i("info", "username:" + username.getText().toString());
        Log.i("info", "password:" + password.getText().toString());
        Log.i("info", "mail:" + mail.getText().toString());

        MyAsync async=new MyAsync();
        try {
            String result=async.execute("http://192.168.1.24/Bitirme/localWeb/registerUser.php").get();
            Log.i("text:",result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class MyAsync extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[] strings) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            String parameters = "username=" + username + "&password=" + password + "$email=" + mail;

            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");

                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(parameters);
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                // Response from server after login process will be stored in response variable.
                response = sb.toString();
                Log.i("response", response);
                isr.close();
                reader.close();
                return "Success";
            } catch (IOException e) {
                // Error
            }

            return null;
        }
    }
    }




