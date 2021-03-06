package com.final_project.bikepass_android.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.final_project.bikepass_android.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
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
    TextView recPwd;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private String userName,passWord;
    ProgressBar pb;
    String emailforecovery;
    String answerforecovery;
    String myurl;
    String usernamerecovery;
    String session;
    String rec_question;
    GoogleSignInClient mGoogleSignInClient;
    Button button;
    int RC_SIGN_IN=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myurl="https://Bikepass.herokuapp.com/API/app.php";
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login);
        buttonLogin.setOnClickListener(this);
        rememberMe = findViewById(R.id.saveLoginCheckBox);
        rememberMe.setOnClickListener(this);
        recPwd=findViewById(R.id.resetPawd);
        recPwd.setOnClickListener(this);
        pb=new ProgressBar(this);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


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
        loginPrefsEditor.apply();
        loginPrefsEditor.commit();
    }

    public void clearStrorage(){
        loginPrefsEditor.clear();
        loginPrefsEditor.commit();
    }
    public void goToSignupActivity(View view){
        signIn();
      /*  Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent); */
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
       /*  if(account!=null){
            Log.i("hello","hello");
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        }  */
        // updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);

            Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("error", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
/*
    @Override
    protected void onStart() {


        super.onStart();
    }
*/

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
                async.execute(myurl).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class MyAsyncLogin extends AsyncTask<String,Void,String> {

        HttpURLConnection connection;
        OutputStreamWriter request = null;
        InputStreamReader isr;

        URL url = null;
        String response = null;
        @Override
        protected String doInBackground(String[]urls) {


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
            case R.id.resetPawd:
                showRecoverPasswordDialog("Please enter your username so we can ask your security question","Username");
                break;

        }
    }

    private void showRecoverPasswordDialog(String title, final String hint) {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(title);
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText value=new EditText(this);
        value.setHint(hint);
        value.setPadding(20,40,40,20);
        value.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        linearLayout.addView(value);
        linearLayout.setPadding(40,20,20,10);

        builder.setView(linearLayout);
        builder.setPositiveButton("Confirm", null);
        builder.setNegativeButton("Cancel",null);
        runOnUiThread(new Runnable() {
            public void run() {
                //builder.create().show();
                final AlertDialog alertDialog =builder.show();
                Button p=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(hint.equals("Username")) {
                            String rec_value = value.getText().toString().trim();
                            String status=execute_recovery(rec_value,myurl);
                            if(status.equals("1")){
                                alertDialog.dismiss();
                                showRecoverPasswordDialog(rec_question,"Answer");
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "There is no such a user!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            String rec_value = value.getText().toString().trim();
                            if(rec_value.equals(answerforecovery)) {
                                String status=sendRecoveryEmail(emailforecovery,"https://Bikepass.herokuapp.com/recoverymail.php");
                                alertDialog.dismiss();
                                setProgressDialog("Sending email to "+ emailforecovery,"Email sent","Error occured when sending email!",status);

                            }else
                            {
                                Toast.makeText(getApplicationContext(), "Answer doesnt match!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
                Button n=alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        });
    }
    private void showChangePasswordDialog(String title) {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(title);
        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final EditText value=new EditText(this);
        value.setHint("Code");
        value.setPadding(50,60,100,20);
        value.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        linearLayout.addView(value);

        final EditText passwordvalue=new EditText(this);
        passwordvalue.setHint("Password");
        passwordvalue.setPadding(50,80,100,20);
        passwordvalue.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        linearLayout.addView(passwordvalue);

        final EditText passwordagain=new EditText(this);
        passwordagain.setHint("Password Again");
        passwordagain.setPadding(50,80,100,20);
        passwordagain.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        linearLayout.addView(passwordagain);

        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);
        builder.setPositiveButton("Confirm", null);
        builder.setNegativeButton("Cancel",null);
        runOnUiThread(new Runnable() {
            public void run() {
                //builder.create().show();
                final AlertDialog alertDialog =builder.show();
                Button p=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!passwordvalue.getText().toString().equals(passwordagain.getText().toString())){
                            Toast.makeText(getApplicationContext(), "Password doesnt match!", Toast.LENGTH_SHORT).show();
                        }

                        else if(!value.getText().toString().equals(session)){
                            Toast.makeText(getApplicationContext(), "The code we have sent you doesnt match!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            MyAsyncChangePassword asynccp=new MyAsyncChangePassword(usernamerecovery,password.getText().toString());
                            try {
                                String result=asynccp.execute(myurl).get();
                                setProgressDialog("Password is changing ","Your password have been changed succesfully","An error occurred!",result);
                            } catch (ExecutionException e) {

                                e.printStackTrace();
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                            alertDialog.dismiss();
                        }
                    }
                });
                Button n=alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        });
    }

    private String execute_recovery(String usernamerec,String url) {

        MyAsyncRecovery async = new MyAsyncRecovery(usernamerec);
        String result="";
        try {
            result=async.execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String sendRecoveryEmail(String emailforecovery,String url) {

        MyAsyncRecoveryEmail async = new MyAsyncRecoveryEmail(emailforecovery);
        String result="";
        try {

            result=async.execute(url).get();

        } catch (ExecutionException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
      return result;
    }

    class MyAsyncRecovery extends AsyncTask<String,Void,String> {

        String username;

        public MyAsyncRecovery(String usernamerec)
        {
            this.username=usernamerec;
            usernamerecovery=usernamerec;
        }
        @Override
        protected String doInBackground(String[]urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonRecData = new JSONObject();
            try {
                jsonRecData.put("usernamerec",this.username);

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
                request.write(String.valueOf(jsonRecData));
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                // Response from server after recovery process will be stored in response variable.
                response = sb.toString().trim();
                String jsonString=response;
                JSONObject jObj = new JSONObject(jsonString);
                JSONArray data = jObj.getJSONArray("data");
                final String message = jObj.getString("message");
                String status=jObj.getString("status");
                JSONObject values = data.getJSONObject(0);
                emailforecovery=values.getString("email");
                answerforecovery=values.getString("answer");
                if(jObj.getString("status").equals("0")) {
                   /* runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }else if(status.equals("1")){
                    rec_question=values.getString("question");
                   // showRecoverPasswordDialog(values.getString("question"),"Answer");
                }
                isr.close();
                reader.close();
                return status;
            } catch (IOException e) {
                // Error
                return "Failure sending";
            } catch (JSONException e) {
                e.printStackTrace();
                return "Failure sending";

            }
        }
    }


    class MyAsyncChangePassword extends AsyncTask<String,Void,String> {

        String username;
        String changedpassword;
        public MyAsyncChangePassword(String username, String password){
            this.username=username;
            this.changedpassword=password;
        }
        @Override
        protected String doInBackground(String[]urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonRecData = new JSONObject();
            try {
                jsonRecData.put("usernamerecovery",this.username);
                jsonRecData.put("passwordrecovery",this.changedpassword);
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
                request.write(String.valueOf(jsonRecData));
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                //Response from server after recovery process will be stored in response variable.
                response = sb.toString().trim();
                JSONObject jObj = new JSONObject(response);
                final String status = jObj.getString("status");
                isr.close();
                reader.close();
                if(status.equals("0"))
                    return "Success";
                else
                    return "Failure";
            } catch (IOException e) {
                // Error
                e.printStackTrace();
                return "Failure sending";
            } catch (JSONException e) {
                return "Failure sending";
                //  e.printStackTrace();
            }

        }
    }

    class MyAsyncRecoveryEmail extends AsyncTask<String,Void,String> {

        String email;
        public MyAsyncRecoveryEmail(String email){
            this.email=email;
        }
        @Override
        protected String doInBackground(String[]urls) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
              RandomString  s = new RandomString(8);
              session=s.nextString();
            }
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonRecData = new JSONObject();
            try {
                jsonRecData.put("recovery_email",this.email);
                jsonRecData.put("recovery_username",usernamerecovery);
                jsonRecData.put("recovery_code",session);
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
                request.write(String.valueOf(jsonRecData));
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                //Response from server after recovery process will be stored in response variable.
                response = sb.toString().trim();
                JSONObject jObj = new JSONObject(response);
                final String status = jObj.getString("status");
                isr.close();
                reader.close();
               return status;
            } catch (IOException e) {
                // Error
                e.printStackTrace();
                return "Failure sending";
            } catch (JSONException e) {
                return "Failure sending";
                //  e.printStackTrace();
            }

        }
    }
    public void setProgressDialog(String message, final String successmesage, final String errormessage, final String result) {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, 60, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText(message);
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        final AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    if(result.equals("1")) {
                        Toast.makeText(getApplicationContext(), successmesage, Toast.LENGTH_SHORT).show();
                        showChangePasswordDialog("Please enter the code we sent you and create a new password");
                    }else if(result.equals("Success"))
                    {
                        Toast.makeText(getApplicationContext(), successmesage, Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), errormessage, Toast.LENGTH_SHORT).show();
                }
            }
        };
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 2000);
    }

}