package com.example.bikepass_android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bikepass_android.R;
public class ReportBikeDamage extends AppCompatActivity {
    String user_name;
    String user_addr;
    String reason;
    LinearLayout clickedLayout;
    TextView clickedText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bike_damage);
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
                startActivity(new Intent(ReportBikeDamage.this, ScanQrCode.class));
            }
        });

        final LinearLayout qrcodelayout=findViewById(R.id.qrcodelayout);
        final TextView qrcodetext=findViewById(R.id.qrcodetext);
        qrcodetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=qrcodetext.getText().toString();
                clickedText=qrcodetext;
                clickedLayout=qrcodelayout;
                qrcodelayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                qrcodetext.setTextColor(Color.WHITE);

            }
        });
        final LinearLayout tekeripatlamislayout=findViewById(R.id.tekeripatlamislayout);
        final TextView tekerpatlamistext=findViewById(R.id.tekerpatlamistext);
        tekerpatlamistext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=tekerpatlamistext.getText().toString();
                clickedLayout=tekeripatlamislayout;
                clickedText=tekerpatlamistext;
                tekeripatlamislayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                tekerpatlamistext.setTextColor(Color.WHITE);
            }
        });
        final LinearLayout kilidibozulmuslayout=findViewById(R.id.kilidibozulmuslayout);
        final TextView kilidibozulmustext=findViewById(R.id.kilidibozulmustext);
        kilidibozulmustext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=kilidibozulmustext.getText().toString();
                clickedLayout=kilidibozulmuslayout;
                clickedText=kilidibozulmustext;
                kilidibozulmuslayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                kilidibozulmustext.setTextColor(Color.WHITE);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}


