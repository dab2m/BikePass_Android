package com.graduation_project.bikepass_android.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.graduation_project.bikepass_android.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dilan on 18.01.2020
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    GoogleSignInClient mGoogleSignInClient;
    EditText username;
    EditText password;
    EditText mail;
    EditText qanswer;
    Button button;
    String userName= "";
    String passwordUser="";
    String email="";
    Intent intent=null;
    Spinner spinner;
    String selectedItemText;
    String questionAnswer;
    String email_user;
    TextView signout;
    public void backToMainActivity(View view){

        intent=new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username = findViewById(R.id.usernamesignup);
        password = findViewById(R.id.passwordsignup);
        mail = findViewById(R.id.emailsignup);
        qanswer=findViewById(R.id.answerspinner);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this
        );
        if (acct != null) {
            Log.i("nulldegil ","nulldegil");
            email_user = acct.getEmail();
            setView();

        }
        else{
            Log.i("null","null");
        }

        button = findViewById(R.id.signupButton);
        button.setOnClickListener(this);
        spinner=findViewById(R.id.spinner);
        signout=findViewById(R.id.signoutButton);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.signoutButton:
                        signOut();
                        break;
                    // ...

                }
            }
        });

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Initializing a String Array
        String[] sequrityQuestions = new String[]{
                "Select sequrity question...",
               "Whats is your mothers maiden name?",
        " Whats is the name of your first pet?",
        " What was the first record/CD you first bought?",
        "Whats is your favorite place?",
        "Whats is the name of your last school you attended?",
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinneritem,sequrityQuestions){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                    tv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                   // Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Toast.makeText(getApplicationContext(), "Please select a question and answer ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setView(){
        username.setTextColor(Color.parseColor("#007DAE"));
        username.setText(email_user.substring(0,email_user.indexOf('@')));
        username.setEnabled(false);
        mail.setTextColor(Color.parseColor("#007DAE"));
        mail.setText(email_user);
        mail.setEnabled(false);

    }
    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(SignUpActivity.this,PremierActivity.class));
                finish();
            }
        });
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signupButton:
                trySignup(v);
                break;

        }
    }

    public void trySignup(View view) {

        userName=username.getText().toString();
        passwordUser=password.getText().toString();
        email=mail.getText().toString();
        questionAnswer=qanswer.getText().toString();

        if (username.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Username cant be empty", Toast.LENGTH_SHORT).show();
        else if (mail.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Email cant be empty", Toast.LENGTH_SHORT).show();
        else if (password.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Password cant be empty", Toast.LENGTH_SHORT).show();
        else if (qanswer.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Answer cant be empty", Toast.LENGTH_SHORT).show();
        else {
            MyAsyncSignup async = new MyAsyncSignup();
            try {
                String result = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // Initializing a String Array
    String[] plants = new String[]{
            "Select an item...",
            "California sycamore",
            "Mountain mahogany",
            "Butterfly weed",
            "Carrot weed"
    };


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
                jsonRegisterData.put("question",selectedItemText);
                jsonRegisterData.put("answer",questionAnswer);

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




