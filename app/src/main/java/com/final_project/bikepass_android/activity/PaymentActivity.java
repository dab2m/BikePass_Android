package com.final_project.bikepass_android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.final_project.bikepass_android.R;

/**
 * Created by mustafatozluoglu on 20.04.2020
 */
public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView justACycle_cardView;
    private CardView procycler_cardView;
    private CardView cycleAddict_cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        justACycle_cardView = (CardView) findViewById(R.id.justACycle_cardView);
        procycler_cardView = (CardView) findViewById(R.id.procycler_cardView);
        cycleAddict_cardView = (CardView) findViewById(R.id.cycleAddict_cardView);

        justACycle_cardView.setOnClickListener(this);
        procycler_cardView.setOnClickListener(this);
        cycleAddict_cardView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.justACycle_cardView:
                //TODO: pay 2.5tl
                break;
            case R.id.procycler_cardView:
                //TODO: pay 5tl
                break;
            case R.id.cycleAddict_cardView:
                //TODO: pay 10tl
                break;
        }

    }
}
