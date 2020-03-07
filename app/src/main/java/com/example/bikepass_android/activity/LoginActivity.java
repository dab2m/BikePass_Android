package com.example.bikepass_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dilan on 22.01.2020
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonLogin;
    EditText username;
    EditText password;
    CheckBox rememberMe;
    Intent intent;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private String userName,passWord;


    public void setView(String userName,String passWord){

     username.setText(userName);
     password.setText(passWord);
     Toast.makeText(getApplicationContext(), "Account is created! Welcome to BikePass "+userName+"!", Toast.LENGTH_SHORT).show();
     setStorage(userName,passWord);

    }
     public void setStorage(String username,String password){

         loginPrefsEditor.putBoolean("saveLogin", true);
         loginPrefsEditor.putString("username", username);
         loginPrefsEditor.putString("password", password);
         loginPrefsEditor.commit();
     }

     public void clearStrorage(){
         loginPrefsEditor.clear();
         loginPrefsEditor.commit();
     }
    public void goToSignupActivity(View view){

        Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent);
    }

    public void goToReportsActivity(String message){
        Intent intent=new Intent(getApplicationContext(),ReportsActivity.class);
        intent.putExtra("message",message);
        startActivity(intent);
    }

    public void tryLogin(View view){

        if (username.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Username cant be empty", Toast.LENGTH_SHORT).show();
        else if (password.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Password cant be empty", Toast.LENGTH_SHORT).show();
        else {
            userName=username.getText().toString();
            passWord=password.getText().toString();
            MyAsyncLogin async = new MyAsyncLogin();
            try {
                async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login);
        buttonLogin.setOnClickListener(this);
        rememberMe = findViewById(R.id.saveLoginCheckBox);
        rememberMe.setOnClickListener(this);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        Intent intent=getIntent();
        if(intent.getStringExtra("username")!=null && intent.getStringExtra("password")!=null)
            setView(intent.getStringExtra("username"),intent.getStringExtra("password"));

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            username.setText(loginPreferences.getString("username", ""));
            password.setText(loginPreferences.getString("password", ""));
            rememberMe.setChecked(true);
        }
    }

    class MyAsyncLogin extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[]urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonLoginData = new JSONObject();
            try {
                jsonLoginData.put("username",userName);
                jsonLoginData.put("password",passWord);
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
                request.write(String.valueOf(jsonLoginData));
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
                JSONObject jObj = new JSONObject(response);
                final String message = jObj.getString("message");
                String status = jObj.getString("status");
                ArrayList<String> statuscodeListForLogin = new ArrayList<>(Arrays.asList("1", "2"));
                if(status.equals("0")) {
                    setStorage(userName,passWord);
                    goToReportsActivity(message);
                }else if(statuscodeListForLogin.contains(status)){
                    clearStrorage();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
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
            case R.id.login:
                tryLogin(v);
                break;
            case R.id.saveLoginCheckBox:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(username.getWindowToken(), 0);

                userName = username.getText().toString();
                passWord = password.getText().toString();

                if (rememberMe.isChecked()) {
                   setStorage(userName,passWord);
                } else {
                   clearStrorage();
                }
                break;
        }
    }

}
