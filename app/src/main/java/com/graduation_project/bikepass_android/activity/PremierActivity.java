package com.graduation_project.bikepass_android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import com.graduation_project.bikepass_android.R;

/**
 * Created by Mustafa on 10.02.2020
 */
public class PremierActivity extends AppCompatActivity {
    private static int CAMERA_REQUEST_CODE = 100;
    GoogleSignInClient mGoogleSignInClient;
    Button button;
    int RC_SIGN_IN=0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premier);

        button=findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.button2:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        SharedPreferences sharedpreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (!sharedpreferences.getString("username", "").equals("")){
            //has login
            startActivity(new Intent(this, ReportsActivity.class));
            //must finish this activity (the premier activity will not be shown when click back in reports activity)
            finish();
        }

        // Request for camera permission from user when app is starting
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

            Intent intent=new Intent(PremierActivity.this,SignUpActivity.class);
            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("error", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

 /*    @Override
    protected void onStart() {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            startActivity(new Intent(PremierActivity.this,SignUpActivity.class));
        }
        // updateUI(account);
        super.onStart();
    } */


    /*public void toSignUp(View view){
        //Intent signUpIntent = new Intent(this,SignUpActivity.class);
        //startActivity(signUpIntent);
        Intent googleIntent=new Intent(this,SigninWithGoogle.class);
        startActivity(googleIntent);
    }*/

    public void toSignIn(View view ){
        Intent loginIntent=new Intent(this,LoginActivity.class);
        startActivity(loginIntent);
    }

}
