package com.example.bikepass_android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikepass_android.R;
public class ReportBikeDamage extends AppCompatActivity {
    String user_name;
    String user_addr;
    String reason;
    LinearLayout clickedLayout;
    TextView clickedText;
    private static final int CAMERA_REQUEST = 1888;
    ImageView imageView;
    Uri image_uri;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PERMISSION_CODE=1000;
    private static final int IMAGE_CAPTURE_CODE=1001;
    LinearLayout photoButton;
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
        photoButton=findViewById(R.id.photolayout);
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {//If system os is >=marshmallow ,request runtime permission
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                        //Permission not enabled,request it

                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //Showpopuptorequest permissions
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        openCamera();
                    }
                }
                    else{
                        //systemos<marshmallow
                        openCamera();
                    }

               // startActivity(new Intent(ReportBikeDamage.this,PhotoActivity.class));
              /*  if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 0);
                } */
            }
        });
        imageView=findViewById(R.id.putphoto);
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

       setListenersForButtons();
    }

    public void openCamera(){

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pıcture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //Camera intent
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);

    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
       //this method is called,when user presses Aloow or Deny from permission request poup
        switch(requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    Toast.makeText(this,"Permission denied..",Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULT_OK )
        {
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            TextView damagephoto=findViewById(R.id.damagephoto);
            damagephoto.setVisibility(View.INVISIBLE);
            //imageView.setImageBitmap(photo);
            imageView.setImageURI(image_uri);
            //photoButton.addView(imageView);
        }
    }

    public void setListenersForButtons(){

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
 /*   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            TextView damagephoto=findViewById(R.id.damagephoto);
            damagephoto.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(photo);
            //photoButton.addView(imageView);
        }
    }  */
    @Override
    public void onBackPressed() {

    }
}


