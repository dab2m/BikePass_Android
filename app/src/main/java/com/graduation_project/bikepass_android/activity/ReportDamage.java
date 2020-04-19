package com.graduation_project.bikepass_android.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.graduation_project.bikepass_android.R;

public class ReportDamage extends AppCompatActivity {
    Dialog infodialog;
    String user_name;
    String user_addr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_damage);
        infodialog = new Dialog(this);
        Intent intent=getIntent();
        if(intent.getStringExtra("username")!=null && intent.getStringExtra("useraddr")!=null) {
            user_name=intent.getStringExtra("username");
            user_addr=intent.getStringExtra("useraddr");
        }

        LinearLayout parking_button=findViewById(R.id.parking_button);
        parking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showParkingProblemDialog();
            }
        });
        LinearLayout damage_button=findViewById(R.id.damage_button);
        damage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDamageProblemDialog();
            }
        });

    }
    public void showParkingProblemDialog(){
        Intent intent=new Intent(getApplicationContext(), ReportParkingProblem.class);
        intent.putExtra("username",user_name);
        intent.putExtra("useraddr",user_addr);
        startActivity(intent);

    }

    public void showDamageProblemDialog(){
        Intent intent=new Intent(getApplicationContext(), ReportBikeDamage.class);
        intent.putExtra("username",user_name);
        intent.putExtra("useraddr",user_addr);
        startActivity(intent);

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ReportDamage.this, MapActivity.class));
    }

}
