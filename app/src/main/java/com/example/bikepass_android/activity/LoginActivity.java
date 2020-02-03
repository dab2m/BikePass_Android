package com.example.bikepass_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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

/**
 * Created by Dilan on 22.01.2020
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonLogin;
    EditText username;
    EditText password;
    CheckBox rememberMe;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private String userName,passWord;


    public void setView(String userName,String passWord){

     username.setText(userName);
     password.setText(passWord);
     Toast.makeText(getApplicationContext(), "Welcome BikePass!", Toast.LENGTH_SHORT).show();
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

    public void tryLogin(){

        if (username.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Username cant be empty", Toast.LENGTH_SHORT).show();
        else if (password.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Password cant be empty", Toast.LENGTH_SHORT).show();
        else {
            MyAsyncLogin async = new MyAsyncLogin();
            try {
                String result = async.execute("http://10.100.10.69/Bitirme/localWeb/userLogin.php").get();
                Log.i("text:", result);
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
        protected String doInBackground(String[] strings) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            String parameters = "username=" + userName + "&password=" + passWord ;

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
                Log.i("info:",response);
                if(response.equals("1")) {
                    setStorage(userName,passWord);
                    startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                }else {
                    clearStrorage();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Username or password is wrong", Toast.LENGTH_SHORT).show();
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
            }

            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                tryLogin();
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
