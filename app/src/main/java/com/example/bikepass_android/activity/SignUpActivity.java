package com.example.bikepass_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bikepass_android.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener {

    EditText username;
    EditText password;
    EditText mail;
    Button buton;
    String code= "";
    String userName= "";
    String passwordUser="";
    String email="";
    Intent intent=null;
    public void backToMainActivity(View view){

        intent=new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        buton = findViewById(R.id.signupButton);
        buton.setOnClickListener(this);
    }
     public void setUserInfo(){

         intent=new Intent(getApplicationContext(), LoginActivity.class);
         intent.putExtra("username",userName);
         intent.putExtra("password",passwordUser);
         startActivity(intent);

     }
    @Override
    public void onClick(View view) {

        username = findViewById(R.id.usernamesignup);
        userName=username.getText().toString();
        password = findViewById(R.id.passwordsignup);
        passwordUser=password.getText().toString();
        mail = findViewById(R.id.emailsignup);
        email=mail.getText().toString();


        //Log.i("info", "username:" + username.getText().toString());
        //Log.i("info", "password:" + password.getText().toString());
        //Log.i("info", "mail:" + mail.getText().toString());


        if (username.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Username cant be empty", Toast.LENGTH_SHORT).show();
        else if (mail.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Email cant be empty", Toast.LENGTH_SHORT).show();
        else if (password.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Password cant be empty", Toast.LENGTH_SHORT).show();

        else {
            MyAsync async = new MyAsync();
            try {
                String result = async.execute("http://10.100.10.63/Bitirme/localWeb/registerUser.php").get();
                Log.i("text:", result);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    class MyAsync extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[] strings) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            String parameters = "username=" + userName + "&password=" + passwordUser + "&email=" + email;

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
                response = sb.toString().trim();
                if(response.equals("1")) {
                    setUserInfo();
                }else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Username or password is already used", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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




