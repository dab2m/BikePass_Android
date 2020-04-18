package com.final_project.bikepass_android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.final_project.bikepass_android.R;
public class ReportParkingProblem extends AppCompatActivity {
    String user_name;
    String user_addr;
    String reason;
    LinearLayout clickedLayout;
    TextView clickedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_parking_problem);
        Intent intent=getIntent();
        if(intent.getStringExtra("username")!=null && intent.getStringExtra("useraddr")!=null) {
            user_name=intent.getStringExtra("username");
            user_addr=intent.getStringExtra("useraddr");
        }
        TextView loc=findViewById(R.id.userlocation);
        loc.setText(user_addr);
        loc.setTextColor(Color.BLACK);
        ImageView arrow=findViewById(R.id.txtclose);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ReportDamage.class);
                intent.putExtra("username",user_name);
                intent.putExtra("useraddr",user_addr);
                startActivity(intent);
            }
        });

        LinearLayout qrscan=findViewById(R.id.qrscan);
        qrscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportParkingProblem.this, ScanQrCode.class));
            }
        });
        final LinearLayout erisilebilirliklayout=findViewById(R.id.erisilebilirliklayout);
        final TextView erisililebilirlik=findViewById(R.id.erisilebilirlik);
        erisililebilirlik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=erisililebilirlik.getText().toString();
                clickedText=erisililebilirlik;
                clickedLayout=erisilebilirliklayout;
                erisilebilirliklayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                erisililebilirlik.setTextColor(Color.WHITE);

            }
        });
        final LinearLayout ozelmulkiyetlayout=findViewById(R.id.ozelmulkiyetlayout);
        final TextView ozelmulkiyet=findViewById(R.id.ozelmulkiyet);
        ozelmulkiyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=ozelmulkiyet.getText().toString();
                clickedLayout=ozelmulkiyetlayout;
                clickedText=ozelmulkiyet;
                ozelmulkiyetlayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                ozelmulkiyet.setTextColor(Color.WHITE);
            }
        });

        final LinearLayout devrilmislayout=findViewById(R.id.devrilmislayout);
        final TextView devrilmis=findViewById(R.id.devrilmis);
        devrilmis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=devrilmis.getText().toString();
                clickedLayout=devrilmislayout;
                clickedText=devrilmis;
                devrilmislayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                devrilmis.setTextColor(Color.WHITE);
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

}
