package com.example.bikepass_android.activity;


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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dilan on 18.01.2020
 */
public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener {

    EditText username;
    EditText password;
    EditText mail;
    Button buton;
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

        //Log.i("info","setUserInfo");

         intent=new Intent(getApplicationContext(), LoginActivity.class);
         intent.putExtra("username",userName);
         intent.putExtra("password",passwordUser);
         intent.putExtra("email",email);
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

        if (username.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Username cant be empty", Toast.LENGTH_SHORT).show();
        else if (mail.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Email cant be empty", Toast.LENGTH_SHORT).show();
        else if (password.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Password cant be empty", Toast.LENGTH_SHORT).show();
        else {
            MyAsyncSignup async = new MyAsyncSignup();
            try {
                String result = async.execute("http://Bikepass.herokuapp.com/API/app.php").get();
                Log.i("text:", result);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    class MyAsyncSignup extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[] strings) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonRegisterData = new JSONObject();
            try {
                jsonRegisterData.put("username",userName);
                jsonRegisterData.put("password",passwordUser);
                jsonRegisterData.put("email",email);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(String.valueOf(jsonRegisterData));
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                // Response from server after register process will be stored in response variable.
                response = sb.toString().trim();
                JSONObject jObj = new JSONObject(response);
                final String message = jObj.getString("message");
                String status = jObj.getString("status");
                ArrayList<String> statuscodeListForRegister = new ArrayList<>(Arrays.asList("1", "2", "3","4","5"));
                if(status.equals("0")) {
                    setUserInfo();
                }else if(statuscodeListForRegister.contains(status)  ) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                isr.close();
                reader.close();
                return "Success";
            } catch (IOException e) {
                // Error
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    }




