package com.example.bikepass_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bikepass_android.R;

/**
 * Created by Berk on 03.02.2020
 */
public class CardDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etCardOwnerName;
    EditText etCardNumber;
    EditText etExpDate;
    EditText etCCV;
    Button bApprove;
    Button bReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        Intent intent=getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }
}